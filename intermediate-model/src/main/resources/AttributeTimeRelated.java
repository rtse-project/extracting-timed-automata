public class
TranscodeJobImpl
		implements TranscodeJob, DownloadWillBeRemovedListener
{
	private long                    started_on;
	private long                    paused_on;
	private long                    process_time;
	public int
	resume()
	{
		synchronized( this ){
			if ( state == ST_PAUSED ){
				state = ST_RUNNING;
				if ( paused_on > 0 && started_on > 0 ){
					process_time -= SystemTime.getMonotonousTime() - paused_on;
				}
			}else{
				return;
			}
		}
		queue.jobChanged( this, false, true );
		return paused_on;
	}

	public synchronized void
	pause()
	{
		synchronized( this ){
			if ( use_direct_input ){
				return;
			}
			if ( state == ST_RUNNING ){
				state = ST_PAUSED;
				paused_on = SystemTime.getMonotonousTime();
			}else{
				return;
			}
		}
		queue.jobChanged( this, false, true );
	}
}