package debugger;

import server.helper.PropertiesFileReader;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Debugger {
    private static Debugger instance = null;
    private int port = 0;
    private List<String> ip = new ArrayList<>();
    ServerDebugger server;
    String name;

    public static synchronized Debugger getInstance(){
        if(instance == null) {
            instance = new Debugger();
        }
        return instance;
    }

    public void setName(String name) {
        OutputLogs.getInstance().setName(name);
    }

    private Debugger() {
        try {
            //Start the server
            server = new ServerDebugger();
            port = server.getPort();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));

            // Print info
            System.out.println("Version : " + PropertiesFileReader.getGitSha1());
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String ips = i.getHostAddress();
                    ip.add(ips);
                    System.out.println(String.format("Loaded on %s:%d", ips , port));
                }
            }
        } catch (Exception e) {
            System.err.println("Cannot instantiate the debugger");
        }
    }

    public String getName() {
        return name;
    }

    public void stop(){
        server.stop();
    }

    public void log(String msg){
        OutputLogs.getInstance().add(msg);
    }
}
