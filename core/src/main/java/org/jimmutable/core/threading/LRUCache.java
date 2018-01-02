package org.jimmutable.core.threading;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jimmutable.core.utils.Validator;


/**
 * A thread safe LRU cache
 * 
 * @author jim.kane
 *
 * @param <K> The key type
 * @param <V> The value type
 * 
 */


public class LRUCache<K,V> 
{
	private Map<K,V> inner_map;
	private int cache_size;
	
	/**
	 * Create an empty LRUCache
	 * 
	 * @param cache_size
	 *            The maximum size of the cache. The cache will contain, at
	 *            most, this many items. Attempts to add more items will result
	 *            in the least recently used (LRU) item being removed to keep
	 *            the size below the specified value. Valid value are zero and
	 *            greater.
	 */
	public LRUCache(int cache_size)
	{
		Validator.min(cache_size, 0);
		
		this.cache_size = cache_size;
		inner_map = Collections.synchronizedMap(new InnerMap<K,V>());
	}
	
	/**
	 * Get the maximum size of the cache
	 * 
	 * @return The maximum size of the LRU cache
	 */
	public int getSimpleCacheSize() { return cache_size; }
	
	/**
	 * Get the value associated with a given key
	 * 
	 * @param key
	 *            The key of the value to get
	 * @param default_value
	 *            The value to return if key is not in the cache
	 * @return The value associated with key, or default_value if no value is
	 *         associated with the specified key
	 */
	public V get(K key, V default_value)
	{
		V ret = inner_map.get(key);
		if ( ret == null ) return default_value;
		
		return ret;
	}
	
	/**
	 * Test to see if a give key is in the LRU cache
	 * 
	 * @param key
	 *            The key to test. Null keys always return false
	 * @return true if the key is in the cache, false otherwise
	 */
	public boolean containsKey(K key)
	{
		if ( key == null ) return false;
		return inner_map.containsKey(key);
	}
	
	/**
	 * Put a given key/value pair into the cache
	 * 
	 * @param key
	 *            The key to put
	 * @param value
	 *            The value to put. putting a null value is the same as a remove
	 */
	public void put(K key, V value)
	{
		if ( key == null ) return;
		
		if ( value == null ) 
		{
			inner_map.remove(key);
			return;
		}
		
		
		inner_map.put(key, value);
	}
	
	
	/**
	 * Remove a given key (along with its associated value) from the map
	 * 
	 * @param key
	 *            The key of the key/value pair to remove. If key is null then
	 *            nothing is removed
	 */
	public void remove(K key)
	{
		if ( key == null ) return;
		inner_map.remove(key);
	}
	
	/**
	 * Remove all entries from the cache
	 */
	public void clear()
	{
		inner_map.clear();
	}
	
	private class InnerMap<K,V> extends LinkedHashMap<K,V>
	{
		private InnerMap() 
		{
			super(16, 0.75f, true);
			
		}

		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) 
		{
			return size() > cache_size;
		}
	}
	
	public void copyKeysIntoCollection(Collection<K> dest)
	{
		dest.addAll(inner_map.keySet());
	}
}