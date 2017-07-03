package org.jimmutable.core.threading;

import org.jimmutable.core.threading.OperationRunnable.Result;
import org.jimmutable.core.utils.Validator;


/**
 * A dummy OperationRunnable that will stop another OperationRunnable after a
 * specified interval. If the operation_to_stop was signaled to stop, this
 * operation returns Result.SUCCESS.
 * 
 * 
 * @author jim.kane
 *
 */
final public class SampleStopOperation extends OperationRunnable
{
	private long run_time;
	private OperationRunnable operation_to_stop;
	
	
	/**
	 * Create a SampleStopOperation
	 * 
	 * @param run_time
	 *            The amount of time (in milliseconds) to wait before signalling
	 *            operation_to_stop to stop
	 * @param operation_to_stop
	 *            The operation to stop
	 */
	public SampleStopOperation(long run_time, OperationRunnable operation_to_stop)
	{
		Validator.min(run_time, 0);
		Validator.notNull(operation_to_stop);
		
		this.run_time = run_time;
		this.operation_to_stop = operation_to_stop;
	}

	
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) return Result.STOPPED;
		
		Thread.currentThread().sleep(run_time);
		
		if ( shouldStop() ) return Result.STOPPED;
		
		operation_to_stop.stop();
		
		return Result.SUCCESS;
	}
}
