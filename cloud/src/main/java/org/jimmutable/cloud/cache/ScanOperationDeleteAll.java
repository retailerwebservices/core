package org.jimmutable.cloud.cache;

/**
 * A scan operation that simply deletes all keys matched
 * 
 * To use this operation, simply use ScanOperationDeleteAll.OPERATION
 * 
 * @author kanej
 *
 */
public class ScanOperationDeleteAll implements ScanOperation
{
	static public final ScanOperationDeleteAll OPERATION = new ScanOperationDeleteAll();
	
	private ScanOperationDeleteAll() {}
	
	public void performOperation( ICache cache, CacheKey key )
	{
		cache.delete(key);
	}

}
