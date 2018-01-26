package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

/**
 * A volatile cache suitable for sharing *between* instances of an application.
 * (In production: this is typically a redis cache)
 * 
 * Cache keys are organized hierarchically (in a fashion similar to files on
 * disk, objects in a S3 store etc.). There is no practical limit to the depth
 * that keys may be organized. Keys can be easily scanned or deleted by prefix.
 * 
 * All operations are thread safe. Any operations are presumed to be synchronous
 * (blocking) unless they are named "async"
 * 
 * 
 * A NOTE ON VOLATILITY: Caches are LRU and will (memory permitting) drop items.
 * As a result, it is *never* safe to assume that anything added to a cache will
 * be available at a later time (caches are super volatile). Even if you *just*
 * said "hey, does key X exist" the next line *can not* assume that it still
 * does.
 * 
 * @author kanej
 *
 */
@SuppressWarnings("rawtypes")
public interface ICache
{
	/**
	 * Put data (byte array) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
	public void put(CacheKey key, byte data[]);
	
	/**
	 * Put data (String) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
	public void put(CacheKey key, String data);
	
	/**
	 * Put data (StandardObject) into a cache (no expiration time)
	 * 
	 * @param key The key for the data
	 * @param data The data
	 */
    public void put(CacheKey key, StandardObject data);
	
	/**
	 * Put data (byte array) into the cache while also specifying a maximum time to
	 * live (data will be deleted after that ttl). Negative and zero TTL has the
	 * meaning of "infinite"
	 * 
	 * @param key
	 *            The key for the data
	 * @param data
	 *            The data
	 * @param max_ttl
	 *            The maximum time to live for the data, in milliseconds. Negative
	 *            and zero values have the meaning of "never expire"
	 */
	public void put(CacheKey key, byte data[], long max_ttl);
	
	/**
	 * Put data (String) into the cache while also specifying a maximum time to
	 * live (data will be deleted after that ttl). Negative and zero TTL has the
	 * meaning of "infinite"
	 * 
	 * @param key
	 *            The key for the data
	 * @param data
	 *            The data
	 * @param max_ttl
	 *            The maximum time to live for the data, in milliseconds. Negative
	 *            and zero values have the meaning of "never expire"
	 */
	public void put(CacheKey key, String data, long max_ttl);
	
	/**
	 * Put data (StandardObject) into the cache while also specifying a maximum time to
	 * live (data will be deleted after that ttl). Negative and zero TTL has the
	 * meaning of "infinite"
	 * 
	 * @param key
	 *            The key for the data
	 * @param data
	 *            The data
	 * @param max_ttl
	 *            The maximum time to live for the data, in milliseconds. Negative
	 *            and zero values have the meaning of "never expire"
	 */
    public void put(CacheKey key, StandardObject data, long max_ttl);
	
	/**
	 * Check to see if a given key exists in the cache. Remember, caches are
	 * volatile, so just because the key exists *now* does not mean that it will
	 * still exist even one nanosecond later.
	 * 
	 * @param key
	 *            The key to test
	 * @return true if the key exists, false otherwise
	 */
	public boolean exists(CacheKey key);
	
	/**
	 * Get the *remaining* TTL for an entry. If you add an entry with 10 second TTL
	 * and then ask, 1 second later, what the entry's TTL is you will get 9 seconds.
	 * 
	 * @param key
	 *            The key for the item
	 * @param default_value
	 *            The value to return if the cache does not contain an entry or some
	 *            other error occurs
	 * @return The TTL for the entry with the key (key), or default value if the
	 *         entry does not exist or any other error occurs
	 */
	public long getRemainingTTL(CacheKey key, long default_value);
	
	/**
	 * Get data from the cache
	 * 
	 * @param key The key of the data to get
	 * @param default_value The value to return if no such key exists
	 * @return The data associated with a key, or default_value if the key does not exist in the cache
	 * 
	 */
	public byte[] getBytes(CacheKey key, byte default_value[]);
	
	/**
	 * Get data from the cache
	 * 
	 * @param key The key of the data to get
	 * @param default_value The value to return if no such key exists
	 * @return The data associated with a key, or default_value if the key does not exist in the cache
	 * 
	 */
	public String getString(CacheKey key, String default_value);
	
	/**
	 * Get data from the cache
	 * 
	 * @param key The key of the data to get
	 * @param default_value The value to return if no such key exists
	 * @return The data associated with a key, or default_value if the key does not exist in the cache
	 * 
	 */
    public StandardObject getObject(CacheKey key, StandardObject default_value);
	
	/**
	 * Delete an entry from the cache
	 * 
	 * @param key The key of the entry to delete
	 */
	public void delete(CacheKey key);
	
	/**
	 * Delete all entries with a given prefix.  Warning: May be slow!
	 * 
	 * @param prefix The prefix of the entries to delete.  May not be null
	 */
	public void deleteAllSlow(CacheKey prefix); 
	
	/**
	 * Perform an operation on all keys in the cache with a given prefix
	 * 
	 * @param prefix
	 *            The prefix of the key(s) to perform the operation on. May not be
	 *            null.
	 * @param operation
	 *            The operation to perform
	 */
	public void scan(CacheKey prefix, ScanOperation operation);
	
	/**
	 * Perform a scan operation asynchronously (in one new thread created for this purpose)
	 * 
	 * @param prefix The prefix of the key(s) to perform the operation on
	 * @param operation The operation to perform
	 */
	public void scanAsync(CacheKey prefix, ScanOperation operation);
	
	/**
	 * Asynchronously (in one new thread created for this purpose) delete all keys
	 * with a specified prefix from the cache
	 * 
	 * @param prefix
	 *            The prefix of the key to delete. May not be null
	 */
	public void deleteAllAsync(CacheKey prefix);
	
	
	
	default public void put(String key, byte data[]) { put(new CacheKey(key), data); }
	default public void put(String key, String data) { put(new CacheKey(key), data); }
    default public void put(String key, StandardObject data) { put(new CacheKey(key), data); }
	default public void put(String key, byte data[], long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	default public void put(String key, String data, long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	default public void put(String key, StandardObject data, long max_ttl) { put(new CacheKey(key), data, max_ttl); }
	default public boolean exists(String key) { return exists(new CacheKey(key)); }
	default public long getTTL(String key, long default_value) { return getRemainingTTL(new CacheKey(key), default_value); }
	default public byte[] getBytes(String key, byte default_value[]) { return getBytes(new CacheKey(key), default_value); }
	default public String getString(String key, String default_value) { return getString(new CacheKey(key), default_value); }
	default public StandardObject getObject(String key, StandardObject default_value) { return getObject(new CacheKey(key), default_value); }
	default public void delete(String key) { delete(new CacheKey(key)); }
	default public void deleteAllSlow(String prefix) { deleteAllSlow(new CacheKey(prefix)); }
	default public void scan(String prefix, ScanOperation operation) { scan(new CacheKey(prefix),operation); }
	default public void scanAsync(String prefix, ScanOperation operation) { scanAsync(new CacheKey(prefix), operation); }
	default public void deleteAsync(String prefix) { deleteAllAsync(new CacheKey(prefix)); }
}
