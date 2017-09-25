package org.jimmutable.core.threading;

import org.junit.Test;

import junit.framework.TestCase;

public class ExpirationCacheTest extends TestCase
{
	ExpirationCache<String, String> cache;

	@Test
	public void testPutHasandGet()
	{
		cache = new ExpirationCache<>(100, 1);
		cache.put("Statler", "Waldorf");
		assertTrue(cache.has("Statler"));
		assertEquals("Waldorf", cache.getOptional("Statler", "Definitely Not Waldorf"));
	}

	@Test
	public void testTimeOut() throws InterruptedException
	{
		cache = new ExpirationCache<>(100, 2);
		cache.put("Statler", "Waldorf");
		Thread.sleep(10);
		assertTrue(cache.has("Statler"));
		Thread.sleep(1000);
		assertFalse(cache.has("Statler"));
	}

	@Test
	public void testRemove() throws InterruptedException
	{
		cache = new ExpirationCache<>(100, 2);
		cache.put("Statler", "Waldorf");
		assertTrue(cache.has("Statler"));
		cache.remove("Statler");
		assertFalse(cache.has("Statler"));
	}
}
