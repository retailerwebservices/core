package org.jimmutable.core.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

/**
 * An abstract base class used to provide uniform behavior of runnable(s).
 * 
 * Every operation evaluates to a result (OperationRunnable.Result). The only
 * valid results are SUCCESS (the operation worked), ERROR (the operation did
 * not succeed because it encountered an error) and STOPPED (the runnable was
 * stopped by another thread before it could finish)
 * 
 * @author jim.kane
 *
 */
abstract public class OperationRunnable implements Runnable
{
	volatile private Result result = null;
	volatile State state = State.AWAITING_START;
	volatile private long start_time = -1;

	
	/**
	 * Check to see if the operation has a result
	 * 
	 * @return True if the operation has finished running, false otherwise
	 */
	final public boolean hasResult() 
	{ 
		return result != null; 
	}
	
	/**
	 * Get the result of the operation.
	 * 
	 * @param default_value
	 *            The value to return if the operation has not finished yet.
	 * @return The result of the operation, or default_value if the operation
	 *         has not finished yet.
	 */
	final public Result getOptionalResult(Result default_value) 
	{ 
		return result != null ? result : default_value; 
	}
	
	/**
	 * Check to see if the operation has a start time (i.e. has it started
	 * running)
	 * 
	 * @return True if the operation has started and has a start time, false
	 *         otherwise
	 * 
	 */
	final public boolean hasStartTime() 
	{ 
		return Optional.has(start_time, -1); 
	}
	
	/**
	 * Get the start time of the operation
	 * 
	 * @param default_value
	 *            The value to return if the operation has not started yet
	 *            (hence, no start time)
	 * @return The start time of the operation, or default_value if the
	 *         operation has not started yet.
	 */
	final public long getOptionalStartTime(long default_value) 
	{ 
		return Optional.getOptional(start_time, -1, default_value); 
	}
	
	/**
	 * Get the current running time of the operation
	 * 
	 * @param default_value
	 *            The value to return if the operation has not started yet
	 *            (hence, no start time)
	 * @return The current running time
	 */
	final public long getOptionalRunTime(long default_value) 
	{ 
		if ( !Optional.has(start_time, -1) ) return default_value;
		return System.currentTimeMillis()-start_time; 
	}
	
	
	/**
	 * Get the state of the operation
	 * 
	 * @return The state of the operation
	 */
	final public State getSimpleState() 
	{ 
		return state; 
	}
	
	/**
	 * Check to see if the operation is in a particular state
	 * 
	 * @param state_to_check
	 *            The state to check
	 * @return True if the operation is in the state (state_to_check), false
	 *         otherwise.
	 */
	final public boolean isInState(State state_to_check) 
	{
		if ( state_to_check == null ) return false;
		return state == state_to_check;
	}
	
	/**
	 * Signal the operation that it should stop.
	 * 
	 * Calling stop() does *NOT* halt the thread running the operation. It
	 * simply guarantees that any future calls to shouldStop() return true. Well
	 * behaved operations call shouldStop() regularly and will quickly stop
	 * after stop() is called.
	 */
	final public void stop() 
	{ 
		if ( isInState(State.STOPPING) ) return; // nothing to do
		if ( isInState(State.FINISHED) ) return; // nothing to do, already stopped (finished)
		
		state = State.STOPPING;
	}
	
	/**
	 * Check if the operation shouldStop()
	 * 
	 * Authors of operation(s) should routinely check to see if they have been
	 * signaled to stop and halt (returning Result.STOPPED)
	 * 
	 * @return True if the operation should stop, false otherwise.
	 */
	final public boolean shouldStop()
	{
		if ( isInState(State.STOPPING) ) return true;
		if ( isInState(State.FINISHED) ) return true;
		
		return false;
	}
	
	/**
	 * 
	 * All OperationRunnable implementations use the same run method (hence the
	 * final). The default implementation simply calls performOperation() and
	 * provides nice handling for state changes and any exceptions thrown by
	 * performOperation (exceptions are printed and the operation resutls in
	 * Result.ERROR)
	 * 
	 */
	final public void run()
	{
		state = State.RUNNING;
		start_time = System.currentTimeMillis();
		
		try
		{
			if ( shouldStop() )
			{
				result = Result.STOPPED;
				return;
			}
			
			result = performOperation();
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result = Result.ERROR;
			return;
		}
		finally
		{
			state = State.FINISHED;
		}
	}
	
	/**
	 * Do the work associated with the operation, returning the appropriate
	 * result. Any exceptions thrown will be printed and will result in the
	 * operation evaluating to Result.ERROR
	 * 
	 * @return The result of the operation (SUCCESS, ERROR, or STOPPED)
	 * @throws Exception
	 */
	abstract protected Result performOperation() throws Exception;
	
	static public enum Result
	{
		SUCCESS,
		ERROR,
		STOPPED;
	}
	
	static public enum State
	{
		AWAITING_START,
		RUNNING,
		STOPPING,
		FINISHED;
	}
	
	/**
	 * Execute an operation (in this thread).
	 * 
	 * @param runnable
	 *            The OperationRunnable to execute
	 * @param default_value
	 *            The Result to return in the event that an error occurs while
	 *            executing the operation
	 *            
	 * @return The result of the operation
	 */
	static public Result execute(OperationRunnable runnable, Result default_value)
	{
		Validator.notNull(runnable, "runnable");
		
		try
		{
			runnable.run();
			return runnable.getOptionalResult(default_value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return default_value;
		}
	}
	
	/**
	 * Execute an operation in a separate thread. The current thread is used to
	 * execute the monitoring of the operation. This function will not return
	 * until the operation is completed.
	 * 
	 * @param runnable
	 *            The operation to perform
	 * @param heartbeat_interval
	 *            The approximate waiting interval between calls to
	 *            monitor.onOperationMonitorHeartbeat(runnable)
	 * @param monitor
	 *            The (optional, can be null) monitor for the operation
	 * @param default_value
	 *            The result to return in the event of an (unexpected) error.
	 * @return The result of the operation, or default_value if an unexpected
	 *         error occurs.
	 *         
	 */
	static public Result executeWithMonitor(OperationRunnable runnable, long heartbeat_interval, OperationMonitor monitor, Result default_value)
	{
		Validator.notNull(runnable);
		Validator.min(heartbeat_interval, 50);
		
		
		ExecutorService single_thread = Executors.newSingleThreadExecutor();
		single_thread.submit(runnable);
		
		try 
		{ 
			while(true)
			{
				if ( runnable.hasResult() ) break;
				
				try { Thread.currentThread().sleep(heartbeat_interval); } catch(Exception e) { }
				
				if ( monitor != null )
					monitor.onOperationMonitorHeartbeat(runnable);
				
			}
			single_thread.shutdown();
			
			return runnable.getOptionalResult(default_value);
		} 
		catch(Exception e) 
		{ 
			e.printStackTrace(); 
			return default_value; 
		}
	}
}
