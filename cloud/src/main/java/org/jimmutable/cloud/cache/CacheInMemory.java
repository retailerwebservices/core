package org.jimmutable.cloud.cache;

import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.threading.LRUCache;
import org.jimmutable.core.utils.Validator;

public class CacheInMemory implements Cache
{
	private UnderlyingCache backing_cache;
	
	public CacheInMemory()
	{
		backing_cache = new UnderlyingCache();
	}
	
	public void put( CacheKey key, byte[] data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) { delete(key); return; }
		
		backing_cache.put(key, data, max_ttl);
	}



	@Override
	public void put( CacheKey key, String data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) { delete(key); return; }
		
		put(key, data.getBytes(StandardCharsets.UTF_8), max_ttl);
	}



	@Override
	public void put( CacheKey key, StandardObject data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) { delete(key); return; }
		
		put(key, data.serialize(Format.JSON), max_ttl);
	}
	


	public boolean exists( CacheKey key )
	{
		return backing_cache.getBytes(key, null) != null;
	}

	public long getTTL( CacheKey key, long default_value )
	{
		return backing_cache.getTTL(key, default_value);
	}

	public byte[] getBytes( CacheKey key, byte[] default_value )
	{
		return backing_cache.getBytes(key, default_value);
	}


	@Override
	public String getString( CacheKey key, String default_value )
	{
		byte data[] = getBytes(key, null);
		if ( data == null ) return default_value;
		return new String(data,StandardCharsets.UTF_8);
	}



	@Override
	public StandardObject getObject( CacheKey key, StandardObject default_value )
	{
		String str = getString(key, null);
		if ( str == null ) return default_value;
		
		try { return StandardObject.deserialize(str); } catch(Exception e) { return default_value; }
	}



	@Override
	public void delete( CacheKey key )
	{
		if ( key == null ) return;
		backing_cache.remove(key);
	}



	@Override
	public void scan( CacheKey prefix, ScanOperation operation )
	{
		Validator.notNull(prefix, operation);
		
		String prefix_str = prefix.toString();
		
		List<CacheKey> tmp_keys = new ArrayList();
		backing_cache.copyKeysIntoCollection(tmp_keys);
		
		for ( CacheKey cur : tmp_keys )
		{
			String cur_str = cur.toString();
			
			if ( cur_str.startsWith(prefix_str) && exists(cur) ) 
				operation.performOperation(this, cur);
		}
	}
	
	
	static private class UnderlyingCache
	{
		private LRUCache<CacheKey,CacheValue> cache = new LRUCache(10_000);
		
		private UnderlyingCache()
		{
			
		}
		
		public byte[] getBytes(CacheKey key, byte default_value[])
		{
			if ( key == null ) return default_value;
			
			CacheValue value = cache.get(key, null);
			if ( value == null ) return default_value;
			
			byte ret[] = value.getData(null);
			
			if ( ret == null )
			{
				cache.remove(key);
				return default_value;
			}
			
			return ret;
		}
		
		public long getTTL(CacheKey key, long default_value)
		{
			if ( key == null ) return default_value;
			
			CacheValue value = cache.get(key, null);
			if ( value == null ) return default_value;
			
			if ( value.isCurrentlyValidValue() )
			{
				if ( !value.hasExpirationTime() ) return default_value;
				return value.getExpirationTime() - System.currentTimeMillis();
			}
			else
			{
				cache.remove(key);
				return default_value;
			}
		}
		
		public void put(CacheKey key, byte data[], long max_ttl)
		{
			cache.put(key, new CacheValue(data,max_ttl));
		}
		
		public void remove(CacheKey key)
		{
			cache.remove(key);
		}
		
		public void copyKeysIntoCollection(Collection<CacheKey> c)
		{
			cache.copyKeysIntoCollection(c);
		}
	}

	/**
	 * Class that wraps one cache value entry
	 * 
	 * @author kanej
	 *
	 */
	static private class CacheValue
	{
		private long expiration_time;
		private SoftReference<byte[]> data;
		
		public CacheValue(byte data[], long max_ttl)
		{
			this.data = new SoftReference(data); 
			
			if ( max_ttl > 0 )
			{
				this.expiration_time = System.currentTimeMillis() + max_ttl;
			}
			else
			{
				this.expiration_time = -1;
			}
		}
		
		public boolean isCurrentlyValidValue()
		{
			if ( hasExpired() ) return false;
			if ( data.get() == null ) return false;
			
			return true;
		}
		
		public boolean hasExpired()
		{
			if ( !hasExpirationTime() ) return false;
			return System.currentTimeMillis() > expiration_time;
		}
		
		public boolean hasExpirationTime() { return expiration_time > 0; }
		
		public byte[] getData(byte default_value[])
		{
			if ( hasExpired() ) return default_value;
			
			byte ret[] = data.get();
			if ( ret == null ) return default_value;
			
			return ret;
		}
		
		public long getExpirationTime() { return expiration_time; }
	}
}
