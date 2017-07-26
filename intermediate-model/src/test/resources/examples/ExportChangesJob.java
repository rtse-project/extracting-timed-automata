package at.aau.service.jobs;

import at.aau.entity.JobEntry;
import at.aau.entity.JobState;
import at.aau.service.JobService;
import at.aau.service.ProjectService;
import at.aau.service.dto.CommitPair;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ExportChangesJob extends Thread{
	private Long jobId;
	private JobService jobService;
	private ProjectService projectService;
	private JobEntry jobEntry;
	private Long projectId;

	public ExportChangesJob(JobService jobService,Long jobId, ProjectService projectService, Long projectId) {
		this.jobService = jobService;
		this.jobId = jobId;
		this.projectId = projectId;
		this.jobEntry = jobService.findJobEntry(jobId);
		this.projectService = projectService;
	}
	public void run() {
		try {
			Map<CommitPair, List<String>> changesPerCommit = projectService.getChangesPerCommit(projectId);
			try {
				File f =new File("/Users/chris/Desktop/export.csv");
				System.out.println(f.getAbsolutePath());
				int maxChanges=findMaxChanges(changesPerCommit);
				PrintStream ps = new PrintStream(f);
				//i am here at the moment
				for (CommitPair cp : changesPerCommit.keySet()) {
					ps.print(cp.getOldCommitId()+","+cp.getNewCommitId());
					List<String> changes = changesPerCommit.get(cp);
					int i = 0;
					for (; i < changes.size(); i++) {
						String change = changes.get(i);
						ps.print(","+change);
					}
					while(i<maxChanges) {
						ps.print(",");
						i++;
					}
					ps.println();
				}
				ps.close();
				System.out.println("DISTINCT");
				f =new File("/Users/chris/Desktop/exportDistinct.csv");
				System.out.println(f.getAbsolutePath());
				maxChanges=findMaxDistinctChanges(changesPerCommit);
				System.out.println("maxChanges="+maxChanges);
				ps = new PrintStream(f);
				ps.print("oldId,newId");
				for(int i=0;i<maxChanges;i++) {
					ps.print(",c"+(i+1));
				}
				ps.println();
				for (CommitPair cp : changesPerCommit.keySet()) {
					ps.print(cp.getOldCommitId()+","+cp.getNewCommitId());
					Set<String> changes = new TreeSet<String>(changesPerCommit.get(cp));
					int i = 0;
//					for (; i < changes.size(); i++) {
					for(String change : changes) {
//						String change = changes.get(i);
						ps.print(","+change);
						i++;
					}
//					i++; //helper
					while(i<maxChanges) {
						ps.print(",");
						i++;
					}
					ps.println();
				}
				ps.close();
				//check
//				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
//				String s="";
//				while((s=br.readLine())!=null) {
//					System.out.println(StringUtils.countMatches(s, ","));
//				}
//				br.close();
				
				//another apriori data approach
				List<String> distinctChanges = projectService.getDistinctChanges(projectId);
				f =new File("/Users/chris/Desktop/exportDistinctTable.csv");
				ps = new PrintStream(f);
				ps.print("oid,nid");
				for (String distinctChange : distinctChanges) {
					ps.print(",");
					ps.print(distinctChange);
				}
				ps.println();
				for (CommitPair commitPair : changesPerCommit.keySet()) {
					List<String> changesInThisCommit = changesPerCommit.get(commitPair);
					ps.print(commitPair.getOldCommitId()+","+commitPair.getNewCommitId());
					for (String distinctChange : distinctChanges) {
						ps.print(",");
						ps.print((""+changesInThisCommit.contains(distinctChange)).toUpperCase());
					}
					ps.println();
				}
				ps.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			jobService.finishJob(jobId,JobState.SUCCESS,"success");
			System.out.println("FINISHED THE JOB");
		} catch (Exception e) {
			e.printStackTrace();
			jobService.finishJob(jobId,JobState.ERROR,e.getMessage());
		}
	}
	private int findMaxDistinctChanges(Map<CommitPair, List<String>> changesPerCommit) {
		int max=0;
		for (CommitPair cp : changesPerCommit.keySet()) {
			max=Math.max(max, new TreeSet<String>(changesPerCommit.get(cp)).size());
		}
		return max;
	}
	private int findMaxChanges(Map<CommitPair, List<String>> changesPerCommit) {
		int max=0;
		for (CommitPair cp : changesPerCommit.keySet()) {
//			System.out.println(max+" " +changesPerCommit.get(cp).size());
			max=Math.max(max, changesPerCommit.get(cp).size());
		}
		return max;
	}
}