package org.jimmutable.cloud.cache;

import java.nio.charset.StandardCharsets;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class CacheRedis implements Cache
{ 
	
	private JedisPool pool;
	
	public CacheRedis()
	{
		this("localhost", 6379);
	}
	
	public CacheRedis(String host, int port)
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
		config.setMaxIdle(1000 * 60);
		config.setTestOnBorrow(false);
		
		
		pool = new JedisPool(config, "localhost", 6379);
	}
	
	private void expire(Jedis jedis, CacheKey key, long max_ttl)
	{
		if ( jedis == null || key == null ) return;
		
		if ( max_ttl <= 0 ) return;
		jedis.expire(key.toString(), (int)(max_ttl/1000l));
	}
	
	public void put( CacheKey key, byte[] data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		try(Jedis jedis = pool.getResource();)
		{
			jedis.set(key.toString().getBytes(StandardCharsets.UTF_8), data);
			expire(jedis,key,max_ttl);
		}
		
	}

	@Override
	public void put( CacheKey key, String data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		try(Jedis jedis = pool.getResource();)
		{
			jedis.set(key.toString(), data);
			expire(jedis,key,max_ttl);
		}
	}

	@Override
	public void put( CacheKey key, StandardObject data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) delete(key);
		
		put(key, data.serialize(Format.JSON), max_ttl);
	}

	@Override
	public boolean exists( CacheKey key )
	{
		if ( key == null ) return false;
		
		try(Jedis jedis = pool.getResource();)
		{
			return jedis.exists(key.toString());
		}
	}

	@Override
	public long getTTL( CacheKey key, long default_value )
	{
		if ( key == null ) return default_value;
		
		try(Jedis jedis = pool.getResource();)
		{
			long ret = jedis.ttl(key.toString());
			if ( ret <= 0 ) return default_value;
			
			return ret*1000;
		}
	}

	@Override
	public byte[] getBytes( CacheKey key, byte[] default_value )
	{
		if ( key == null ) return default_value;
		
		try(Jedis jedis = pool.getResource();)
		{
			byte ret[] = jedis.get(key.toString().getBytes(StandardCharsets.UTF_8));
			
			if ( ret == null ) return default_value;
			return ret;
		}
	}

	@Override
	public String getString( CacheKey key, String default_value )
	{
		if ( key == null ) return default_value;
		
		try(Jedis jedis = pool.getResource();)
		{
			String ret = jedis.get(key.toString());
			if ( ret == null ) return default_value;
			
			return ret;
		}
	}

	@Override
	public StandardObject getObject( CacheKey key, StandardObject default_value )
	{
		String str = getString(key, null);
		if ( str == null ) return default_value;
		
		try
		{
			return StandardObject.deserialize(str);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}

	@Override
	public void delete( CacheKey key )
	{
		if ( key == null ) return;
		
		try(Jedis jedis = pool.getResource();)
		{
			jedis.del(key.toString());
		}
	}

	@Override
	public void scan( CacheKey prefix, ScanOperation operation )
	{
		try(Jedis jedis = pool.getResource();)
		{
			ScanParams params = new ScanParams();
			
			if ( prefix != null )
				params = params.match(prefix+"*");
			
			params.count(100);
			
			String cursor = "0";
			
			while(true)
			{
				ScanResult<String> result = jedis.scan(cursor, params);
				
				for ( String key : result.getResult() )
				{
					try
					{
						operation.performOperation(this, new CacheKey(key));
					}
					catch(Exception e)
					{
						
					}
				}
				
				cursor = result.getStringCursor();
				if ( cursor.equals("0") ) break;
			}
		}
		
	}
	
	
}
