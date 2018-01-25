package org.jimmutable.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSinkExecutor<T> implements Sink
{
	private ExecutorService pool = Executors.newCachedThreadPool();
	private Sink sink;
	
	
	public ThreadPoolSinkExecutor(Sink<T> sink)
	{
		this.sink = sink;
	}

	
	public void onEmit( Object value )
	{
		pool.submit(new Runnable()
				{
					public void run() { sink.onEmit(value); }
				}
		);
	}
	
	
}
