package org.jimmutable.core.threading;


/**
 * This interface is implemented by classes that wish to monitor the ongoing
 * actions of an OperationRunnable.
 * 
 * Methods such as OperationRunnable.executeWithMonitor allow you to execute a
 * runnable where a monitors "heartbeat" function is called at a specified
 * frequency.
 * 
 * @author jim.kane
 *
 */
public interface OperationMonitor
{
	/**
	 * The function to execute on each monitor heartbeat. THis function runs in
	 * a seperate thread from the OperationRunnable, therefore the processing
	 * done in the heart beat is separate from and has no impact on the
	 * runnable.
	 * 
	 * @param runnable A reference to the runnable being monitored
	 */
	public void onOperationMonitorHeartbeat(OperationRunnable runnable);
}
