package fogassistant2;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
	
	private Button startFOGassistant;
	private float[] data;
	private float[] ap_data;
	private float[] msg;
	private float[] ap_msg;
	private SensorManager sm;
	private Sensor mAccelerometer;
	private int i;
	private Processor p;
	private Intent intent;
	private Intent logger;
	private boolean FOG;
	private ParameterDataSource datasource;
	private double durata;
	private int decimisec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startFOGassistant = (Button) findViewById(R.id.startFOGass);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (savedInstanceState != null && savedInstanceState.getBoolean("start FOG")==false){
        	startFOGassistant.setEnabled(savedInstanceState.getBoolean("start FOG"));
        	data = savedInstanceState.getFloatArray("data");
        	ap_data = savedInstanceState.getFloatArray("ap data");
        	i = savedInstanceState.getInt("i");
        	sm.registerListener(this, mAccelerometer , ApplicationUtility.Ts);
        	intent = new Intent(this, CueService.class);
        	logger = new Intent(this, LoggerService.class);
        } else {
        	Functions.getStorageDir("FOGassistant");
        	data = new float[ApplicationUtility.nWindow];
        	ap_data = new float[ApplicationUtility.nWindow];
        	i=0;
        }
        msg = new float[ApplicationUtility.nWindow];
        ap_msg = new float[ApplicationUtility.nWindow];
        p = new Processor();
        i=0;
        datasource = new ParameterDataSource(this);
		datasource.open();
		durata = 100;
    }
    
    public void startAssistant(View view){
    	sm.registerListener(this, mAccelerometer , ApplicationUtility.Ts);
    	startFOGassistant.setEnabled(false);
    	FOG = false;
    	intent = new Intent(this, CueService.class);
    	logger = new Intent(this, LoggerService.class);
    	decimisec=0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	thLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void thLogin(){
    	Intent intent_th = new Intent(this, ThresholdSettingLogin.class);
    	startActivity(intent_th);
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		ap_data[i]= event.values[1];
		data[i++]=event.values[2];
		if (i==ApplicationUtility.nWindow){
			String[] sdata = new String[data.length];
			for (int k=0; k<ApplicationUtility.nWindow; k++)
			{
				msg[k]=data[k];
				ap_msg[k] = ap_data[k];
				sdata[k]=""+data[k];
			}
			JSONArray array = new JSONArray(Arrays.asList(sdata));
			String s = array.toString();
			File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"FOGassistant");
			FileOutputStream os;
			try {
				os = new FileOutputStream(new File(f,"Acc.txt"),true);
				os.write(s.getBytes());
				String nl = "\r\n";
				os.write(nl.getBytes());
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			float[] param = p.algorithm(msg,ap_msg);
			datasource.insertParameter(param,decimisec);
			logger.putExtra(ApplicationUtility.EXTRA_DATA, param);
			startService(logger);
			if (durata!=100){
				durata = durata+0.4;
			}
			if (FOG==false && param[0]>10){
				FOG = true;
				startService(intent);
				durata = 0.4;
			}
			if (FOG == true && param[0]<10){
				datasource.insertFOG(durata,decimisec);
				FOG = false;
			}
			decimisec = decimisec+1;
			if (decimisec==5){
				decimisec = 0;
			}
			for (int k=0; k<ApplicationUtility.nWindow-ApplicationUtility.sliding; k++){
				data[k]=data[k+ApplicationUtility.sliding];
			}
			i=ApplicationUtility.nWindow-ApplicationUtility.sliding;
			//Log.d(ApplicationUtility.APPTAG, "primo camp: " + data[0] + " secondo camp: " + data[ApplicationUtility.sliding]);
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean("start FOG", startFOGassistant.isEnabled());
		outState.putFloatArray("data", data);
		outState.putFloatArray("ap data", ap_data);
		outState.putInt("i", i);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState){
		super.onRestoreInstanceState(savedState);
	}
	
	@Override
    protected void onPause() {
		super.onPause();
		
		if (intent!=null){
			stopService(intent);
		}
	}
    
    @Override
    protected void onStop() {
		super.onStop();
		if (sm!=null){
			sm.unregisterListener(this);
		}
		
		if (intent!=null){
			stopService(intent);
		}
		datasource.close();
	}
    
}
