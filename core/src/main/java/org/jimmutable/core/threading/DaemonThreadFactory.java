package org.jimmutable.core.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This class is a simple thread factory used to create daemon threads.
 * 
 * @author kanej
 *
 */

public class DaemonThreadFactory implements ThreadFactory
{	
	public DaemonThreadFactory() {}
	
	@Override
	public Thread newThread( Runnable r )
	{
		Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
	}
	
	static private class FiveSecondRunnable implements Runnable
	{
		public void run()
		{
			try
			{
				System.out.println("Sleeping for 5 seconds...");
				Thread.currentThread().sleep(5000);
				System.out.println("... Finished sleeping for 5 seconds!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Create a executor service that is a "daemon" (i.e. it will not stop a program
	 * from exiting)
	 * 
	 * @param num_threads
	 *            The number of threads to use in the executor
	 * @return
	 */
	static public ExecutorService createDaemonFixedThreadPool(int num_threads)
	{
		return Executors.newFixedThreadPool(num_threads, new DaemonThreadFactory());
	}
	
	/**
	 * Create a single thread executor that is a "daemon" (i.e. will not stop a
	 * program from exiting) 
	 * 
	 * Convenience method, equivalent to
	 * createDaemonSingleThreadExecutor(1)
	 * 
	 * @return
	 */
	static public ExecutorService createDaemonSingleThreadExecutor()
	{
		return createDaemonFixedThreadPool(1);
	}
}
