package instrumentation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giovanni on 28/04/2017.
 */
public class Store {

    private static Map<String,List<StoreItem>> data = new HashMap<>();
    private static Store instance = null;


    private Store() {
    }

    public static synchronized Store getInstance() {
        if(instance == null){
            instance = new Store();
        }
        return instance;
    }

    public void addElement(StoreItem item){
        List<StoreItem> l;
        if(data.containsKey(item.javaClassName)){
            l = data.get(item.javaClassName);
        } else {
            l = new ArrayList<>();
        }
        l.add(item);
        data.put(item.javaClassName, l);
    }

    public boolean containClass(String className){
        return data.containsKey(className);
    }

    public List<StoreItem> getClass(String className){
        if(containClass(className))
            return data.get(className);
        return new ArrayList<>();
    }

    public int size() {
        return data.size();
    }

    public int totalSize() {
        int out = 0;
        for(String k : data.keySet()){
            out += data.get(k).size();
        }
        return out;
    }


}
