package org.jimmutable.cloud.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis
{
	static public void main(String args[])
	{
		/*String the_acid_string = createAcidString();
		
		J
		jedis.set("foo", the_acid_string);
		String value = jedis.get("foo");
		
		System.out.println(value.equals(the_acid_string));*/
		
		
		/*Jedis jedis = new Jedis("localhost");
		long t1 = System.currentTimeMillis();
		for ( int i = 0; i < 10_000; i++ )
		{
			jedis.set("foo", ""+i);
			jedis.get("foo");
			
			
		}
		System.out.println("Time: "+(System.currentTimeMillis()-t1));*/
		
		/*JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
		config.setMaxIdle(1000 * 60);
		config.setTestOnBorrow(false);
		
		long t1 = System.currentTimeMillis();
		
		JedisPool pool = new JedisPool(config, "localhost", 6379);
		
		
		for ( int i = 0; i < 10_000; i++ )
		{
			Jedis jedis = pool.getResource();
			
			jedis.set("foo", ""+i);
			jedis.get("foo");
			
			jedis.close();
		}
		
		System.out.println("Time: "+(System.currentTimeMillis()-t1));*/
		
		
		Jedis jedis = new Jedis("localhost");
		
		jedis.set("fooz".getBytes(), "bar".getBytes());
		jedis.expire("fooz", 100);
	}
	
	static public String createAcidString()
	{
		StringBuffer ret = new StringBuffer();
		
		for ( int i = 0; i < 10_000; i++ )
		{
			ret.append((char)i);
		}
		
		return ret.toString();
	}
	
}
