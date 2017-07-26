package fogassistant2;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import org.jtransforms.fft.FloatFFT_1D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("NewApi") public class ThProcessor implements Parcelable {
	
	private float[] power_spectrum;
	public List<myFloat> FI;
	public List<myFloat> EN;
	
	public ThProcessor(){
		power_spectrum = new float[ApplicationUtility.nWindow];
		FI = new ArrayList<myFloat>();
		EN = new ArrayList<myFloat>();
	}
	
	public void initialization(){
		FI.removeAll(FI);
		EN.removeAll(EN);
	}
	
    public void thresholdAlgorithm(float[] d){
		
		FloatFFT_1D fft = new FloatFFT_1D(ApplicationUtility.nWindow);
		fft.realForward(d);
		for (int j=0; j<ApplicationUtility.nWindow; j++)
		{
			power_spectrum[j] = d[j]*d[j];
		}
		
		float[] fi_en = Functions.fi_computing(power_spectrum);
		myFloat my_fi = new myFloat(fi_en[0]);
		myFloat my_en = new myFloat(fi_en[1]);
		FI.add(my_fi);
		EN.add(my_en);
	}
    
    @SuppressLint("NewApi") public float[] thresholdComputing(){
		
    	int n = FI.size();
		float fi[] = new float[n];
		int k=0;
		for (myFloat f : FI){
			fi[k++]=(f.getValue() != 0 ? f.getValue() : Float.NaN);
		}
		float[] mean_std_fi = Functions.mean_std(Arrays.copyOfRange(fi, Math.round(n/2-10), Math.round(n/2+10)));
		float fi_th = mean_std_fi[0] + mean_std_fi[1];
		
		float en[] = new float[n];
		k=0;
		for (myFloat f : EN){
			en[k++]=(f.getValue()!=0 ? f.getValue() : Float.NaN);
		}
		float[] mean_std_en = Functions.mean_std(Arrays.copyOfRange(en, Math.round(n/2-10), Math.round(n/2+10)));
		float en_th = mean_std_en[0] + mean_std_en[1];
		
		float[] fi_en_th = {fi_th,en_th};
		return fi_en_th;
		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloatArray(power_spectrum);
		dest.writeTypedList(FI);
		dest.writeTypedList(EN);
	}
	
	public static final Parcelable.Creator<ThProcessor> CREATOR = new Parcelable.Creator<ThProcessor>() {
		public ThProcessor createFromParcel(Parcel in){
			return new ThProcessor(in);
		}
		
		public ThProcessor[] newArray(int size){
			return new ThProcessor[size];
		}
	};
	
	private ThProcessor(Parcel in){
		in.readFloatArray(power_spectrum);
		in.readTypedList(FI, myFloat.CREATOR);
		in.readTypedList(EN, myFloat.CREATOR);
	}
	
	
	//Parcelable object myFloat
    public static class myFloat implements Parcelable {
		
		private float mData;

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(mData);
			
		}
		
		public static final Parcelable.Creator<myFloat> CREATOR = new Parcelable.Creator<myFloat>() {
			public myFloat createFromParcel(Parcel in) {
				return new myFloat(in);
			}
			
			public myFloat[] newArray(int size){
				return new myFloat[size];
			}
		};
		
		public myFloat(float f){
			this.mData = f;
		}
		
		private myFloat(Parcel in){
			mData = in.readFloat();
		}
		
		public float getValue(){
			return mData;
		}
		
	}

}
