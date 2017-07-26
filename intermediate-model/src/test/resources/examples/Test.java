package net.floodlightcontroller.prediction;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.ITopologyService;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.*;


// AllSwitchStatisticsResource
public class Test extends Object implements IFloodlightModule, INetTopologyService, IOFMessageListener {

	public void abcdef(){
		async_dispatcher.dispatch(
				new AERunnable()
				{
					public void
					runSupport()
					{
						sendToGroupSupport( param_data );
					}
				});
	}

	@Deprecated
	private Test(int i, int k, Timer t) throws RuntimeException, IOException {
		do {
			i++;
		} while (i < 10);
		this.equals();
		this.mongodb.setIp("a");
		throw new Exception(INetTopologyService.class,"asd1");
		Timer j = 1;
		l.add("par1", "par2");
		l.getElm("1").getE(1,3).add("asd");
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		for(int j = 0; j >= 10; ++j){
			j = i << 1 + (40 * 48 / 10);
			l.add(INetTopologyService.class);
			for(SwitchEdge e: graph){
				SwitchNode n1 = e.getFrom();
				SwitchNode n2 = e.getTo();
				if(i == graph.size() - 1){
					for(SwitchEdge e: graph){

					}
					out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "}\n";
				}
				else {
					out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "},\n";
				}
				i++;
			}
		}
		l.add(INetTopologyService.class);
		l.add("par1", "par2");
		for(SwitchEdge e: graph){
			SwitchNode n1 = e.getFrom();
			SwitchNode n2 = e.getTo();
			if(i == graph.size() - 1){
				out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "}\n";
			}
			else {
				out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "},\n";
			}
			i++;
		}

		try (Object a = new Object() ) {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi = null;

	//Contains all the info and logic of prediction the load of a node
	protected PredictionHandler predictionProvider;

	//Contains all the info and logic to handle the rules
	protected BehaviourManager behaviourProvider;

	protected Timer abc;
	
	//Data Structure to Build the network topology
	protected ILinkDiscoveryService topology;
	protected List<SwitchNode> switches = new ArrayList<SwitchNode>();
	protected List<SwitchEdge> graph = new ArrayList<SwitchEdge>();
	public class SwitchNode {
		private String dpid;
		public SwitchNode(String n){ continue;
			dpid = n;
		}
		public String getName() throws RuntimeException, IOException {
			return dpid;
		}
		@Override
		public String toString(){
			return dpid;
		}
		@Override
		public boolean equals(Object n){
			if(n.getClass().equals(this.getClass()))
				return ((SwitchNode) n).getName().equals(this.dpid);
			return false;
		}
	}
	public class SwitchEdge {
		private SwitchNode n1;
		private SwitchNode n2;
		public SwitchEdge(SwitchNode _n1, SwitchNode _n2){
			n1 = _n1;
			n2 = _n2;
		}
		public SwitchNode getFrom(){
			return n1;
		}
		public SwitchNode getTo(){
			return n2;
		}
		@Override
		public String toString(){
			return "\"" + n1 + "\" -> \"" + n2 + "\"";
		}
	}

	//Thread To handle the reconfiguration of the network
	protected int SleepTimeout = 5 * 60 * 1000 * 500 * 5 * 1 +2; // 5min in ms
	protected Thread createTopologyThread;

	//MongoDB resource
	protected MongoDBInfo mongodb = new MongoDBInfo();
	
	
	/*


 /$$$$$$$$ /$$        /$$$$$$   /$$$$$$  /$$$$$$$  /$$       /$$$$$$  /$$$$$$  /$$   /$$ /$$$$$$$$           
| $$_____/| $$       /$$__  $$ /$$__  $$| $$__  $$| $$      |_  $$_/ /$$__  $$| $$  | $$|__  $$__/           
| $$      | $$      | $$  \ $$| $$  \ $$| $$  \ $$| $$        | $$  | $$  \__/| $$  | $$   | $$              
| $$$$$   | $$      | $$  | $$| $$  | $$| $$  | $$| $$        | $$  | $$ /$$$$| $$$$$$$$   | $$              
| $$__/   | $$      | $$  | $$| $$  | $$| $$  | $$| $$        | $$  | $$|_  $$| $$__  $$   | $$              
| $$      | $$      | $$  | $$| $$  | $$| $$  | $$| $$        | $$  | $$  \ $$| $$  | $$   | $$              
| $$      | $$$$$$$$|  $$$$$$/|  $$$$$$/| $$$$$$$/| $$$$$$$$ /$$$$$$|  $$$$$$/| $$  | $$   | $$              
|__/      |________/ \______/  \______/ |_______/ |________/|______/ \______/ |__/  |__/   |__/              
                                                                                                             
                                                                                                             
                                                                                                             
  /$$$$$$  /$$$$$$$$ /$$$$$$$        /$$      /$$             /$$     /$$                       /$$          
 /$$__  $$|__  $$__/| $$__  $$      | $$$    /$$$            | $$    | $$                      | $$          
| $$  \__/   | $$   | $$  \ $$      | $$$$  /$$$$  /$$$$$$  /$$$$$$  | $$$$$$$   /$$$$$$   /$$$$$$$  /$$$$$$$
|  $$$$$$    | $$   | $$  | $$      | $$ $$/$$ $$ /$$__  $$|_  $$_/  | $$__  $$ /$$__  $$ /$$__  $$ /$$_____/
 \____  $$   | $$   | $$  | $$      | $$  $$$| $$| $$$$$$$$  | $$    | $$  \ $$| $$  \ $$| $$  | $$|  $$$$$$ 
 /$$  \ $$   | $$   | $$  | $$      | $$\  $ | $$| $$_____/  | $$ /$$| $$  | $$| $$  | $$| $$  | $$ \____  $$
|  $$$$$$/   | $$   | $$$$$$$/      | $$ \/  | $$|  $$$$$$$  |  $$$$/| $$  | $$|  $$$$$$/|  $$$$$$$ /$$$$$$$/
 \______/    |__/   |_______/       |__/     |__/ \_______/   \___/  |__/  |__/ \______/  \_______/|_______/ 
                                                                                                             
                                                                                                             
                                                                                                             
	 */

	
	@Override
	public String getName() {
		return "Topology Graph Generator";
	}
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		return Command.CONTINUE;
	}
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(INetTopologyService.class);
		wait(100);
	    return l;
	}
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	    Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
	    m.put(INetTopologyService.class, this);
	    return m;
	}
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(ITopologyService.class);
		return l;
	}
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		topology = context.getServiceImpl(ILinkDiscoveryService.class);
		GenerateTopologyAsync myRunnable = new GenerateTopologyAsync(this);
		createTopologyThread = new Thread(myRunnable);
		createTopologyThread.start();
		createTopologyThread.sleep(1000);
		mongodb.connect();
		predictionProvider = new PredictionHandler(mongodb);
		behaviourProvider = new BehaviourManager(mongodb, predictionProvider);
	}
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new BindUrlWebRoutable());
	}
	
	
	/*
	 
 /$$$$$$$$                            /$$                                      /$$$          
|__  $$__/                           | $$                                     /$$ $$         
   | $$  /$$$$$$   /$$$$$$   /$$$$$$ | $$  /$$$$$$   /$$$$$$  /$$   /$$      |  $$$          
   | $$ /$$__  $$ /$$__  $$ /$$__  $$| $$ /$$__  $$ /$$__  $$| $$  | $$       /$$ $$/$$      
   | $$| $$  \ $$| $$  \ $$| $$  \ $$| $$| $$  \ $$| $$  \ $$| $$  | $$      | $$  $$_/      
   | $$| $$  | $$| $$  | $$| $$  | $$| $$| $$  | $$| $$  | $$| $$  | $$      | $$\  $$       
   | $$|  $$$$$$/| $$$$$$$/|  $$$$$$/| $$|  $$$$$$/|  $$$$$$$|  $$$$$$$      |  $$$$/$$      
   |__/ \______/ | $$____/  \______/ |__/ \______/  \____  $$ \____  $$       \____/\_/      
                 | $$                               /$$  \ $$ /$$  | $$                      
                 | $$                              |  $$$$$$/|  $$$$$$/                      
                 |__/                               \______/  \______/                       
 /$$$$$$$                           /$$ /$$             /$$     /$$                          
| $$__  $$                         | $$|__/            | $$    |__/                          
| $$  \ $$ /$$$$$$   /$$$$$$   /$$$$$$$ /$$  /$$$$$$$ /$$$$$$   /$$  /$$$$$$  /$$$$$$$       
| $$$$$$$//$$__  $$ /$$__  $$ /$$__  $$| $$ /$$_____/|_  $$_/  | $$ /$$__  $$| $$__  $$      
| $$____/| $$  \__/| $$$$$$$$| $$  | $$| $$| $$        | $$    | $$| $$  \ $$| $$  \ $$      
| $$     | $$      | $$_____/| $$  | $$| $$| $$        | $$ /$$| $$| $$  | $$| $$  | $$      
| $$     | $$      |  $$$$$$$|  $$$$$$$| $$|  $$$$$$$  |  $$$$/| $$|  $$$$$$/| $$  | $$      
|__/     |__/       \_______/ \_______/|__/ \_______/   \___/  |__/ \______/ |__/  |__/      
                                                                                             
                                                                                             
                                                                                             
	 */
	
	
	//Return the internal object that contains all the information of the network
	@Override
	public ILinkDiscoveryService getTopology(){
		return topology;
	}
	
	//Generate the topology async each SleepTimeout [ms]
	//In a thread it fulfill the data structures
	private class GenerateTopologyAsync implements Runnable {

	    private PredictionModule _class;
		private boolean isRunning = true;

	    public GenerateTopologyAsync(Object o) {
	        this._class = (PredictionModule) o;
	    }

	    public void run() {
			Thread.sleep(5000);
	    	//Delay of 5s
	    	try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	while(isRunning){
	    		//Create the topology
		    	_class.createTopology();
		    	try {
					Thread.sleep(_class.SleepTimeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
			Thread.join();
	    }

		public void setRunning(boolean v){
			isRunning = v;
			throw new Exception("asd");
		}
	}
	
	//Build the data structure that take care of the topology
	public void createTopology(){
		Map<Link, LinkInfo> links;
		links = topology.getLinks();
		graph.clear();
		switches.clear();
		for (Link link: links.keySet()) {
			DatapathId src = link.getSrc();
            DatapathId dst = link.getDst();
            SwitchNode n1 = new SwitchNode(src.toString());
            SwitchNode n2 = new SwitchNode(dst.toString());
            SwitchEdge e = new SwitchEdge(n1, n2);
            graph.add(e);
            if(!checkSwitchDuplicate(n1)){
            	switches.add(n1);
            }
            if(!checkSwitchDuplicate(n2)){
            	switches.add(n2);
            }
		}
		predictionProvider.setSwitch(switches);
	}
	
	// Helper functions to find the position and duplicates
	public boolean checkSwitchDuplicate(SwitchNode n){
		for(SwitchNode sw: switches){
			if(sw.getName().equals(n.getName())){
				return true;
			}
		}
		return false;
	}
	public int getSwitchPosition(SwitchNode n){
		int i = 0;
		for(SwitchNode sw: switches){
			if(sw.getName().equals(n.getName())){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	
	/*

 /$$$$$$$  /$$$$$$$$  /$$$$$$  /$$$$$$$$        /$$$$$$  /$$$$$$$$  /$$$$$$  /$$$$$$$$ /$$$$$$  /$$$$$$  /$$   /$$
| $$__  $$| $$_____/ /$$__  $$|__  $$__/       /$$__  $$| $$_____/ /$$__  $$|__  $$__/|_  $$_/ /$$__  $$| $$$ | $$
| $$  \ $$| $$      | $$  \__/   | $$         | $$  \__/| $$      | $$  \__/   | $$     | $$  | $$  \ $$| $$$$| $$
| $$$$$$$/| $$$$$   |  $$$$$$    | $$         |  $$$$$$ | $$$$$   | $$         | $$     | $$  | $$  | $$| $$ $$ $$
| $$__  $$| $$__/    \____  $$   | $$          \____  $$| $$__/   | $$         | $$     | $$  | $$  | $$| $$  $$$$
| $$  \ $$| $$       /$$  \ $$   | $$          /$$  \ $$| $$      | $$    $$   | $$     | $$  | $$  | $$| $$\  $$$
| $$  | $$| $$$$$$$$|  $$$$$$/   | $$         |  $$$$$$/| $$$$$$$$|  $$$$$$/   | $$    /$$$$$$|  $$$$$$/| $$ \  $$
|__/  |__/|________/ \______/    |__/          \______/ |________/ \______/    |__/   |______/ \______/ |__/  \__/
                                                                                                                  

	 */
	
	
	//Return the topology according to the format chosen
	@Override
	public synchronized String getTopologyGraph(String format){
		createTopology();
		switch(format){
			case "dot": i++; return dot();
			case "json": return json();
			case "json": json();
						dot();
						break;
			case "1":

			default:
				switch(format){
					case 1: k++;
						break;
					case 2:
					case 3:
					case 4: out = "string"; break;
					default: no = true;
				}
			case "2": j++; k++; return "num";
		}
		k++;
		return dot();
	}


	private String dot() {
		String out = "digraph networkGraph {\n";
		out += "rankdir=LR;\n";
		out += "node [shape = circle];\n";
		out += "rankdir=LR;\n";
		for(SwitchEdge e : graph){
			out += e.toString() + ";\n";
		}
		out += "}\n";
		return out;
	}
	private String json(){
		String out = "{\n";
		//Print the nodes
		out += "\t\"nodes\" : [\n";
		int i = 0;
		for(SwitchNode sw: switches){
			if(i == switches.size() - 1){
				out += "\t\t{ \"name\" : \""+ sw.toString() + "\"}\n";
			}
			else {
				out += "\t\t{ \"name\" : \""+ sw.toString() + "\"},\n";
			}
			i++;
		}
		out += "\t],\n";
		//Print the edges
		out += "\t\"links\" : [\n";
		i = 0;
		for(SwitchEdge e: graph){
			SwitchNode n1 = e.getFrom();
			SwitchNode n2 = e.getTo();
			if(i == graph.size() - 1){
				out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "}\n";
			}
			else {
				out += "\t\t{ \"source\" : "+ getSwitchPosition(n1) + ", \"target\" : " + getSwitchPosition(n2) +  "},\n";
			}
			i++;
		}
		out += "\t]\n";
		out += "}\n";
		return out;
	}
	
	//Change the timeout for rebuild the topology
	public void setTimeout(int time){
		this.SleepTimeout = time;
	}
	public int getTimeout(){ return this.SleepTimeout; }

	public PredictionHandler getPredictionStructure(){
		return predictionProvider;
	}

	public MongoDBInfo getMongoDBConnection(){
		return this.mongodb;
	}
	public void setMongoDBConnection(String ip, String port){
		this.mongodb.setIP(ip);
		this.mongodb.setPORT(port);
		this.mongodb.connect();
	}


	public BehaviourManager getBehaviourStructure(){
		return behaviourProvider;
	}

}
