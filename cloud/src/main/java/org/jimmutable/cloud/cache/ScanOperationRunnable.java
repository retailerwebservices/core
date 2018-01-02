package org.jimmutable.cloud.cache;

import org.jimmutable.core.utils.Validator;

public class ScanOperationRunnable implements Runnable
{
	private Cache cache;
	private CacheKey prefix;
	private ScanOperation operation;
	
	public ScanOperationRunnable(Cache cache, CacheKey prefix, ScanOperation operation)
	{
		Validator.notNull(cache, prefix, operation);
		
		this.cache = cache;
		this.prefix = prefix;
		this.operation = operation;
	}
	
	public void run()
	{
		cache.scan(prefix,  operation);
	}
}
