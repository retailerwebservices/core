package org.jimmutable.cloud.cache;

import java.util.Arrays;
import java.util.Random;

public class TestRedisCache
{
	static public void main(String args[])
	{
		Cache cache = new CacheInMemory();		
	
		cache.put("aleph/foo", "1");
		cache.put("aleph/bar", "2");
		cache.put("aleph/baz", "3");
		
		cache.put("bet/foo", "a");
		cache.put("bet/bar", "b");
		cache.put("bet/baz", "c");
		
		System.out.println(cache.getString("aleph/bar", null));
		System.out.println(cache.getString("bet/bar", null));
		
		cache.deleteAllSlow("bet");
		
		System.out.println(cache.getString("aleph/bar", null));
		System.out.println(cache.getString("bet/bar", null));
		
		writeBinaryData(cache,1024);
		//testTTL(cache);
	}
	
	static private void writeBinaryData(Cache cache, int n)
	{
		for ( int i = 0; i < n; i++ )
		{
			byte data[] = createRandomBytes(1024*1024);
			CacheKey key = new CacheKey("gimel/"+i);
			
			cache.put(key, data);
			
			byte from_cache[] = cache.getBytes(key, null);
			
			boolean still_have_first_key = cache.exists(new CacheKey("gimel/0"));
			boolean is_from_cache_ok = from_cache != null && Arrays.equals(from_cache, data);
			
			System.out.println("i: "+i+", still_have_first_key: "+still_have_first_key+", is_from_cache_ok: "+is_from_cache_ok);
		} 
		
		System.out.println("Before delete");
		cache.scan(new CacheKey("gimel"), new PrintOperation());
		
		cache.deleteAllSlow(new CacheKey("gimel"));
		
		System.out.println("After delete");
		cache.scan(new CacheKey("gimel"), new PrintOperation());
	}
	
	static private void testTTL(Cache cache)
	{
		cache.put(new CacheKey("dalet/foo"),"bar",2_000);
		
		System.out.println("expiration time: "+cache.getTTL(new CacheKey("dalet/foo"), -1));
		
		int i = 0;
		
		for ( i = 0; i < 5; i++ )
		{
			try { Thread.currentThread().sleep(1000); } catch(Exception e) {}
			
			System.out.println("i: "+i+",exists: "+cache.exists(new CacheKey("dalet/foo")));
		}
		
	}
	
	static private byte[] createRandomBytes(int size)
	{
		byte[] ret = new byte[size];
		
		Random r = new Random(); 
		
		r.nextBytes(ret);
			
		return ret;
	} 
	
	static private class PrintOperation implements ScanOperation
	{

		@Override
		public void performOperation( Cache cache, CacheKey key )
		{
			System.out.println(key);
		}
		
	}
}
