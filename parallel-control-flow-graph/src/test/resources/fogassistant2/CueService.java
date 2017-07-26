package fogassistant2;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;

@SuppressLint("SimpleDateFormat") public class CueService extends IntentService {

	public CueService() {
		super("CueService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.cue);
		mp.start();
		long tStart = System.currentTimeMillis();
		long tEnd;
		do{
			tEnd = System.currentTimeMillis();
		}while((tEnd-tStart)/1000.0<6.0);
		
		mp.stop();
			

	}

}
