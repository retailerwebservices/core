package org.jimmutable.cloud.cache.redis;

import org.jimmutable.cloud.cache.CacheKey;

/**
 * A low level redis scan operation
 * 
 * @author kanej
 *
 */
public interface RedisScanOperation
{
	/**
	 * Perform the operation on the specified key.
	 * 
	 * No guarantee is made that key still exists in the cache (it is possible for a
	 * entry in a cache to expire or be deleted at any time). Never assume that the
	 * entry still exists just because performOperation is called on the entry.
	 * 
	 * @param key
	 *            The key
	 */
	public void performOperation(Redis redis, CacheKey key);
}
