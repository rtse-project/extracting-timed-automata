package fogassistant2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;

public class ThresholdSetting extends ActionBarActivity implements SensorEventListener {
	
	private float[] data = new float[ApplicationUtility.nWindow];
	private float[] msg = new float[ApplicationUtility.nWindow];
	private ThProcessor p_th;
	private SensorManager sm;
	private Sensor mAccelerometer;
	private TextView th_info;
	private Button start;
	private Button stop;
	private int i;
	private long tStart;
	private long tStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threshold_setting);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		start = (Button) findViewById(R.id.start_button);
		stop = (Button) findViewById(R.id.stop_button);
		th_info = (TextView) findViewById(R.id.th_info);
		if (savedInstanceState == null || !savedInstanceState.containsKey("key")){
			p_th = new ThProcessor();
			stop.setEnabled(false);
		} else {
			p_th = savedInstanceState.getParcelable("key");
			sm.registerListener(this, mAccelerometer, ApplicationUtility.Ts);
			start.setEnabled(savedInstanceState.getBoolean("start button"));
			stop.setEnabled(savedInstanceState.getBoolean("stop button"));
		}
		
		Log.d(ApplicationUtility.APPTAG, "numero elementi: "+p_th.FI.size());
	}
	
	public void startAcquisition(View view){
		p_th.initialization();
        i=0;
		sm.registerListener(this, mAccelerometer, ApplicationUtility.Ts);
		tStart = System.currentTimeMillis();
		start.setEnabled(false);
		stop.setEnabled(true);
		th_info.setText("Acquisizione in corso...");
	}
	
	public void stopAcquisition(View view) throws IOException{
		tStop = System.currentTimeMillis();
		sm.unregisterListener(this);
		start.setEnabled(true);
		if ((tStop-tStart)/1000 <= 20){
			th_info.setText("Acquisizione breve");
			Log.d(ApplicationUtility.APPTAG, "numero elementi adesso: "+p_th.FI.size());
		} else {
			float[] fi_en_th = p_th.thresholdComputing();
			th_info.setText(Float.toString(fi_en_th[0]));
			Log.d(ApplicationUtility.APPTAG, "numero elementi adesso: "+p_th.FI.size());
			String filename = "Soglia";
			String string = Float.toString(fi_en_th[0]);
			FileOutputStream os;
			os = openFileOutput(filename, Context.MODE_PRIVATE);
			os.write(string.getBytes());
			os.close();
		}
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.threshold_setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		data[i++]=event.values[2];
		if (i==ApplicationUtility.nWindow){
			for (int k=0; k<ApplicationUtility.nWindow; k++)
			{
				msg[k]=data[k];
			}
			p_th.thresholdAlgorithm(msg);
			for (int k=0; k<ApplicationUtility.nWindow-ApplicationUtility.sliding; k++){
				data[k]=data[k+ApplicationUtility.sliding];
			}
			i=ApplicationUtility.nWindow-ApplicationUtility.sliding;
			
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putCharSequence("info", th_info.getText());
		outState.putParcelable("key", p_th);
		outState.putBoolean("start button", start.isEnabled());
		outState.putBoolean("stop button", stop.isEnabled());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState){
		super.onRestoreInstanceState(savedState);
		th_info.setText(savedState.getCharSequence("info"));
		p_th = savedState.getParcelable("key");
	}
	
	@Override
    protected void onStop() {
		super.onStop();
		if (sm!=null){
			sm.unregisterListener(this);
		}
	}
	
}
