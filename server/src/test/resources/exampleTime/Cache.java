public class Cache {
    int value;
    long lastRefresh;
    static final int MAX_TIME = 10*60*1000;//10minutes
    public Cache(){
        value = readValue();
        lastRefresh = System.currentTimeMillis();
    }
    public int read(){
        long now = System.currentTimeMillis();
        if(now - lastRefresh > MAX_TIME){
            value = readValue();
            lastRefresh = System.currentTimeMillis();
        }
        return value;
    }
    public void write(int v){
        value = v;
        lastRefresh = System.currentTimeMillis();
    }
}