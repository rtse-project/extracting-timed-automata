package fogassistant2;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class Functions {
	
	public static void getStorageDir(String fileName) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
		if(!file.exists()){
			try{
				file.mkdirs();
				Log.d(ApplicationUtility.APPTAG, "Directory: " + file);
			} catch (Exception e){
				Log.e(ApplicationUtility.APPTAG, "Errore");
			}
		}
		
		try {
			FileOutputStream fOut = new FileOutputStream(new File(file,"DataLogger.json"));
			FileOutputStream out = new FileOutputStream(new File(file,"Acc.txt"),true);
			out.close();
			fOut.close();
		} catch (Exception e) {
			Log.e(ApplicationUtility.APPTAG, "Problemi");
		}
		
	}
	
public static float[] fi_computing(float[] spectrum){
		
		float loco_en = 0;
		for (int i = ApplicationUtility.loco_band[0]; i<ApplicationUtility.loco_band[1]; i++){
			loco_en = loco_en + spectrum[i];
		}
		
		float freeze_en = 0;
		for (int i = ApplicationUtility.freeze_band[0]; i<ApplicationUtility.freeze_band[1]; i++){
			freeze_en = freeze_en + spectrum[i];
		}
		
		float[] par = new float[2];
		
		par[0] = freeze_en/loco_en;
		par[1] = freeze_en+loco_en;
		
		return par;
		
	}

public static float[] mean_std(float[] vec){
	
	int n = vec.length;
	float sum = 0;
	for (int j=0; j<n; j++){
		sum = sum + vec[j];
	}
	float mean = sum/n;
	float appo = 0;
	for (int j=0; j<n; j++){
		appo = appo + (vec[j]-mean)*(vec[j]-mean);
	}
	float std = (float) Math.sqrt(appo/n);
	float[] res = {mean,std};
	return res;
	
}

public static float[] butter(float[] d){
	int k;
	int j;
	float[] a={(float) 1.0,(float) -3.6717,(float) 5.0680,(float) -3.1160,(float) 0.7199};
	float[] b={(float) 1.329372889875141e-05,
			(float) 5.317491559500565e-05,
			(float) 7.976237339250847e-05,
			(float) 5.317491559500565e-05,
			(float) 1.329372889875141e-05};
	float[] y = new float[d.length];
	y[0] = b[0]*d[0];
	for (k=1;k<5;k++){
		y[k]=(float)0.0;
		for (j=0;j<k+1;j++){
			y[k]=y[k]+b[j]*d[k-j];
		}
		
		for (j=0;j<k;j++){
			y[k]=y[k]-a[j+1]*y[k-j-1];
		}
	}
	
	for (k=5;k<d.length;k++){
		y[k]=(float)0.0;
		for (j=0;j<5;j++)
			y[k]=y[k]+b[j]*d[k-j];
		for (j=0;j<4;j++)
			y[k]=y[k]-a[j+1]*y[k-j-1];
	}
	
	for (k=0;k<d.length;k++){
		d[k]=y[d.length-k-1];
	}
	for (k=0;k<d.length;k++){
		y[k]=d[k];
	}
	
	y[0]=b[0]*d[0];
	for (k=1;k<5;k++){
		y[k]=(float)0.0;
		for (j=0;j<k+1;j++){
			y[k]=y[k]+b[j]*d[k-j];
		}
		
		for (j=0;j<k;j++){
			y[k]=y[k]-a[j+1]*y[k-j-1];
		}
	}
	
	for (k=5;k<d.length;k++) {
		y[k]=(float)0.0;
		for (j=0;j<5;j++)
			y[k]=y[k]+b[j]*d[k-j];
		for (j=0;j<4;j++)
			y[k]=y[k]-a[j+1]*y[k-j-1];
	}
	
	for (k=0;k<d.length;k++)
		d[k]=y[d.length-k-1];
	for (k=0;k<d.length;k++)
		y[k]=d[k];
	
	return y;
}

}
