package uppaal.replaceParameter;

import instrumentation.TestInstrumentation;
import uppaal.NTA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giovanni on 03/05/2017.
 */
public class HandleReplacement {

    public static void createModelsFromTraces(String model, String traces, String dir){
        ReplaceTraces replacer = new ReplaceTraces(model);
        List<TraceItem> instances = ReadTraces.getTraces(traces);
        //info on thread ids
        List<Integer> threadIds = getThreads(instances);
        //Group by Thread ID the substitutions
        HashMap<Integer,List<TraceItem>> groupById = groupById(instances, threadIds);
        // create an instance of the model per thread
        for(Integer thid : threadIds){
            List<List<TraceItem>> versions = splitVersion(groupById.get(thid));
            int vId = 0;
            for(List<TraceItem> v : versions){
                vId++;
                NTA thModel = replacer.replace(v);
                //writer = new BufferedWriter(new FileWriter("graph.xal"));
                try {
                    thModel.writeXMLWithPrettyLayout(dir + "thread_" + thid + "_v" + vId + ".xml");
                } catch (Exception e) {
                    System.err.println("Cannot create file for thread #" + thid + "_v"+ vId);
                }
            }

        }
    }

    public static HashMap<Integer,List<TraceItem>> groupById(List<TraceItem> instances, List<Integer> threadIds) {
        HashMap<Integer,List<TraceItem>> groupById = new HashMap<>();
        //init with empty lists each thread
        for(Integer thid : threadIds){
            groupById.put(thid, new ArrayList<TraceItem>());
        }
        // add data
        for(TraceItem t : instances){
            int thId = t.getThreadID();
            List<TraceItem> vals = groupById.get(thId);
            vals.add(t);
        }
        return groupById;
    }

    public static List<Integer> getThreads(List<TraceItem> traces){
        List<Integer> threadIds = new ArrayList<>();
        for(TraceItem t : traces){
            int thId = t.getThreadID();
            if(!threadIds.contains(thId))
                threadIds.add(thId);
        }
        return threadIds;
    }


    public static List<List<TraceItem>> splitVersion(List<TraceItem> trace){
        List<List<TraceItem>> out = new ArrayList<>();
        List<TraceItem> tmp = new ArrayList<>();
        for(TraceItem elm : trace){
            //do we end the current method execution?
            if(elm.getVarName().equals(TestInstrumentation.endMethod)){
                if(tmp.size() > 0) {
                    out.add(tmp);
                    tmp = new ArrayList<>();
                }
            } else {
                //is a value already there?
                boolean f = false;
                //String varName = elm.getVarName();
                for(TraceItem visited : tmp){
                    if(visited.getLine() == elm.getLine() && visited.getVarName().equals(elm.getVarName())){
                        f = true;
                    }
                }
                //we come across the same var? -> update value and split
                if(f){
                    out.add(tmp);
                    List<TraceItem> cicleHead = new ArrayList<>();
                    for(TraceItem t : tmp){
                        if(t.getLine() == elm.getLine() && t.getVarName().equals(elm.getVarName())){
                            cicleHead.add(elm);
                        } else {
                            cicleHead.add(t);
                        }
                    }
                    tmp = cicleHead;
                } else {
                    tmp.add(elm);
                }
            }
        }
        if(tmp.size() > 0)
            out.add(tmp);
        return out;
    }

}
