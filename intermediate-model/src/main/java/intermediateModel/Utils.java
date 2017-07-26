package intermediateModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by giovanni on 14/03/2017.
 */
public class Utils {
    public static void send_email(String msg) {
        try {
            msg = URLEncoder.encode(msg, "UTF-8");
            String a= "http://sensarisvolti.altervista.org/send_email.php?msg=" + msg;
            URL url = new URL(a);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            br.read();
            br.close();
            br = null;
            conn = null;
            url = null;
        } catch (Exception e){
            //is not a problem if we can not send the email
        }
    }
}
