package fogassistant2;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoggerService extends IntentService {

	public LoggerService() {
		super("LoggerService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		float[] par = intent.getFloatArrayExtra(ApplicationUtility.EXTRA_DATA);
		JSONObject a = new JSONObject();
		try {
			a.put("freeze index", par[0]);
			a.put("energy", par[1]);
			String p = a.toString();
			File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"FOGassistant");
			FileOutputStream os;
			try {
				os = new FileOutputStream(new File(f,"DataLogger.json"),true);
				os.write(p.getBytes());
				//JsonWriter writer = new JsonWriter(new OutputStreamWriter(os,"UTF-8"));
				//writer.setIndent("   ");
				//writer.beginObject();
				//writer.name("freeze index").value(par[0]);
				//writer.name("energy").value(par[1]);
				//writer.endObject();
				//writer.close();
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
