package org.jimmutable.core.serialization.collections;

import org.jimmutable.core.threading.LRUCache;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LRUCacheTest extends TestCase
{
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LRUCacheTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LRUCacheTest.class );
    }
    
    public void testCache()
    {
    	LRUCache<Integer,String> cache = new LRUCache(3);
    	
    	cache.put(1, "foo");
    	cache.put(2, "bar");
    	cache.put(3, "baz");
    	
    	assert(cache.containsKey(1));
    	assert(cache.containsKey(2));
    	assert(cache.containsKey(3));
    	
    	assertEquals(cache.get(1,null),"foo");
    	assertEquals(cache.get(2,null),"bar");
    	assertEquals(cache.get(3,null),"baz");
    	
    	cache.put(4, "qux");
    	
    	assert(cache.containsKey(2));
    	assert(cache.containsKey(3));
    	assert(cache.containsKey(4));
    	
    	cache.get(2,null); // access 2 with an eye to preserve it from the next addition...
    	
    	cache.put(5, "quux");
    	
    	assert(cache.containsKey(2));
    	assert(cache.containsKey(4));
    	assert(cache.containsKey(5));
    }
}
