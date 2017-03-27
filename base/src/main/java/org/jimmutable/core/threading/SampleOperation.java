package org.jimmutable.core.threading;

import org.jimmutable.core.utils.Validator;


/**
 * A dummy OperationRunnable that will run for a specified period of time,
 * returning a specified result.
 * 
 * @author jim.kane
 *
 */
final public class SampleOperation extends OperationRunnable
{
	private long run_time;
	private boolean success;
	
	/**
	 * Create a new sample operation
	 * 
	 * @param run_time
	 *            The run time of the operation (in milliseconds)
	 * @param success
	 *            The return value (true = Result.SUCCESS, false = Result.ERROR)
	 */
	public SampleOperation(long run_time, boolean success)
	{
		Validator.min(run_time, 0);
		
		this.run_time = run_time;
		this.success = success;
	}

	
	/**
	 * Run the sample operation.
	 * 
	 * The operation will sleep for the specified number of milliseconds
	 * (run_time) and then return the specified result (SUCCESS or ERROR)
	 */
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) return Result.STOPPED;
		
		Thread.currentThread().sleep(run_time);
		
		if ( shouldStop() ) return Result.STOPPED;
		
		return success ? Result.SUCCESS : Result.ERROR;
	}
}
