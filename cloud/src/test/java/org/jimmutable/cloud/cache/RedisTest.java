package org.jimmutable.cloud.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.cache.redis.Redis;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.new_messaging.queue.QueueId;
import org.jimmutable.cloud.new_messaging.queue.QueueListener;
import org.jimmutable.cloud.new_messaging.signal.SignalListener;
import org.jimmutable.cloud.new_messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.junit.Test;

public class RedisTest extends StubTest
{
	private Redis redis;
	private ApplicationId app;
	private boolean is_redis_live = false;

	public RedisTest()
	{	
		app = new ApplicationId("stub");
		redis = new Redis();
		
		is_redis_live = isRedisLive();
	}
	
	@Test
	public void testQueue()
	{ 
		if ( !is_redis_live ) { System.out.println("Redis server not available, skipping queue unit test!"); return; }
		
		// Test overflow protection
		{
			QueueId queue_id = new QueueId("overflow-test");
			
			redis.queue().clear(app, queue_id);
			assert(redis.queue().getQueueLength(app, queue_id, 0) == 0);
			
			for ( int i = 0; i < 20_000; i++ )
				redis.queue().submit(app, queue_id, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(i)));
			
			assert(redis.queue().getQueueLength(app, queue_id, 0) < 20_000);
			assert(redis.queue().getQueueLength(app, queue_id, 0) > 9_000);
		}
		
		// Test fan out
		{
			QueueId queue_id = new QueueId("fan-out-test");
			
			redis.queue().clear(app, queue_id);
			assert(redis.queue().getQueueLength(app, queue_id, 0) == 0);
			
			for ( int i = 0; i < 1_000; i++ )
				redis.queue().submit(app, queue_id, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(i)));
			
			assert(redis.queue().getQueueLength(app, queue_id, 0) == 1_000);
			
			TestListener one = new TestListener(10);
			TestListener two = new TestListener(10);
			TestListener three = new TestListener(10);
			TestListener four = new TestListener(10);
			
			redis.queue().startListening(app, queue_id, one, 1);
			redis.queue().startListening(app, queue_id, two, 1);
			redis.queue().startListening(app, queue_id, three, 1);
			redis.queue().startListening(app, queue_id, four, 2);
			
			System.out.println("Testing fan out");
			for ( int i = 0; i < 16; i++ )
			{
				try { Thread.currentThread().sleep(250); } catch(Exception e) {}
				
				System.out.println( 
							String.format("%d, %d, %d, %d", one.ids.size(), two.ids.size(), three.ids.size(), four.ids.size())
						);
			}
			
			System.out.println();
			
			assert(redis.queue().getQueueLength(app, queue_id, 0) == 0);
			assert(one.ids.size()+two.ids.size()+three.ids.size()+four.ids.size() == 1_000);
			
