package org.jimmutable.cloud.cache;

/**
 * Interface the defines an operation to perform on keys in a cache
 * 
 * @author kanej
 *
 */
public interface ScanOperation
{
	/**
	 * Perform the operation on the specified key.
	 * 
	 * No guarantee is made that key still exists in the cache (it is possible for a
	 * entry in a cache to expire or be deleted at any time). Never assume that the
	 * entry still exists just because performOperation is called on the entry.
	 * 
	 * @param cache
	 *            The cache performing the scan operation
	 * @param key
	 *            The key
	 */
	public void performOperation(Cache cache, CacheKey key);
}
