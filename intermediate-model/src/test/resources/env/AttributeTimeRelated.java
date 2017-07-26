public class
TranscodeJobImpl
		implements TranscodeJob, DownloadWillBeRemovedListener
{
	private long                    started_on;
	private long                    paused_on;
	private long                    process_time;
	public void
	resume()
	{
		synchronized( this ){
			if ( state == ST_PAUSED ){
				state = ST_RUNNING;
				if ( paused_on > 0 && started_on > 0 ){
					process_time -= System.currentTimeMillis()  - paused_on;
				}
			}else{
				return;
			}
		}
		queue.jobChanged( this, false, true );
	}

	public void
	pause()
	{
		synchronized( this ){
			if ( use_direct_input ){
				return;
			}
			if ( state == ST_RUNNING ){
				state = ST_PAUSED;
				paused_on = System.currentTimeMillis() ;
			}else{
				return;
			}
		}
		queue.jobChanged( this, false, true );
	}
}