package org.jimmutable.cloud.cache;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.cloud.cache.redis.RedisScanOperation;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.utils.Validator;

public class CacheRedis implements Cache
{ 
	private LowLevelRedisDriver redis;
	private ApplicationId app;
	
	public CacheRedis(ApplicationId app, LowLevelRedisDriver redis)
	{
		Validator.notNull(redis);
		this.redis = redis;
		this.app = app;
	}
	
	public void put( CacheKey key, byte[] data, long max_ttl )
	{
		redis.cache().set(app, key, data, max_ttl);
	}

	@Override
	public void put( CacheKey key, String data, long max_ttl )
	{
		redis.cache().set(app, key, data, max_ttl);
	}

	@Override
	public void put( CacheKey key, StandardObject data, long max_ttl )
	{
		if ( key == null ) return;
		if ( data == null ) { delete(key); return; }
		
		redis.cache().set(app, key, data.serialize(Format.JSON), max_ttl);
	}

	@Override
	public long getTTL( CacheKey key, long default_value )
	{
		return redis.cache().getTTL(app, key, default_value);
	}

	@Override
	public byte[] getBytes( CacheKey key, byte[] default_value )
	{
		return redis.cache().getBytes(app, key, default_value);
	}

	@Override
	public String getString( CacheKey key, String default_value )
	{
		return redis.cache().getString(app, key, default_value);
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
		redis.cache().delete(app, key);
	}

	@Override
	public void scan( CacheKey prefix, ScanOperation operation )
	{
		Validator.notNull(prefix, operation);
		
		RedisScanOperation low_level_op = new RedisScanOperation()
		{
			public void performOperation(LowLevelRedisDriver redis, CacheKey key)
			{
				operation.performOperation(CacheRedis.this, key);
			}
		};
		
		redis.cache().scan(app, prefix, low_level_op);
	}

	@Override
	public boolean exists( CacheKey key )
	{
		return redis.cache().exists(app, key);
	}
	
}
