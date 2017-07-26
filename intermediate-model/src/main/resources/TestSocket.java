import java.net.*;

public class ABC {

    ServerSocket s = new Socket();
    ServerSocket s_s = new Socket();
    URLConnection sr = new URLConnection();
    URLConnection sr2 = new URLConnection();
    Socket noTime = new Socket();
    Socket jupTime = new Socket();
    int time = System.currentTimeMillis();
    int noTime = 0;

    public ABC(){
        super();
        super.clone();
        s.setSoTimeout(1100);
        sr.setReadTimeout(100);
        jupTime.setSoTimeout(1000);
    }

    public static void
    connectWithTimeouts( final URLConnection connection )
            throws IOException
    {
        connection.setConnectTimeout(100);
        connection.connect();  //should trigger a time cnst
    }

    public void m1(){
        s.accept(); //should trigger a time cnst
    }

    public void m2(){
        s_s.accept(); //should NOT trigger a time cnst
    }

    public void m3(){
        s_s.setSoTimeout(1100);
        s_s.accept(); //should trigger a time cnst
    }

    public static void
    connectWithTimeout(final URLConnection connection)
            throws IOException
    {
        connectWithTimeouts(connection);
    }

    public void m4(){
        ABCD d = new DatagramSocket();
        DatagramSocket d = new DatagramSocket();
        d.setSoTimeout(100);
        d.receive( new DatagramPacket(p) );
    }

    public void m5(){
        DatagramSocket d = new DatagramSocket();
        //d.setSoTimeout(100);
        d.receive( new DatagramPacket(p) );
    }

    public void m6(){
        sr.getInputStream().read();
        jupTime.getInputStream().read();
        noTime.getInputStream().read();

    }

    public int getTime(){
        return System.currentTimeMillis();
    }

    public int getTime1(){
        return time;
    }

    public int getTime2(){
        return notime;
    }

    public void m7(){
        int now = getTime();
        if( now < 10) {
            //time cnst
        }
    }

    public void m8(){
        int now = getTime1();
        if( now < 10) {
            //time cnst
        }
    }

    public void m8(){
        int now = getTime2();
        if( now < 10) {
            //time cnst
        }
    }

}