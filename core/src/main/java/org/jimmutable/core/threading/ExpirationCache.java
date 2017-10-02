package org.jimmutable.core.threading;

import org.jimmutable.core.utils.Validator;

/**
 * This class allows us to do caching for a short duration. <br>
 * <b>**WARNING**</b> <br>
 * if you do not make your ExpirationCache large enough, it may remove an item
 * that has not timed out if you put too many items in it.
 * 
 * @author andrew.towe
 *
 * @param <K>
 *            The key type
 * @param <V>
 *            The value type
 */
public class ExpirationCache<K, V>
{

	private long maximum_allowed_entry_age_in_ms;
	private LRUCache<K, V> data;
	private LRUCache<K, Long> put_times;

	/**
	 * 
	 * @param maximum_allowed_entry_age_in_ms
	 *            how long should something exist in the ExpirationCache
	 * @param maximum_size
	 *            The most amount of items allowed in the cache.
	 */
	public ExpirationCache(long maximum_allowed_entry_age_in_ms, int maximum_size)
	{
		Validator.min(maximum_allowed_entry_age_in_ms, 0);// strictly positive numbers
		this.maximum_allowed_entry_age_in_ms = maximum_allowed_entry_age_in_ms;
		data = new LRUCache<>(maximum_size);
		put_times = new LRUCache<>(maximum_size);
	}

	/**
	 * Puts in a key-value pair
	 * 
	 * @param key
	 *            of object we are looking to find
	 * @param value
	 *            to be associated with the key.
	 */
	public void put(K key, V value)
	{
		data.put(key, value);
		put_times.put(key, System.currentTimeMillis());
	}

	/**
	 * 
	 * @param key
	 *            of the object you are looking for.
	 * @param default_value
	 *            value to return if no value if found
	 * @return the value associated with the key if the value has not timed out,
	 *         otherwise default_value
	 */
	public V getOptional(K key, V default_value)
	{
		Long time_in_cache = put_times.get(key, null);
		if (time_in_cache != null)
		{// if it is not in the put times, return default value
			if ((System.currentTimeMillis() - time_in_cache) <= maximum_allowed_entry_age_in_ms)
			{// if it has been in the system more that the maximum time, return the default
				// value
				return data.get(key, default_value);
			}
		}
		return default_value;// if any of the conditions are not met, return default_value
	}

	/**
	 * 
	 * @param key
	 *            of object that you are searching for
	 * @return true if the object is in the expiration cache and has not timed out.
	 *         Otherwise false.
	 */

	public boolean has(K key)
	{
		return getOptional(key, null) != null;
	}

	/**
	 * @param key
	 *            to remove from the Expiration Cache.
	 */
	public void remove(K key)
	{
		data.remove(key);
		put_times.remove(key);
	}
}
