/*
 * File    : ThreadPool.java
 * Created : 21-Nov-2003
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.core3.util;

/**
 * @author parg
 *
 */

import java.util.ArrayList;
import java.util.List;


public class 
ThreadPool 
{
	private static final boolean	NAME_THREADS = Constants.IS_CVS_VERSION && System.getProperty( "az.thread.pool.naming.enable", "true" ).equals( "true" ); 
	
	private static final boolean	LOG_WARNINGS	= false;
	private static final int		WARN_TIME		= 10000;
	
	private static List		busy_pools			= new ArrayList();
	private static boolean	busy_pool_timer_set	= false;
	
	private static boolean	debug_thread_pool;
	private static boolean	debug_thread_pool_log_on;
	
	static{
		if ( System.getProperty("transitory.startup", "0").equals("0")){

			AEDiagnostics.addEvidenceGenerator(
				new AEDiagnosticsEvidenceGenerator()
				{
					public void
					generate(
						IndentWriter		writer )
					{
						writer.println( "Thread Pools" );
						
						try{
							writer.indent();

							List	pools;	
							
							synchronized( busy_pools ){
								
								pools	= new ArrayList( busy_pools );
							}
							
							for (int i=0;i<pools.size();i++){
								
								((ThreadPool)pools.get(i)).generateEvidence( writer );
							}
						}finally{
						
							writer.exdent();
						}
					}
				});
		}
	}
	
	private static ThreadLocal		tls	= 
		new ThreadLocal()
		{
			public Object
			initialValue()
			{
				return( null );
			}
		};


	void releaseManual(ThreadPoolTask toRelease) {
		if( !toRelease.canManualRelease()){
			throw new IllegalStateException("task not manually releasable");
		}
		
		synchronized( this ){
		
			long elapsed = SystemTime.getMonotonousTime() - toRelease.worker.run_start_time;
			if (elapsed > WARN_TIME && LOG_WARNINGS)
				DebugLight.out(toRelease.worker.getWorkerName() + ": terminated, elapsed = " + elapsed + ", state = " + toRelease.worker.state);
			
			if ( !busy.remove(toRelease.worker)){
				
				throw new IllegalStateException("task already released");
			}
			
			// if debug is on we leave the pool registered so that we
			// can trace on the timeout events
			
			if (busy.size() == 0 && !debug_thread_pool){
				
				synchronized (busy_pools){
				
					busy_pools.remove(this);
				}
			}
			
			if ( busy.size() == 0){
				
				if ( reserved_target > reserved_actual ){
					
					reserved_actual++;
					
				}else{
					
					thread_sem.release();
				}
			}else{
				
				new threadPoolWorker();
			}
		}

	}
}
