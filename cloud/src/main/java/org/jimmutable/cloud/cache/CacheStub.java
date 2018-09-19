package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.StandardObject;

public class CacheStub implements ICache
{ 
	public static final String ERROR_MESSAGE = "This should have never been called for unit testing, use a different implementation for integration testing!";

	
	public CacheStub()
	{

	}
	
	public void put( CacheKey key, byte[] data, long max_ttl )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void put( CacheKey key, String data, long max_ttl )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

    @Override
	@SuppressWarnings("rawtypes")
	public void put( CacheKey key, StandardObject data, long max_ttl )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public long getRemainingTTL( CacheKey key, long default_value )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public byte[] getBytes( CacheKey key, byte[] default_value )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public String getString( CacheKey key, String default_value )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

    @Override
	@SuppressWarnings("rawtypes")
	public StandardObject getObject( CacheKey key, StandardObject default_value )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void delete( CacheKey key )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void scan( CacheKey prefix, ScanOperation operation )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public boolean exists( CacheKey key )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void put( CacheKey key, byte[] data )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}

	@Override
	public void put( CacheKey key, String data )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}

	@Override
	public void put( CacheKey key, StandardObject data )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}

	@Override
	public void deleteAllSlow( CacheKey prefix )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}

	@Override
	public void scanAsync( CacheKey prefix, ScanOperation operation )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}

	@Override
	public void deleteAllAsync( CacheKey prefix )
	{
		throw new RuntimeException(ERROR_MESSAGE);		
	}
	
}
