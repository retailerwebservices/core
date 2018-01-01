package org.jimmutable.cloud.cache;

import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.threading.LRUCache;

public class CacheInMemory implements Cache
{
	private LRUCache<CacheKey,CacheValue> cache = new LRUCache(10_000);
	
	public CacheInMemory()
	{
		
	}
	
	public void put( CacheKey key, byte[] data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		cache.put(key, new CacheValue(data,max_ttl));
	}



	@Override
	public void put( CacheKey key, String data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		put(key, data.getBytes(StandardCharsets.UTF_8), max_ttl);
	}



	@Override
	public void put( CacheKey key, StandardObject data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		put(key, data.serialize(Format.JSON), max_ttl);
	}


	public boolean exists( CacheKey key )
	{
		if ( key == null ) return false;
		
		CacheValue val = cache.get(key, null);
		
		if ( val == null ) return false;
		return val.isValid();
	}

	public long getTTL( CacheKey key, long default_value )
	{
		if ( key == null ) return default_value;
		
		CacheValue val = cache.get(key, null);
		
		if ( val == null ) return default_value;
		if ( !val.isValid() ) return default_value;
		if ( !val.hasExpirationTime() ) return default_value;
		
		return val.getExpirationTime() - System.currentTimeMillis();
	}

	public byte[] getBytes( CacheKey key, byte[] default_value )
	{
		if ( key == null ) return default_value;
		
		CacheValue val = cache.get(key, null);
		
		if ( val == null ) return default_value;
		if ( !val.isValid() ) return default_value;
		
		return val.getData(default_value);
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
		cache.remove(key);
	}



	@Override
	public void scan( CacheKey prefix, ScanOperation operation )
	{
		String prefix_str = prefix.toString();
		
		List<CacheKey> tmp_keys = new ArrayList();
		cache.copyKeysIntoCollection(tmp_keys);
		
		for ( CacheKey cur : tmp_keys )
		{
			String cur_str = cur.toString();
			
			if ( cur_str.startsWith(prefix_str) && exists(cur) ) 
				operation.performOperation(this, cur);
		}
	}

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
		
		boolean isValid()
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
			byte ret[] = data.get();
			if ( ret != null ) return ret;
			return default_value;
		}
		
		public long getExpirationTime() { return expiration_time; }
	}
}
