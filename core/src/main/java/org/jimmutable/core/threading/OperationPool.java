package org.jimmutable.core.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jimmutable.core.utils.Validator;

final public class OperationPool extends OperationRunnable
{
	private List<OperationRunnable> seed_operations = new ArrayList();
	private int thread_count;
	
	private List<OperationRunnable> all_tasks = new ArrayList();
	private ExecutorService thread_pool;
	
	public OperationPool(OperationRunnable seed_operation, int thread_count)
	{
		Validator.notNull(seed_operation); 
		Validator.min(thread_count, 1);
		
		
		this.seed_operations.add(seed_operation);
		this.thread_count = thread_count;
	}
	
	public OperationPool(Collection<OperationRunnable> seed_operations, int thread_count)
	{
		Validator.notNull(seed_operations); 
		Validator.containsNoNulls(seed_operations);
		Validator.min(thread_count, 1);
		
		this.seed_operations.addAll(seed_operations);
		this.thread_count = thread_count;
	}
	
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) return Result.STOPPED;
		
		thread_pool = Executors.newFixedThreadPool(thread_count);
		
		for ( OperationRunnable operation : seed_operations )
		{
			submitOperation(operation);
		}
		
		while(true)
		{
			if ( shouldStop() ) break;
			if ( areAnyTasksWithResult(Result.ERROR) ) break;
			if ( areAllTasksInState(State.FINISHED) ) break;
			
			try { Thread.currentThread().sleep(500); } catch(Exception e) { e.printStackTrace(); }
		}
		
		stopAllTasks();
		thread_pool.shutdown(); // shutdown the thread pool as well, no more requests will be accepted
		
		if ( areAnyTasksWithResult(Result.ERROR) ) return Result.ERROR;
		if ( areAllTasksInState(State.FINISHED) && areAllTasksWithResult(Result.SUCCESS) ) return Result.SUCCESS;
		
		return Result.STOPPED;
	}
	
	public void stopAllTasks()
	{
		synchronized(all_tasks) 
		{
			for ( OperationRunnable runnable : all_tasks )
				runnable.stop();
		}
	}
	
	public void submitOperation(OperationRunnable operation)
	{
		if ( operation == null ) return;
		if ( thread_pool.isShutdown() ) return; // can not accept the operation, the thread pool has been shutdown...
		
		if ( shouldStop() ) return; // don't accept any new tasks if this (parent) task shoudl stop...
		
		thread_pool.submit(operation);
		
		synchronized(all_tasks) 
		{
			all_tasks.add(operation);
		}
	}
	
	public boolean areAllTasksInState(State state)
	{
		if ( state == null ) return false;
		
		synchronized(all_tasks)
		{
			for ( OperationRunnable runnable : all_tasks )
			{
				if ( runnable.getSimpleState() != state ) return false;
			}
		}
		
		return true;
	}
	
	public boolean areAnyTasksInState(State state)
	{
		if ( state == null ) return false;
		
		synchronized(all_tasks)
		{
			for ( OperationRunnable runnable : all_tasks )
			{
				if ( runnable.getSimpleState() == state ) return true;
			}
		}
		
		return false;
	}
	
	public boolean areAnyTasksWithResult(Result result)
	{
		if ( result == null ) return false;
		
		synchronized(all_tasks)
		{
			for ( OperationRunnable runnable : all_tasks )
			{
				if ( runnable.getOptionalResult(null) == result ) return true;
			}
		}
		
		return false;
	}
	
	public boolean areAllTasksWithResult(Result result)
	{
		if ( result == null ) return false;
		
		synchronized(all_tasks)
		{
			for ( OperationRunnable runnable : all_tasks )
			{
				if ( runnable.getOptionalResult(null) != result ) return false;
			}
		}
		
		return true;
	}
}
