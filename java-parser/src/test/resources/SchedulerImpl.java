/**
 */
package scheduler.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import ascass.solving.CaspSolver;
import scheduler.Changeover;
import scheduler.Checker;
import scheduler.Constants;
import scheduler.Group;
import scheduler.Job;
import scheduler.JobType;
import scheduler.Machine;
import scheduler.Parser;
import scheduler.Scheduler;
import scheduler.SchedulerPackage;
import scheduler.Solver;
import scheduler.Splitter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scheduler</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link scheduler.impl.SchedulerImpl#getParser <em>Parser</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getSolver <em>Solver</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getMachines <em>Machines</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getSplitter <em>Splitter</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getCurrentTime <em>Current Time</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getGroups <em>Groups</em>}</li>
 *   <li>{@link scheduler.impl.SchedulerImpl#getJobs <em>Jobs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SchedulerImpl extends MinimalEObjectImpl.Container implements Scheduler {
	/**
	 * The cached value of the '{@link #getParser() <em>Parser</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParser()
	 * @generated
	 * @ordered
	 */
	protected Parser parser;

	/**
	 * The cached value of the '{@link #getSolver() <em>Solver</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSolver()
	 * @generated
	 * @ordered
	 */
	protected Solver solver;

	/**
	 * The cached value of the '{@link #getMachines() <em>Machines</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMachines()
	 * @generated
	 * @ordered
	 */
	protected HashMap<Integer,Machine> machines;

	/**
	 * The cached value of the '{@link #getSplitter() <em>Splitter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSplitter()
	 * @generated
	 * @ordered
	 */
	protected Splitter splitter;

	/**
	 * The default value of the '{@link #getCurrentTime() <em>Current Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentTime()
	 * @generated
	 * @ordered
	 */
	protected static final int CURRENT_TIME_EDEFAULT = 0;

	

	/**
	 * The cached value of the '{@link #getCurrentTime() <em>Current Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentTime()
	 * @generated
	 * @ordered
	 */
	protected int currentTime = CURRENT_TIME_EDEFAULT;
	
	
	
	



	/**
	 * The cached value of the '{@link #getGroups() <em>Groups</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroups()
	 * @generated
	 * @ordered
	 */
	protected HashMap<Integer,Group> groups;

	/**
	 * The cached value of the '{@link #getJobs() <em>Jobs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJobs()
	 * @generated
	 * @ordered
	 */
	protected HashMap<String,Job> jobs;

	protected int maxTime;

	protected int maxPenalty;
	
	private Checker checker;

	protected HashMap<String, Changeover> changeover;
	
	protected String inputPath;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SchedulerImpl() {
		super();
		jobs= new HashMap<String,Job>();
		machines=new HashMap<Integer,Machine>();
		groups=new HashMap<Integer,Group>();
		changeover= new HashMap<String,Changeover>();
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * @param args contains the information about the directory of the input files for the parser and the destination of the output 
	 * <!-- end-user-doc -->
	 */
	
	public SchedulerImpl(String path) {
		super();
		this.inputPath=path;
		jobs= new HashMap<String,Job>();
		machines=new HashMap<Integer,Machine>();
		groups=new HashMap<Integer,Group>();
		changeover= new HashMap<String,Changeover>();
		if (path.endsWith(".asp"))
			setParser(new ParserIS(path,this));
		else
			if (path.endsWith(".edb"))
				setParser(new ParserImpl(path,this));
			else
				if (path.endsWith(".lp"))
					setParser(new ParserImpl(path,this));
				else
					System.out.println("ERROR: File format not compatible with the parser: "+path);
				
	}
	
	
	public SchedulerImpl(String[] args) {
		super();
		jobs= new HashMap<String,Job>();
		machines=new HashMap<Integer,Machine>();
		groups=new HashMap<Integer,Group>();
		changeover= new HashMap<String,Changeover>();
		setParser(new ParserImpl(args[0], args[1], args[2],this));

	}
	
	
	
	
	
	@Override
	public String getInputPath() {
		return inputPath;
	}

	@Override
	public void setMaxTime(int parseInt) {
		this.maxTime=parseInt;
		
	}
	
	
	public int getMaxTime() {
		return this.maxTime;
		
	}
	
	
	
	@Override
	public void setMaxPenalty(int parseInt) {
		this.maxPenalty=parseInt;
		
	}
	
	
	public int getMaxPenalty() {
		return this.maxPenalty;
		
	}

	
	@Override
	public void setSolver(Solver value) {
		// TODO Auto-generated method stub
		this.solver=value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SchedulerPackage.Literals.SCHEDULER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parser getParser() {
		if (parser != null && parser.eIsProxy()) {
			InternalEObject oldParser = (InternalEObject)parser;
			parser = (Parser)eResolveProxy(oldParser);
			if (parser != oldParser) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, SchedulerPackage.SCHEDULER__PARSER, oldParser, parser));
			}
		}
		return parser;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parser basicGetParser() {
		return parser;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParser(Parser newParser) {
		Parser oldParser = parser;
		parser = newParser;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SchedulerPackage.SCHEDULER__PARSER, oldParser, parser));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Solver getSolver() {
	
		return solver;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HashMap<Integer,Machine> getMachines() {
		if (machines == null) {
			return null;
		}
		return machines;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HashMap<String,Job> getJobs() {
		if (jobs == null) {
		return null;
		}
		return jobs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Splitter getSplitter() {
		if (splitter != null && splitter.eIsProxy()) {
			InternalEObject oldSplitter = (InternalEObject)splitter;
			splitter = (Splitter)eResolveProxy(oldSplitter);
			if (splitter != oldSplitter) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, SchedulerPackage.SCHEDULER__SPLITTER, oldSplitter, splitter));
			}
		}
		return splitter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Splitter basicGetSplitter() {
		return splitter;
	}

	
	
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSplitter(Splitter newSplitter) {
		Splitter oldSplitter = splitter;
		splitter = newSplitter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SchedulerPackage.SCHEDULER__SPLITTER, oldSplitter, splitter));
	}
	
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setChecker(Checker newChecker) {
		Checker oldChecker = checker;
		checker = newChecker;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SchedulerPackage.SCHEDULER__SPLITTER, oldChecker, checker));
	}
	
	
	
	public Checker getChecker() {
		if (checker != null && checker.eIsProxy()) {
			InternalEObject oldChecker = (InternalEObject)checker;
			checker = (Checker)eResolveProxy(oldChecker);
			if (checker != oldChecker) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, SchedulerPackage.SCHEDULER__SPLITTER, oldChecker, checker));
			}
		}
		return checker;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Checker basicGetChecker() {
		return checker;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrentTime(int newCurrentTime) {
		int oldCurrentTime = currentTime;
		currentTime = newCurrentTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SchedulerPackage.SCHEDULER__CURRENT_TIME, oldCurrentTime, currentTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HashMap<Integer,Group> getGroups() {
		if (groups == null) {
			groups = null;
		}
		return groups;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SchedulerPackage.SCHEDULER__PARSER:
				if (resolve) return getParser();
				return basicGetParser();
			case SchedulerPackage.SCHEDULER__SOLVER:
				return getSolver();
			case SchedulerPackage.SCHEDULER__MACHINES:
				return getMachines();
			case SchedulerPackage.SCHEDULER__SPLITTER:
				if (resolve) return getSplitter();
				return basicGetSplitter();
			case SchedulerPackage.SCHEDULER__CURRENT_TIME:
				return getCurrentTime();
			case SchedulerPackage.SCHEDULER__GROUPS:
				return getGroups();
			case SchedulerPackage.SCHEDULER__JOBS:
				return getJobs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case SchedulerPackage.SCHEDULER__PARSER:
				setParser((Parser)newValue);
				return;
	/*		case SchedulerPackage.SCHEDULER__SOLVER:
				getSolver().clear();
				getSolver().addAll((Collection<? extends Solver>)newValue);
				return;*/
			case SchedulerPackage.SCHEDULER__MACHINES:
				getMachines().clear();
				((List<Solver>) getMachines()).addAll((Collection<? extends Solver>)newValue);
				return;
			case SchedulerPackage.SCHEDULER__SPLITTER:
				setSplitter((Splitter)newValue);
				return;
			case SchedulerPackage.SCHEDULER__CURRENT_TIME:
				setCurrentTime((Integer)newValue);
				return;
			case SchedulerPackage.SCHEDULER__GROUPS:
				getGroups().clear();
				((List<Solver>) getGroups()).addAll((Collection<? extends Solver>)newValue);
				return;
			case SchedulerPackage.SCHEDULER__JOBS:
				getJobs().clear();
				((List<Solver>) getJobs()).addAll((Collection<? extends Solver>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case SchedulerPackage.SCHEDULER__PARSER:
				setParser((Parser)null);
				return;
			case SchedulerPackage.SCHEDULER__SOLVER:
				setSolver((Solver)null);
				return;
			case SchedulerPackage.SCHEDULER__MACHINES:
				getMachines().clear();
				return;
			case SchedulerPackage.SCHEDULER__SPLITTER:
				setSplitter((Splitter)null);
				return;
			case SchedulerPackage.SCHEDULER__CURRENT_TIME:
				setCurrentTime(CURRENT_TIME_EDEFAULT);
				return;
			case SchedulerPackage.SCHEDULER__GROUPS:
				getGroups().clear();
				return;
			case SchedulerPackage.SCHEDULER__JOBS:
				getJobs().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case SchedulerPackage.SCHEDULER__PARSER:
				return parser != null;
			case SchedulerPackage.SCHEDULER__SOLVER:
				return solver != null;
			case SchedulerPackage.SCHEDULER__MACHINES:
				return machines != null && !machines.isEmpty();
			case SchedulerPackage.SCHEDULER__SPLITTER:
				return splitter != null;
			case SchedulerPackage.SCHEDULER__CURRENT_TIME:
				return currentTime != CURRENT_TIME_EDEFAULT;
			case SchedulerPackage.SCHEDULER__GROUPS:
				return groups != null && !groups.isEmpty();
			case SchedulerPackage.SCHEDULER__JOBS:
				return jobs != null && !jobs.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	
	@Override
	public int getLotsCount(){
		Set<Integer> alreadyCounted= new HashSet<Integer>(); 
		int count=0;
		for (Job j: getJobs().values())
			if (!alreadyCounted.contains(j.getLot())){
				alreadyCounted.add(j.getLot());
				count++;
			}
		return count;
	}
	
	
	@Override
	public int getMaxJobsPerLot(){

		int count=0;
		int max=0;
		
		for (Job j: getJobs().values())
			if (j.hasDeadline()){
				count++;
				while(j.hasPreviousJob()){
				count++;
				j=j.getPreviousJob();
				}
		
			max= Math.max(max, count);
			count=0;
			}
		return max;
	}
	
	
	public String jobsToString(){
		Iterator<Job> it = getJobs().values().iterator();
		return jobsToString(it);
	}
	
	@Override
	public String job2lotToString(){
		Iterator<Job> it = getJobs().values().iterator();
		return job2lotToString(it);
	}
	
	@Override
	public String jobWorkflowsToString(){
		Iterator<Job> it = getJobs().values().iterator();
		return jobWorkflowsToString(it);
	}
	
	
	
	public String machinesToString(){
		Iterator<Machine> it = getMachines().values().iterator();
		return machinesToString(it);
	}
	
	public String deadlinesToString() {
		Iterator<Job> it = getJobs().values().iterator();
		return deadlinesToString(it);
	}
	

	public String precendecesToString() {
		Iterator<Job> it = getJobs().values().iterator();
		return precendecesToString(it);
	}
	
	public String importancesToString() {
		Iterator<Job> it = getJobs().values().iterator();
		return importancesToString(it);
	}
	
	
	

	public String couplingsToString() {
		Iterator<Job> it = getJobs().values().iterator();
		return couplingsToString(it);
	}
	
	
	public String changeoversToString() {
		Iterator<Changeover> it = getChangeover().values().iterator();
		return changeoversToString(it);
	}
	
	
	
	public String availabilitiesToString() {
		Iterator<Machine> it =  getMachines().values().iterator();
		return availabilitiesToString(it);
	}
	
	
	@Override
	public String currentTimeToString() {
		
		return Constants.CURRENTTIME+"("+currentTime+").\n";	
	}
	
	
	@Override
	public String maxTimeToString() {
		
		return Constants.MAXTIME+"("+maxTime+").\n";	
	}
	
	
	
	@Override
	public String maxPenaltyToString() {
		
		return Constants.MAXPENALTY+"("+maxPenalty+").\n";	
	}
	
	
	@Override
	public String alreadyFinishedToString() {
		Iterator<Job> it = getJobs().values().iterator();
		return alreadyFinishedToString(it);
	
	}
	
	
	@Override
	public String joblenToString() {
		Iterator<Job> it =   getJobs().values().iterator();
		return joblenToString(it);
	}



	@Override
	public String machinePossibleTasksToString() {
		Iterator<Job> it =   getJobs().values().iterator();
		return machinePossibleTasksToString(it);
	}
	
	@Override
	public String currJobsToString() {
		Iterator<Job> it =   getJobs().values().iterator();
		return currJobsToString(it);
	}
	

	@Override
	public String currTypeToString() {
		Iterator<Machine> it =  getMachines().values().iterator();
		return currTypeToString(it);
	}

	@Override
	public String alreadyFinishedToString(Group g) {
		Iterator<Job> it = g.getJobsInGroup().iterator();
		return alreadyFinishedToString(it);
	}

	
	


	@Override
	public String currTypeToString(Group g) {
		Iterator<Machine> it =  g.getMachineInGroup().iterator();
		return currTypeToString(it);
	}

	public String jobsToStringRandom(Group g){
		EList<Job> jobsRandom= g.getJobsInGroup();
		Collections.shuffle( jobsRandom);
		
		Iterator<Job> it = jobsRandom.iterator();
		return jobsToString(it);
	}
	
	public String jobsToString(Group g){
		Iterator<Job> it = g.getJobsInGroup().iterator();
		return jobsToString(it);
	}
	
	@Override
	public String job2lotToString(Group g){
		Iterator<Job> it = getJobs().values().iterator();
		return job2lotToString(it);
	}
	
	@Override
	public String jobWorkflowsToString(Group g){
		Iterator<Job> it = g.getJobsInGroup().iterator();
		return jobWorkflowsToString(it);
	}
	
	
	
	public String machinesToString(Group g){
		Iterator<Machine> it =  g.getMachineInGroup().iterator();
		return machinesToString(it);
	}
	@Override
	public String machineLastFinishTimesToString(Group g){
		Iterator<Machine> it =  g.getMachineInGroup().iterator();
		return machineLastFinishTimesToString(it);
	}
	
	
	public String deadlinesToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return deadlinesToString(it);
	}
	

	public String precendecesToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return precendecesToString(it);
	}
	
	public String importancesToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return importancesToString(it);
	}
	
	

	public String couplingsToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return couplingsToString(it);
	}
	
	
	public String changeoversToString(Group g) {
	

		
		Iterator<Changeover> it =  g.getChangeover().iterator();
		return changeoversToString(it);
		
	
	}
	
	
	public String availabilitiesToString(Group g) {
		Iterator<Machine> it =  g.getMachineInGroup().iterator();
		return availabilitiesToString(it);
	}
	
	@Override
	public String joblenToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return joblenToString(it);
	}
	
	@Override
	public String machinePossibleTasksToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return machinePossibleTasksToString(it);
	}
	
	@Override
	public String currJobsToString(Group g) {
		Iterator<Job> it =  g.getJobsInGroup().iterator();
		return currJobsToString(it);
	}
	
	@Override
	public String nMachinesToString(){
		return Constants.NMACHINES+"("+getMachines().size()+").\n";
	}
	
	
	public String ascassToString(String file){
		String result="\n";
		LineIterator it=null;
		
		try {
			File f = new File((new File(".").getCanonicalPath())+file);  //"\\ascass\\split.ascass");
			it = FileUtils.lineIterator(f, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(it.hasNext()){
			String line=it.nextLine();
			result += line+"\n";
		}
		return result;
	}
	
	
	

	
	
	
	private String jobsToString(Iterator<Job> it){
		String result="";
		int jobs=0;
		while(it.hasNext()){
			Job j=it.next();
			result+= Constants.JOBS+"("+j.getId()+","+j.getType().getProduct()+","+j.getType().getTask()+").\n";
			jobs++;
		}
	
		return result;
	}
	
	
	
	private String job2lotToString(Iterator<Job> it){
		String result="";
		int jobs=0;
		while(it.hasNext()){
			Job j=it.next();
			result+= Constants.JOB2LOT+"("+j.getId()+","+j.getLot()+").\n";
			jobs++;
		}
	
		return result;
	}
	
	

	private String jobWorkflowsToString(Iterator<Job> it){
		String result="";
		int jobs=0;
		while(it.hasNext()){
			Job j=it.next();
			result+= Constants.JOBWORKFLOWS+"("+j.getId()+","+j.getWorkflow()+").\n";
			jobs++;
		}
	
		return result;
	}
	
	private String machinesToString(Iterator<Machine> it){
		String result="";
		while(it.hasNext()){
			Machine m=it.next();
				result+= Constants.MACHINE+"("+m.getId()+").\n";
		}
		return result;
	}
	
	
	
	private String machineLastFinishTimesToString(Iterator<Machine> it){
		StringBuffer sb=new StringBuffer();
		while(it.hasNext()){
			Machine m=it.next();
			Job j=m.getLastScheduledJob();
			if (j==null)
				sb.append(Constants.MACHINELASTFINISHTIME+"("+m.getId()+","+getCurrentTime()+").\n");
			else
				sb.append(Constants.MACHINELASTFINISHTIME+"("+m.getId()+","+j.getFinishAt()+").\n");
		}
		return sb.toString();
	}
	
	
	
	private String deadlinesToString(Iterator<Job> it) {
		StringBuffer sb=new StringBuffer();
		while(it.hasNext()){
			Job j=it.next();
			if(j.hasDeadline())
				sb.append(Constants.DEADLINE+"("+j.getId()+","+j.getDeadline()+").\n");
		}
		return sb.toString();
	}

	
	private String importancesToString(Iterator<Job> it) {
		StringBuffer sb=new StringBuffer();
		while(it.hasNext()){
			Job j=it.next();
			
				sb.append(Constants.IMPORTANCE+"("+j.getId()+","+j.getImportance()+").\n");
		}
		return sb.toString();
	}

	private String precendecesToString(Iterator<Job> it) {
		String result="";
		while(it.hasNext()){
			Job j=it.next();
			if(j.hasPreviousJob())
				result+= Constants.PRECEDENCE+"("+j.getPreviousJob().getId()+","+j.getId()+").\n";
		}
		return result;
	}
	
	

	private String couplingsToString(Iterator<Job> it) {
		String result="";
		
		while(it.hasNext()){
			Job j=it.next();
			if(j.hasCoupling())
				if(j.hasPreviousJob() && j.getPreviousJob().getId().equals(j.getCoupledJob().getId()))
					result+= Constants.COUPLING+"("+j.getPreviousJob().getId()+","+j.getId()+","+j.getCouplingLength()+").\n";
				
		}
		return result;
	}
	
	
	private String changeoversToString(Iterator<Changeover> it) {
		StringBuffer sb=new StringBuffer();
		Set<String> alreadyPut=new HashSet<String>();
		Changeover c;
		while(it.hasNext()){
	
				c=it.next();
					if (!alreadyPut.contains(c.toString())){
						
						sb.append(Constants.CHANGEOVER+"("+c.getType1().getProduct()+","+c.getType1().getTask()+","+c.getType2().getProduct()+","+c.getType2().getTask()+","+c.getLength()+").");				
						alreadyPut.add(c.toString());
					}
			}
				
			
		
		
		return sb.toString();
	}
	
	
	
	private String availabilitiesToString(Iterator<Machine> it) {
		String result="";
		
		
		while(it.hasNext()){
			Machine m=it.next();
			for (int i=0;i<m.getAvailability().size();i+=2)
			result+= Constants.AVAILABILITY+"("+m.getId()+","+m.getAvailability().get(i)+","+m.getAvailability().get(i+1)+").\n";				
		}
		return result;
	}
	
	

	
	
	
	private String alreadyFinishedToString(Iterator<Job> it) {
		String result="";
		while(it.hasNext()){
			Job j=it.next();
			if(j.isAlreadyFinished())
					result+= Constants.FINISHED+"("+j.getId()+").\n";
				
		}
		return result;
	
	}
	
	
	private String joblenToString(Iterator<Job> it) {
		String result="";
		while(it.hasNext()){
			Job j=it.next();
			for (Entry<Machine, Integer> e: j.getPossibleMachines().entrySet())
					result+= Constants.JOBLENGTH+"("+j.getId()+","+e.getKey().getId()+","+e.getValue()+").\n";
				
		}
		return result;
	}
	
	
	private String machinePossibleTasksToString(Iterator<Job> it) {
		String result="";
		
		Set<Integer> alreadyPut= new HashSet<Integer>();
		while(it.hasNext()){
			Job j=it.next();
			for (Machine m: j.getPossibleMachines().keySet()){
			int hash= (m.getId()+j.getType().getTask()).hashCode();
					if (!alreadyPut.contains(hash)){
					
						result+= Constants.MACHINETASKS+"("+m.getId()+","+j.getType().getTask()+").\n";
						alreadyPut.add(hash);
					}
			}
		}
		
		return result;
	}
	
	

	private String currTypeToString(Iterator<Machine> it) {
		String result="";
		while(it.hasNext()){
			Machine m=it.next();
			result+= Constants.MACHINECURRENTTYPE+"("+m.getId()+","+m.getCurrentType().toString()+").\n";		
		}
		return result;	
	}
	
	
	private String currJobsToString(Iterator<Job> it) {
		StringBuffer sb=new StringBuffer();
		while(it.hasNext()){
			Job j=it.next();
			if(j.isCurrentJob()){
				Machine m=j.getAssigned2Machine();
				int length= j.getFinishAt()-Math.max(currentTime, j.getStartsAt());
				sb.append(Constants.CURRJOB+"("+j.getId()+","+m.getId()+","+length+").\n");
					
			}
		}
		return sb.toString();
	}
	
	
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (currentTime: ");
		result.append(currentTime);
		result.append(')');
		return result.toString();
	}

	
	
	
	

	@Override
	public String generateCheckableOutput(){
		// TODO Auto-generated method stub
		StringBuffer sb = null;
		try {
			
			
			sb =new StringBuffer();
			//add the input facts
			BufferedReader br= new BufferedReader(new FileReader(new File(inputPath)));
	
			for (String line = br.readLine(); line != null; line = br.readLine())
				 sb.append(line  + "\n");
			 
			//sb.append(this.getSolver().getCaspSolver().getAtoms() + "\n");
			
		
			
			
			//add the starting times
			for (Job j: this.getJobs().values())
				if (!j.isAlreadyFinished()){
					sb.append("start("+j.getId()+","+j.getStartsAt()+").\n");
					sb.append("assign("+j.getId()+","+j.getAssigned2Machine().getId()+",1).\n");
					sb.append("finished("+j.getId()+","+j.getFinishAt()+").\n");
					
					//get the order
					int i=1;
					for(Job j1: j.getAssigned2Machine().getScheduledJobs())
						if (!j1.equals(j))
							i++;
						else
							break;
					sb.append("order("+j.getId()+","+i+").\n");
				}
			
			
		
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	
	
	public void generateCheckableOutput(String file){
		// TODO Auto-generated method stub

		try {
			Writer fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));
			
			
			
			//add the input facts
			BufferedReader br= new BufferedReader(new FileReader(new File(inputPath)));
	
			for (String line = br.readLine(); line != null; line = br.readLine())
				 fw.write(line  + "\n");
			 
			fw.write(this.getSolver().getCaspSolver().getAtoms() + "\n");
			
		
			
			
			//add the starting times
			for (Job j: this.getJobs().values())
				if (!j.isAlreadyFinished()){
					fw.write("start("+j.getId()+","+j.getStartsAt()+").\n");
					fw.write("assign("+j.getId()+","+j.getAssigned2Machine().getId()+",1).\n");
					fw.write("finished("+j.getId()+","+j.getFinishAt()+").\n");
					
					//get the order
					int i=1;
					for(Job j1: j.getAssigned2Machine().getScheduledJobs())
						if (!j1.equals(j))
							i++;
						else
							break;
					fw.write("order("+j.getId()+","+i+").\n");
				}
			
			
			
			fw.close();
		
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void addGroup(Group g) {
		if (groups==null)
			groups=new HashMap<Integer,Group>();
		groups.put(g.getId(), g);
		
	}
	
	
	@Override
	public void updateChangeoverInGroup(Group g) {
		EList<JobType> typesInGroup= g.getTypesInGroup();
		for (Machine m : g.getMachineInGroup())
			if (!typesInGroup.contains(m.getCurrentType()))
				for (Job j: g.getJobsInGroup()){
					Changeover c=getChangeover().get(Changeover.generateKey(j.getType(), m.getCurrentType()));
					if (c!=null)
						g.getChangeover().add(c);
					//	System.out.println("changeover added group "+getId()+"= "+Constants.CHANGEOVER+"("+c.machine+","+c.type1+","+c.type2+","+c.length+").");
					}
	}
		
	
	
	
	
	
	@Override
	public HashMap<String, Changeover> getChangeover() {
		return changeover;
	}

	

	@Override
	public int getLatestFinishTime() {
		// TODO Auto-generated method stub
		int max=currentTime;
		for (Machine m: getMachines().values())
			if(m.getLastScheduledJob()!=null && m.getLastScheduledJob().getFinishAt() > max)
				max=m.getLastScheduledJob().getFinishAt();
		
		return max;
				
	
	}

	
	
	

	public int getInitialTardiness() {
		int tardiness = 0;
		for (Job j : jobs.values()) {
			// if the currentTime is bigger than the deadline, it means that we
			// are late
			if (!j.isAlreadyFinished()  && j.hasDeadline())
				tardiness += Math.max(currentTime - j.getDeadline(),
						0);
		}
		return tardiness;
	}
	
	
	public int getTardiness() {
		int tardiness = 0;
		for (Job j : jobs.values()) {
			// if the currentTime is bigger than the deadline, it means that we
			// are late
			if (!j.isAlreadyFinished()&& j.hasDeadline())
				tardiness += Math.max(j.getFinishAt()
						- j.getDeadline(), 0);
		}
		return tardiness;
	}
	
	
	public int getMakeSpan(){
		return getLatestFinishTime()-currentTime;
	}

	
	

	
	public int calculateIdleTime(){
		int finalPoint=0;
		int sumJobsLength=0;
		int sumIdle=0;
		for (Machine m : machines.values()) {
			// if the currentTime is bigger than the deadline, it means that we
			// are late
			for (Job j : m.getScheduledJobs()){
				
				if(j.getFinishAt() > finalPoint)
					finalPoint= j.getFinishAt();
				//System.out.println("final: machine "+m.getId()+" job "+j.getId()+"="+finalPoint);
				sumJobsLength+=j.getPossibleMachines().get(m);//gets the length of the job in the machine in which it is assigned
				//System.out.println("sumJobsLength: machine "+m.getId()+" job "+j.getId()+"="+sumJobsLength);
			}
			if (finalPoint!=0)//means that there are jobs in that machine
				sumIdle+=finalPoint-sumJobsLength-currentTime;
			//re-initialize for the next machine
			finalPoint=0;
			sumJobsLength=0;
		}
		return sumIdle;
		
	}
	
	
	@Override
	public int calculateChangeover(){
		int sum=0;
		for (Machine m : machines.values()) 
			for (Job j : m.getScheduledJobs())
				//finish and start comprehend the changeover time 
				if(!j.isCurrentJob() && !j.isAlreadyFinished() )
					sum+=j.getFinishAt()-j.getStartsAt()-j.getPossibleMachines().get(m);
				
		return sum;
		
	}
	
	
	
	/**
	 * calculates the sum of all the job length in the machine they are assigned to
	 * @generated
	 */
	public double calculateLengthSum(){
		double sumJobsLength=0;
			for (Job j : jobs.values())
				if (!j.isAlreadyFinished())
					sumJobsLength+=j.getPossibleMachines().get(j.getAssigned2Machine());
			return sumJobsLength;
		
	}
	
	/**
	 * calculates the sum of all the job duration in the machine they are assigned to
	 * the duration is the length of the job + the changeover (if present)
	 * @generated
	 */
	public double calculateDurationSum(){
		double sumJobsLength=0;
			for (Job j : jobs.values())
				if (!j.isAlreadyFinished())
					sumJobsLength+=j.getFinishAt()-j.getStartsAt();
			return sumJobsLength;
		
	}
	
	
	



	
	
	
} //SchedulerImpl