			assert(four.ids.size() > one.ids.size());
			assert(four.ids.size() > two.ids.size());
			assert(four.ids.size() > three.ids.size());
		}
	}
	
	@Test
	public void testSignal()
	{ 
		if ( !is_redis_live ) { System.out.println("Redis server not available, skipping signal unit test!"); return; }
		
		TestListener listener = new TestListener(0);
		TestListener listener2 = new TestListener(0);
		
		SignalTopicId topic = new SignalTopicId("test");
		
		redis.signal().startListening(app, topic, listener);
		redis.signal().startListening(app, topic, listener2);
		
		try { Thread.currentThread().sleep(250); } catch(Exception e) {}
		
		redis.signal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(1)));
		redis.signal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(2)));
		redis.signal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(10)));
		
		try { Thread.currentThread().sleep(500); } catch(Exception e) {}
		
		assert(listener.ids.contains(new ObjectId(1)));
		assert(listener.ids.contains(new ObjectId(2)));
		assert(listener.ids.contains(new ObjectId(10)));
		
		assert(listener2.ids.contains(new ObjectId(1)));
		assert(listener2.ids.contains(new ObjectId(2)));
		assert(listener2.ids.contains(new ObjectId(10)));
	}
	
	static private class TestListener implements SignalListener, QueueListener
	{
		private Set<ObjectId> ids = new HashSet();
		
		private long sleep_time;
		
		public TestListener(long sleep_time)
		{
			this.sleep_time = sleep_time;
		}
		
		@Override
		public void onMessageReceived( StandardObject message )
		{
			if ( !(message instanceof StandardMessageOnUpsert) ) return;
			
			StandardMessageOnUpsert upsert_message = (StandardMessageOnUpsert) message;
			 
			ids.add(upsert_message.getSimpleObjectId());
			
			if ( sleep_time > 0 )
				try { Thread.currentThread().sleep(sleep_time); } catch(Exception e) {}
		}
	}
		
	
	@Test
	public void testCache()
	{ 
		if ( !is_redis_live ) { System.out.println("Redis server not available, skipping cache unit test!"); return; }
		

		// Test exists
		{
			CacheKey key1 = new CacheKey("exist-test://one");
			CacheKey key2 = new CacheKey("exist-test://two");


			redis.cache().set(app, key1, "Hello World", -1); 
			
			assert(redis.cache().exists(app, key1) == true);
			assert(redis.cache().exists(app, key2) == false);
		}
		
		// Test the acid string as a string value
		{
			CacheKey key = new CacheKey("acid-string-test://test-acid-string-value");
			
			String acid_string = createAcidString();
			redis.cache().set(app, key, acid_string, -1); 
			
			String from_cache = redis.cache().getString(app, key, null);
			
			assert(Objects.equals(acid_string, from_cache));
		}
		
		
		// Test the get on an empty string
		{
			CacheKey key = new CacheKey("get-unset-test://a-key-that-is-not-set");
			
			String from_cache = redis.cache().getString(app, key, null);
			
			assert(Objects.equals(from_cache, null)); 
		}
		
		// Test the acid string as a key and a value!
		{
			String acid_string = createAcidString();
			
			CacheKey key = new CacheKey("acid-string-test://"+acid_string);
			
			redis.cache().set(app, key, acid_string, -1); 
			String from_cache = redis.cache().getString(app, key, null);
			
			assert(Objects.equals(acid_string, from_cache));
		}
		
		// Test TTL
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl");
			
			redis.cache().set(app, key, "Hello World", 10_000);
			long value = redis.cache().getTTL(app, key, -1);
			
			assert(value > 8_500);
		}
		
		// Test TTL (unset)
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl-unset");
			
			redis.cache().set(app, key, "Hello World", -1);
			long value = redis.cache().getTTL(app, key, -1);
			
			assert(value == -1);
			
			redis.cache().set(app, key, "Hello World", 0);
			value = redis.cache().getTTL(app, key, -1);
			
			assert(value == -1);
		}
		
		// Test delete
		{
			CacheKey key = new CacheKey("ttl-delete://test-delete");
			
			redis.cache().set(app, key, "Hello World", -1);
			
			String from_cache = redis.cache().getString(app, key, null);
			assert(Objects.equals(from_cache, "Hello World"));
			
			redis.cache().delete(app, key);
			
			from_cache = redis.cache().getString(app, key, null);
			
			assert(Objects.equals(from_cache, null));
			
			// test delete on null data
			redis.cache().set(app, key, "Hello World", -1);
			from_cache = redis.cache().getString(app, key, null);
			assert(Objects.equals(from_cache, "Hello World"));
			
			redis.cache().set(app, key, (String)null, -1);
			from_cache = redis.cache().getString(app, key, null);
			assert(Objects.equals(from_cache, null));
		}
		
		// Test binary data
		{
			CacheKey key = new CacheKey("binary-data://test-binary-data");
			byte data[] = createRandomBytes(1024*1024);
			
			redis.cache().set(app, key, data, -1);
			
			byte from_cache[]  =redis.cache().getBytes(app, key, null);
			
			assert(from_cache != null);
			assert(Arrays.equals(data, from_cache));
		}
		
		// Test scan
		{
			List<CacheKey> scan_test = new ArrayList();
			
			for ( int i = 0; i < 10_000; i++ )
			{
				CacheKey key = new CacheKey("scan-test://"+i);
				scan_test.add(key);
				redis.cache().set(app, key, ""+i, -1);
			}
			
			AccumulateKeyScanOp op = new AccumulateKeyScanOp();
			
			redis.cache().scan(app, null, new CacheKey("scan-test://"), op);
			
			assert(op.keys.size() == scan_test.size());
			
			assert(op.keys.containsAll(scan_test));
		}
		
		// Test that a key actually expires
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl-expiration");
			
			redis.cache().set(app, key, "Hello World", 1_000);
			
			String from_cache = redis.cache().getString(app, key, null);
			assert(Objects.equals(from_cache, "Hello World"));
			
			try { Thread.currentThread().sleep(2000); } catch(Exception e) {}
			
			from_cache = redis.cache().getString(app, key, null);
			
			assert(Objects.equals(from_cache, null));
		}
		
	}
	
	static private class AccumulateKeyScanOp implements ScanOperation
	{
		private Set<CacheKey> keys = new HashSet();
		
		@Override
		public void performOperation( Cache cache, CacheKey key )
		{
			keys.add(key);
		}
	}
	
	private boolean isRedisLive()
	{
		try
		{
			CacheKey key = new CacheKey("live-test://test-one-key");
			
			redis.cache().set(app, key, "hello world", -1);
			String get_result = redis.cache().getString(app, key, null);
			
			return get_result.equalsIgnoreCase("hello world");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	static private byte[] createRandomBytes(int size)
	{
		byte[] ret = new byte[size];
		
		Random r = new Random(); 
		
		r.nextBytes(ret);
			
		return ret;
	} 
	
	static private String createAcidString()
	{
		StringBuilder ret = new StringBuilder();
		
		for ( int i = 0; i < 10_000; i++ )
		{
			ret.append((char)i);
		}
		
		return ret.toString();
	}
}
