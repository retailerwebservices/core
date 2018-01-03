package org.jimmutable.cloud.cache;

import java.util.Arrays;
import java.util.Random;

/**
 * NOTES: 
 * To install redis on OS X
 * 
 * 
 * xcode-select --install
 * 
 * Download latest version of redis
 * Then run make test
 * Then run make
 * Inside of the src folder, there is a executable, redis-server
 * 
 * @author kanej
 *
 */

public class TestCache
{
	static public void main(String args[])
	{
		Cache cache = new CacheInMemory();		
	
		cache.put("test://aleph/foo", "1");
		cache.put("test://aleph/bar", "2");
		cache.put("test://aleph/baz", "3");
		
		cache.put("test://bet/foo", "a");
		cache.put("test://bet/bar", "b");
		cache.put("test://bet/baz", "c");
		
		System.out.println(cache.getString("test://aleph/bar", null));
		System.out.println(cache.getString("test://bet/bar", null));
		
		cache.deleteAllSlow("test://bet");
		
		System.out.println(cache.getString("test://aleph/bar", null));
		System.out.println(cache.getString("test://bet/bar", null));
		
		writeBinaryData(cache,1024);
		testTTL(cache);
	}
	
	/**
	 * Writes random 1mb blocks of data in an effort to over-fill the cache
	 * 
	 * @param cache The cache to use
	 * @param n The number of blocks to write
	 */
	static private void writeBinaryData(Cache cache, int n)
	{
		for ( int i = 0; i < n; i++ )
		{
			byte data[] = createRandomBytes(1024*1024);
			CacheKey key = new CacheKey("test://gimel/"+i);
			
			cache.put(key, data);
			
			byte from_cache[] = cache.getBytes(key, null);
			
			boolean still_have_first_key = cache.exists(new CacheKey("test://gimel/0"));
			boolean is_from_cache_ok = from_cache != null && Arrays.equals(from_cache, data);
			
			System.out.println("i: "+i+", still_have_first_key: "+still_have_first_key+", is_from_cache_ok: "+is_from_cache_ok);
		} 
		
		System.out.println("Before delete");
		cache.scan(new CacheKey("test://gimel"), new PrintOperation());
		System.out.println();
		
		cache.deleteAllSlow(new CacheKey("test://gimel"));
		
		System.out.println("After delete");
		cache.scan(new CacheKey("test://gimel"), new PrintOperation());
		System.out.println();
	}
	
	/**
	 * An experiment with items that have an expiration
	 * 
	 * @param cache The cache to use for testing
	 */
	static private void testTTL(Cache cache)
	{
		cache.put(new CacheKey("test://dalet/foo"),"bar",3_000);
		
		System.out.println("expiration time: "+cache.getTTL(new CacheKey("test://dalet/foo"), -1));
		
		int i = 0;
		
		for ( i = 0; i < 5; i++ )
		{
			try { Thread.currentThread().sleep(1000); } catch(Exception e) {}
			
			System.out.println("i: "+i+",exists: "+cache.exists(new CacheKey("test://dalet/foo")));
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
