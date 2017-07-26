import org.junit.Test;
import uppaal.replaceParameter.HandleReplacement;
import uppaal.replaceParameter.ReadTraces;
import uppaal.replaceParameter.TraceItem;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by giovanni on 04/05/2017.
 */
public class TestSplitTraces {

    //@SuppressWarnings(DuplicateCode)
    @Test
    public void TestSplit() {
        String traces = TestSplitTraces.class.getClassLoader().getResource("traces.txt").getFile();
        List<TraceItem> instances = ReadTraces.getTraces(traces);
        //info on thread ids
        List<Integer> threadIds = HandleReplacement.getThreads(instances);
        //Group by Thread ID the substitutions
        HashMap<Integer,List<TraceItem>> groupById = HandleReplacement.groupById(instances, threadIds);
        assertEquals(7, threadIds.size());
        int th = 287;
        List<TraceItem> trace = groupById.get(th);
        List<List<TraceItem>> out = HandleReplacement.splitVersion(trace);
        assertEquals(9, trace.size());
        assertEquals(7, out.size());
    }

    @Test
    public void TestSplitEndMethod() {
        String traces = TestSplitTraces.class.getClassLoader().getResource("traces_endMethod.txt").getFile();
        List<TraceItem> instances = ReadTraces.getTraces(traces);
        //info on thread ids
        List<Integer> threadIds = HandleReplacement.getThreads(instances);
        //Group by Thread ID the substitutions
        HashMap<Integer,List<TraceItem>> groupById = HandleReplacement.groupById(instances, threadIds);
        assertEquals(3, threadIds.size());
        assertEquals(12, threadIds.get(0).intValue());
        assertEquals(173, threadIds.get(1).intValue());
        assertEquals(287, threadIds.get(2).intValue());
        int th = 12;
        List<TraceItem> trace = groupById.get(th);
        assertEquals(16, trace.size());
        List<List<TraceItem>> out = HandleReplacement.splitVersion(trace);
        assertEquals(2, out.size());

        th = 173;
        trace = groupById.get(th);
        assertEquals(21, trace.size());
        out = HandleReplacement.splitVersion(trace);
        assertEquals(3, out.size());

        th = 287;
        trace = groupById.get(th);
        assertEquals(21, trace.size());
        out = HandleReplacement.splitVersion(trace);
        assertEquals(9, out.size());

    }



}


