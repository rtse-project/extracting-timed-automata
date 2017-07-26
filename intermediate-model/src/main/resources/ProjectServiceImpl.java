package at.aau.service;

import at.aau.dao.*;
import at.aau.diff.common.Differ;
import at.aau.diff.maven.MavenBuildChange;
import at.aau.diff.maven.MavenBuildFileDiffer;
import at.aau.entity.*;
import at.aau.service.dto.CommitPair;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	private CommitDao commitDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private ChangeDao changeDao;
	@Autowired
	private WorkItemDao workItemDao;
	@Autowired
	private BuildDao buildDao;
	
	@Override
	@Transactional
	public void createProject(String projectName, List<Commit> commits) {
		Project p = new Project(null,projectName,new ArrayList<Commit>(),new ArrayList<WorkItem>());

		projectDao.save(p);
		for(Commit c : commits) {
//			Commit c = new Commit(null,s,p.getId(),new ArrayList<Change>());
			c.setProjectId(p.getId());
			c.setChanges(new ArrayList<Change>());
			p.getCommits().add(c);
			commitDao.save(c);
		}
	}

	@Override
	@Transactional
	public void generateChanges(Long projectId, File repoFolder) throws Exception {
		File tempDir = new File("poms/tmp");
		
		File gitDir = new File(repoFolder,".git");
		org.eclipse.jgit.lib.Repository repo = new FileRepository(gitDir);
		Git git = new Git(repo);
		
		ObjectReader readerOld = git.getRepository().newObjectReader();
		ObjectReader readerNew = git.getRepository().newObjectReader();
		
		DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
		diffFormatter.setRepository( git.getRepository() );
		
		List<Commit> commits=commitDao.findByProjectId(projectId);
		for (Commit commit : commits) {
			//find parent in the git by using the repoFolder and extract the changes
			System.out.println(commit);
			Commit freshCommit=commitDao.findOne(commit.getId());
			
			//TODO ORGANIZE OLD COMMIT ID (AND CHECK THE NEXT 3 lines)
			RevWalk revWalk = new RevWalk(git.getRepository());
			ObjectId comObject = git.getRepository().resolve(commit.getCommitId());
		    RevCommit revCommitNew = revWalk.parseCommit(comObject);
		    if(revCommitNew.getParentCount()>0) {
		    	RevCommit oldCommit = revCommitNew.getParent(0);
			    String oldCommitId = revCommitNew.getParent(0).getName();
			    String newCommidId=commit.getCommitId();
				
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				ObjectId oldTree = git.getRepository().resolve(commit.getCommitId()+"^{tree}");
				oldTreeIter.reset( readerOld, oldTree );
			    
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				ObjectId newTree = git.getRepository().resolve(oldCommitId +"^{tree}"); 
				newTreeIter.reset( readerNew, newTree );
					
				List<DiffEntry> entries = diffFormatter.scan(newTreeIter,oldTreeIter );
				for (DiffEntry entry : entries) {
					//TODO ADD CHANGES FROM CHANGE DISTILLER AND BUILD CHANGE DETECTOR
					//System.out.println("CHANGE: "+entry);
					Change c = new Change(null, oldCommitId, newCommidId, "", "", entry.getOldPath(), entry.getChangeType().toString(), ChangeType.FILE_CHANGE, "", "", commit.getId());
//					freshCommit.getChanges().add(c);
//					this.commitDao.save(freshCommit);
					this.changeDao.save(c);
					if(entry.getOldPath().endsWith(".java") && entry.getChangeType()==org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY) {
						File oldTmpFile = loadFileOfRepositoryToTemporaryFile(repo, repo.resolve(entry.getOldId().name()), new File(entry.getOldPath()).getName(),
								""/*+oldCommit.getCommitterIdent().getWhen().getTime()*/,oldCommit.getName(),".java", tempDir);
						File newTmpFile = loadFileOfRepositoryToTemporaryFile(repo, repo.resolve(entry.getNewId().name()), new File(entry.getNewPath()).getName(),
								""/*+revCommitNew.getCommitterIdent().getWhen().getTime()*/,revCommitNew.getName(),".java", tempDir);
						List<Change> compare = compareJava(oldTmpFile, newTmpFile, repo, oldCommitId, newCommidId, entry.getOldPath(), commit.getId());
						this.changeDao.save(compare);
					}
					if(entry.getOldPath().endsWith("pom.xml") && entry.getChangeType()==org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY) {
						File oldTmpFile = loadFileOfRepositoryToTemporaryFile(repo, repo.resolve(entry.getOldId().name()), new File(entry.getOldPath()).getName(),
								""/*+oldCommit.getCommitterIdent().getWhen().getTime()*/,oldCommit.getName(),".xml", tempDir);
						File newTmpFile = loadFileOfRepositoryToTemporaryFile(repo, repo.resolve(entry.getNewId().name()), new File(entry.getNewPath()).getName(),
								""/*+revCommitNew.getCommitterIdent().getWhen().getTime()*/,revCommitNew.getName(),".xml", tempDir);
						List<Change> compare = compareMaven(oldTmpFile, newTmpFile, repo, oldCommitId, newCommidId, entry.getOldPath(), commit.getId());
						this.changeDao.save(compare);
					}
					
				}
		    } else {
		    	System.out.println("NO PARENT");
		    }
		}
	}
	
	private File loadFileOfRepositoryToTemporaryFile(org.eclipse.jgit.lib.Repository repo, ObjectId objectId, String fileName,String timestamp,String versionId, String postfix, File tmpDir) throws MissingObjectException, IOException, FileNotFoundException {
		ObjectLoader loader = repo.open(objectId);
		File tmpFile = new File(tmpDir,fileName.substring(0,fileName.indexOf("."))+"["+timestamp+"]["+versionId+"]"+postfix);
		System.out.println(tmpFile.getAbsolutePath());
		OutputStream outputStream = new FileOutputStream(tmpFile);
		loader.copyTo(outputStream);
		outputStream.close();
		return tmpFile;
	}
	@Override
	@Transactional
	public Map<CommitPair, List<String>> getChangesPerCommit(Long projectId) {
		List<Object[]> changesPerProject = changeDao.getChangesPerProject(projectId);
		Map<CommitPair,List<String>> retVal = new HashMap<CommitPair, List<String>>();
		
		for (Object[] row : changesPerProject) {
			String newCommitId=""+row[0];
			String oldCommitId=""+row[1];
			String change = ""+row[2];
			
			CommitPair cp = new CommitPair(oldCommitId, newCommitId);
			if(!retVal.containsKey(cp)) {
				retVal.put(cp, new ArrayList<String>());
			}
			retVal.get(cp).add(change);
		}
		
		return retVal;
	}
	public List<Change> compareMaven(File oldTmpFile, File newTmpFile, Repository repo, String oldId, String newId, String filePath, long commitId) throws Exception {
		Differ diff= new MavenBuildFileDiffer();
		try {
			List<at.aau.diff.common.Change> bcs = diff.extractChanges(oldTmpFile, newTmpFile);
		
			List<Change> retVal = new ArrayList<Change>();
			for (at.aau.diff.common.Change bc : bcs) {
				MavenBuildChange mbc = (MavenBuildChange) bc;
				Change c = new Change(null, oldId, newId, "", "", filePath, mbc.getName(), ChangeType.BUILD_CODE_CHANGE, "CT", "CTT", commitId);
				retVal.add(c);
			}
			return retVal;
		}catch(Exception e) {
			return new ArrayList<Change>();
		}
	}
	public List<Change> compareJava(File oldTmpFile, File newTmpFile, Repository repo, String oldId, String newId, String filePath, long commitId) {
		FileDistiller fd = ChangeDistiller.createFileDistiller(Language.JAVA);
		fd.extractClassifiedSourceCodeChanges(newTmpFile,oldTmpFile);
		List<SourceCodeChange> sccs = fd.getSourceCodeChanges();
		
		List<Change> retVal = new ArrayList<Change>();
		for (SourceCodeChange sourceCodeChange : sccs) {
			Change c = new Change(null, oldId, newId, "", "", filePath, sourceCodeChange.getLabel(), ChangeType.SOURCE_CODE_CHANGE, "CT", "CTT", commitId);
			retVal.add(c);
		}
		return retVal;
	}

	@Override
	@Transactional
	public void extractWorkItems(Long projectId) {
		//TODO REFINE check with bookkeeper
		List<Commit> commits = commitDao.findByProjectId(projectId);
		
		for (Commit commit : commits) {
			String issueId = extractIssueId(commit.getCommitMessage());
			if(issueId!=null) {
				WorkItem wi = workItemDao.findByWorkItemName(issueId);
				if(wi==null) {
					//create wi
					wi = new WorkItem(null, issueId, new ArrayList<Commit>(), projectId,null);
					workItemDao.save(wi);
				}
				commit.setWorkItemId(wi.getId());
//				wi.getCommits().add(commit);
				workItemDao.save(wi);
				commitDao.save(commit);
			}
		}
	}
	
	private  String extractIssueId(String fullMessage) {
		Pattern pattern = Pattern.compile("[a-zA-Z]{2,}-[\\d]{1,}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fullMessage);
		
		if(matcher.find()) {
			String match = matcher.group();
			match = match.replaceAll("\\s", "");
			match = match.replaceAll(":", "");
			match = match.trim();
			return match;			
		}
		
		return null;
	}

	@Override
	@Transactional
	public void importBuildResults(Long projectId, String buildOutputLocation) throws IOException {
		List<Commit> commits = commitDao.findByProjectId(projectId);
		
		for (Commit commit : commits) {
			File fJDK8 = new File(buildOutputLocation,"commit_"+commit.getCommitId()+".txt");
			File fJDK7 = new File(buildOutputLocation,"commit_"+commit.getCommitId()+"_JDK7.txt");
			File fJDK6 = new File(buildOutputLocation,"commit_"+commit.getCommitId()+"_JDK6.txt");
			
			if(fJDK8.exists()) {
				String jdk8res=getFailReason(fJDK8);
				Build b = new Build(null,"",BuildResult.valueOf(jdk8res),0L,"JDK8",new Date(),commit.getId());
				buildDao.save(b);
			}
			if(fJDK7.exists()) {
				String jdk7res=getFailReason(fJDK7);
				Build b = new Build(null,"",BuildResult.valueOf(jdk7res),0L,"JDK7",new Date(),commit.getId());
				buildDao.save(b);
			}
			if(fJDK6.exists()) {
				String jdk6res=getFailReason(fJDK6);
				Build b = new Build(null,"",BuildResult.valueOf(jdk6res),0L,"JDK6",new Date(),commit.getId());
				buildDao.save(b);
			}
		}
	}

	private String getFailReason(File file) throws IOException {
		String content = FileUtils.readFileToString(file);
		//System.out.println("CHECKING FILE FOR REASON: "+file.getAbsolutePath());
		List<String> lines = FileUtils.readLines(file);
		if(content.contains("BUILD SUCCESS")) {
			return "SUCCESS";
		} else {
			for (String line : lines) {
				if(line.startsWith("[ERROR]")) {
					if(line.contains("Could not resolve dependencies")) {
						return "DEPENDENCY_RESOLUTION_FAILED";
					}
					if(line.contains("There are test failures.")) {
						return "TEST_EXECUTION_FAILED";
					}
					if(line.contains("COMPILATION ERROR") || line.contains("Compilation failure")) {
						return "COMPILATION_FAILED";
					}
				}
				if((line.startsWith("[FATAL]") && line.contains("Non-parseable POM")) || (line.startsWith("[ERROR]") && line.contains("Malformed POM"))) {
					return "POM_PARSING_FAILED";
				}
				//tmp
				if(line.contains("Failed to execute goal ")) {
					return "GOAL_FAILED";
				}
				if(line.startsWith("[FATAL]") && line.contains("Non-resolvable parent POM")) {
					return "NO_PARENT_FAILED";
				}
				if(line.startsWith("[ERROR]") && line.contains("The projects in the reactor contain a cyclic reference")) {
					return "CYCLIC_DEPENDENCIES_FAILED";
				}
				if(line.startsWith("[ERROR]") && line.contains("Plugin") && line.contains("or one of its dependencies could not be resolved")) {
					return "PLUGIN_DEPENDENCIES_FAILED";
				}
				if(line.startsWith("[ERROR]") && line.contains("Child module") && line.contains("does not exist")) {
					return "NO_CHILD_FAILED";
				}
				if(line.startsWith("[ERROR]") && line.contains("must be a valid version but is")) {
					return "MALFORMED_VERSION_FAILED";
				}
			}
		}
		
		return "UNKOWN_FAILED";
	}

	@Override
	public void importIssueTypes(Long projectId, String jiraUrl) throws MalformedURLException, UnsupportedEncodingException, IOException, ParseException {
		List<WorkItem> workItems = workItemDao.findByProjectIdAndIssueType(projectId,null);
//		List<WorkItem> workItems = workItemDao.findByProjectId(projectId);
		System.out.println("Number of workitems to do: "+workItems.size());
		for (WorkItem workItem : workItems) {
			String issueType = getTypeOfIssue(workItem.getWorkItemName(), jiraUrl);
			if(issueType!=null) {
				workItem.setIssueType(issueType);
				workItemDao.save(workItem);
				System.out.println("found for :: "+workItem.getWorkItemName()+": "+issueType);
				//To avoid being locked out from jira we wait 
				try {
					Thread.sleep(500+((int)Math.random()*1000));
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private String getTypeOfIssue(String issueId, String jiraUrl) throws MalformedURLException, IOException, UnsupportedEncodingException, ParseException {
		String content=getIssueContents(issueId,jiraUrl);
		if(content==null) {
			return null;
		}
		return extractIssueType(content);
	}

	private String getIssueContents(String issueId, String jiraUrl) throws MalformedURLException, IOException, UnsupportedEncodingException {
		//curl -D- -X GET -H "Content-Type: application/json" https://issues.apache.org/jira/rest/api/2/search?jql=id=HBASE-2042 > curlOutput.txt
		try {
			String content="";
	//		URL url = new URL("https://jira.spring.io/rest/api/2/search?jql=id="+issueId);
	//		URL url = new URL("https://issues.apache.org/jira/rest/api/2/search?jql=id="+issueId);
	//		URL url = new URL("https://hibernate.atlassian.net/rest/api/2/search?jql=id="+issueId);
			
			URL url = new URL(jiraUrl+issueId);
	
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			    for (String line; (line = reader.readLine()) != null;) {
			    	content+=line+"\n";
			    }
			}
			return content;
		}catch(IOException ioe){
			return null;
		}
	}

	private String extractIssueType(String content) throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			JSONObject root = (JSONObject) parser.parse(content);
			JSONArray issues = (JSONArray) root.get("issues");
			Iterator iter = issues.iterator();
			JSONObject next = (JSONObject) iter.next();
			JSONObject fields = (JSONObject) next.get("fields");
			JSONObject issuetype = (JSONObject) fields.get("issuetype");
			String name = ((String)issuetype.get("name"));
			return name;
		} catch(NoSuchElementException nsee) {
			return null;
		}
	}

	@Override
	@Transactional
	public List<String> getDistinctChanges(Long projectId) {
		return changeDao.findDistinctChanges();
	}

}
