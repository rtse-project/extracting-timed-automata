package fogassistant2;

import android.os.Environment;
import android.util.Log;
import org.json.JSONArray;
import org.jtransforms.fft.FloatFFT_1D;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Processor {
	
	private float[] power_spectrum;
	private float[] time_data;
	private float[] frequency_data;
	
	public Processor(){
		power_spectrum = new float[ApplicationUtility.nWindow/2];
		time_data = new float[ApplicationUtility.nWindow];
		frequency_data = new float[ApplicationUtility.nWindow];
	}
	
	public float[] algorithm(float[] d,float[] d_ap) {
		
		for (int i=0; i<ApplicationUtility.nWindow; i++){
			time_data[i]=d[i];
			frequency_data[i]=d[i];
		}
		FloatFFT_1D fft = new FloatFFT_1D(ApplicationUtility.nWindow);
		fft.realForward(frequency_data);
		String[] sp = new String[power_spectrum.length];
		power_spectrum[0]=d[0]*d[0];
		sp[0]=""+power_spectrum[0];
		for (int j=1; j<ApplicationUtility.nWindow/2; j++)
		{
			power_spectrum[j] = (d[2*j]*d[2*j])+(d[2*j+1]*d[2*j+1]);
			sp[j]=""+power_spectrum[j];
		}
		
		float [] fi_en = Functions.fi_computing(power_spectrum);
		
		float[] ap_filt2hz = Functions.butter(d_ap);
		
		float[] hs = heelStrike(ap_filt2hz);
		float[] sl = zijstra(time_data,hs);
		
		boolean FOG = fogDetection(fi_en,sl,hs);
		
		JSONArray array = new JSONArray(Arrays.asList(sp));
		String s = array.toString();
		File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"FOGassistant");
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(f,"Spettri.txt"),true);
			os.write(s.getBytes());
			String nl = "\r\n";
			os.write(nl.getBytes());
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(ApplicationUtility.APPTAG, "fi: " + fi_en[0] + " en: " + fi_en[1]);
		
		return fi_en;
	}
	
	private float[] heelStrike(float[] ap){
		for (int i=1; i<ap.length-1; i++){
			if ((ap[i]>ap[i-1]) && (ap[i]>ap[i+1])){
				
			}
		}
		return ap;
		
	}
	
	private float[] zijstra(float[] vt,float[] hs){
		return hs;
	}
	
	private boolean fogDetection(float[] freq_param,float[] sl,float[] hs){
		return false;
	}

}
