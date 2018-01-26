package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

abstract public class Cache implements ICache
{
	/**
	 * Put data (byte array) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
	public void put(CacheKey key, byte data[]) { put(key,data,-1); }
	
	/**
	 * Put data (String) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
	public void put(CacheKey key, String data) { put(key,data,-1); }
	
	/**
	 * Put data (StandardObject) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
    public void put(CacheKey key, StandardObject data) { put(key,data,-1); }
    
    /**
	 * Delete all entries with a given prefix.  Warning: May be slow!
	 * 
	 * @param prefix The prefix of the entries to delete.  May not be null
	 */
	public void deleteAllSlow(CacheKey prefix) 
	{ 
		Validator.notNull(prefix);
		scan(prefix, ScanOperationDeleteAll.OPERATION); 
	}
	
	/**
	 * Perform a scan operation asynchronously (in one new thread created for this purpose)
	 * 
	 * @param prefix The prefix of the key(s) to perform the operation on
	 * @param operation The operation to perform
	 */
	public void scanAsync(CacheKey prefix, ScanOperation operation)
	{
		Thread t = new Thread(new ScanOperationRunnable(this,prefix, operation));
		t.start();
	}
	
	/**
	 * Asynchronously (in one new thread created for this purpose) delete all keys
	 * with a specified prefix from the cache
	 * 
	 * @param prefix
	 *            The prefix of the key to delete. May not be null
	 */
	public void deleteAllAsync(CacheKey prefix)
	{
		scanAsync(prefix, ScanOperationDeleteAll.OPERATION);
	}
	
	public void put(String key, byte data[]) { put(new CacheKey(key), data); }
	public void put(String key, String data) { put(new CacheKey(key), data); }
    public void put(String key, StandardObject data) { put(new CacheKey(key), data); }
	public void put(String key, byte data[], long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	public void put(String key, String data, long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	public void put(String key, StandardObject data, long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	public boolean exists(String key) { return exists(new CacheKey(key)); }
	public long getTTL(String key, long default_value) { return getRemainingTTL(new CacheKey(key), default_value); }
	public byte[] getBytes(String key, byte default_value[]) { return getBytes(new CacheKey(key), default_value); }
	public String getString(String key, String default_value) { return getString(new CacheKey(key), default_value); }
	public StandardObject getObject(String key, StandardObject default_value) { return getObject(new CacheKey(key), default_value); }
	public void delete(String key) { delete(new CacheKey(key)); }
	public void deleteAllSlow(String prefix) { deleteAllSlow(new CacheKey(prefix)); }
	public void scan(String prefix, ScanOperation operation) { scan(new CacheKey(prefix),operation); }
	public void scanAsync(String prefix, ScanOperation operation) { scanAsync(new CacheKey(prefix), operation); }
	public void deleteAsync(String prefix) { deleteAllAsync(new CacheKey(prefix)); }
}
