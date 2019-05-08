package org.jimmutable.cloud.cache;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.cloud.cache.redis.RedisScanOperation;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.messaging.queue.QueueId;
import org.jimmutable.cloud.messaging.queue.QueueListener;
import org.jimmutable.cloud.messaging.signal.SignalListener;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Test;

public class LowLevelRedisDriverTest extends StubTest
{
	private LowLevelRedisDriver redis;
	private ApplicationId app;
	private boolean is_redis_live = false;

	public LowLevelRedisDriverTest()
	{
		app = new ApplicationId("stub");
		redis = new LowLevelRedisDriver();

		is_redis_live = isRedisLive();
		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);
	}

	@Test
	public void testQueue()
	{
		if ( !is_redis_live )
		{
			System.out.println("Redis server not available, skipping queue unit test!");
			return;
		}

		// Test overflow protection
		{
			QueueId queue_id = new QueueId("overflow-test");

			redis.getSimpleQueue().clear(app, queue_id);
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 0);

			for ( int i = 0; i < 20_000; i++ )
				redis.getSimpleQueue().submit(app, queue_id, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(i)));

			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) < 20_000);
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) > 9_000);
		}

		// Test fan out
		{
			QueueId queue_id = new QueueId("fan-out-test");
			
			redis.getSimpleQueue().clear(app, queue_id);
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 0);
			
			for ( int i = 0; i < 1_000; i++ )
				redis.getSimpleQueue().submit(app, queue_id, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(i)));
			
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 1_000);
			
			TestListener one = new TestListener(10);
			TestListener two = new TestListener(10);
			TestListener three = new TestListener(10);
			TestListener four = new TestListener(10);
			
			redis.getSimpleQueue().startListening(app, queue_id, one, 1);
			redis.getSimpleQueue().startListening(app, queue_id, two, 1);
			redis.getSimpleQueue().startListening(app, queue_id, three, 1);
			redis.getSimpleQueue().startListening(app, queue_id, four, 2);
			
			System.out.println("Testing fan out");
			for ( int i = 0; i < 16; i++ )
			{
				try { Thread.currentThread().sleep(250); } catch(Exception e) {}
				
				System.out.println( 
							String.format("%d, %d, %d, %d", one.ids.size(), two.ids.size(), three.ids.size(), four.ids.size())
						);
			}
			
			System.out.println();
			
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 0);
			assertTrue(one.ids.size()+two.ids.size()+three.ids.size()+four.ids.size() > 980);
			
			assertTrue(four.ids.size() > one.ids.size());
			assertTrue(four.ids.size() > two.ids.size());
			assertTrue(four.ids.size() > three.ids.size());
		}
		
		//Test fan out with blocking work as well as listeners added after work has begun
		{
			QueueId queue_id = new QueueId("fan-out-blocking-test");

			redis.getSimpleQueue().clear(app, queue_id);
			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 0);

			for ( int i = 0; i < 100; i++ )
				redis.getSimpleQueue().submit(app, queue_id, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(i)));

			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 1_00);
			BlockingTestListener one = new BlockingTestListener(200);
			BlockingTestListener two = new BlockingTestListener(200);

			redis.getSimpleQueue().startListening(app, queue_id, one, 10);
			redis.getSimpleQueue().startListening(app, queue_id, two, 10);

			try
			{
				Thread.sleep(1200);
			}
			catch ( InterruptedException e1 )
			{
				e1.printStackTrace();
			}
			
			BlockingTestListener three = new BlockingTestListener(200);
			BlockingTestListener four = new BlockingTestListener(200);
			System.out.println("Work waiting on queue " + redis.getSimpleQueue().getQueueLength(app, queue_id, 0));
			redis.getSimpleQueue().startListening(app, queue_id, three, 10);
			redis.getSimpleQueue().startListening(app, queue_id, four, 10);
			System.out.println("Testing fan out");
			for ( int i = 0; i < 16; i++ )
			{
				try
				{
					Thread.currentThread().sleep(250);
				}
				catch ( Exception e )
				{
				}

				System.out.println(String.format("%d, %d, %d, %d", one.ids.size(), two.ids.size(), three.ids.size(), four.ids.size()));
			}

			System.out.println();

			assertTrue(redis.getSimpleQueue().getQueueLength(app, queue_id, 0) == 0);
			assertTrue(one.ids.size() + two.ids.size() + three.ids.size() + four.ids.size() > 98);

			assertTrue(four.ids.size() < one.ids.size());
			assertTrue(four.ids.size() < two.ids.size());
			assertTrue(three.ids.size() < one.ids.size());
			assertTrue(three.ids.size() < two.ids.size());

		}
		
		
	}

	@Test
	public void testSignal()
	{
		if ( !is_redis_live )
		{
			System.out.println("Redis server not available, skipping signal unit test!");
			return;
		}

		TestListener listener = new TestListener(0);
		TestListener listener2 = new TestListener(0);

		SignalTopicId topic = new SignalTopicId("test");

		redis.getSimpleSignal().startListening(app, topic, listener);
		redis.getSimpleSignal().startListening(app, topic, listener2);

		try
		{
			Thread.currentThread().sleep(250);
		}
		catch ( Exception e )
		{
		}

		redis.getSimpleSignal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(1)));
		redis.getSimpleSignal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(2)));
		redis.getSimpleSignal().sendAsync(app, topic, new StandardMessageOnUpsert(new Kind("foo"), new ObjectId(10)));

		try
		{
			Thread.currentThread().sleep(500);
		}
		catch ( Exception e )
		{
		}

		assertTrue(listener.ids.contains(new ObjectId(1)));
		assertTrue(listener.ids.contains(new ObjectId(2)));
		assertTrue(listener.ids.contains(new ObjectId(10)));

		assertTrue(listener2.ids.contains(new ObjectId(1)));
		assertTrue(listener2.ids.contains(new ObjectId(2)));
		assertTrue(listener2.ids.contains(new ObjectId(10)));
	}
	
	static private class BlockingTestListener implements SignalListener, QueueListener
	{
		private Set<ObjectId> ids = new ConcurrentHashSet<>();
		
		private AtomicInteger currently_working = new AtomicInteger();
		
		private long sleep_time;

		public BlockingTestListener( long sleep_time )
		{
			this.sleep_time = sleep_time;
		}

		@Override
		public void onMessageReceived( StandardObject message )
		{
			if ( !(message instanceof StandardMessageOnUpsert) )
				return;

			StandardMessageOnUpsert upsert_message = (StandardMessageOnUpsert) message;

			while ( currently_working.get() > 5 )
			{
				//System.out.println(ids.size());
				//System.out.println("STUCK!");
			}
			
			currently_working.getAndIncrement();
			
			ids.add(upsert_message.getSimpleObjectId());

			if ( sleep_time > 0 )
				try
				{
					Thread.currentThread().sleep(sleep_time);
				}
				catch ( Exception e )
				{
				}
			
			currently_working.getAndDecrement();
		}
	}

	static private class TestListener implements SignalListener, QueueListener
	{
		private Set<ObjectId> ids = new HashSet();
		
		private long sleep_time;

		public TestListener( long sleep_time )
		{
			this.sleep_time = sleep_time;
		}

		@Override
		public void onMessageReceived( StandardObject message )
		{
			if ( !(message instanceof StandardMessageOnUpsert) )
				return;

			StandardMessageOnUpsert upsert_message = (StandardMessageOnUpsert) message;
			
			ids.add(upsert_message.getSimpleObjectId());

			if ( sleep_time > 0 )
				try
				{
					Thread.currentThread().sleep(sleep_time);
				}
				catch ( Exception e )
				{
				}
			
		}
	}

	@Test
	public void testCache()
	{
		if ( !is_redis_live )
		{
			System.out.println("Redis server not available, skipping cache unit test!");
			return;
		}

		// Test exists
		{
			CacheKey key1 = new CacheKey("exist-test://one");
			CacheKey key2 = new CacheKey("exist-test://two");

			redis.getSimpleCache().set(app, key1, "Hello World", -1);

			assertTrue(redis.getSimpleCache().exists(app, key1) == true);
			assertTrue(redis.getSimpleCache().exists(app, key2) == false);
		}

		// Test the acid string as a string value
		{
			CacheKey key = new CacheKey("acid-string-test://test-acid-string-value");

			String acid_string = createAcidString();
			redis.getSimpleCache().set(app, key, acid_string, -1);

			String from_cache = redis.getSimpleCache().getString(app, key, null);

			assertTrue(Objects.equals(acid_string, from_cache));
		}

		// Test the get on an empty string
		{
			CacheKey key = new CacheKey("get-unset-test://a-key-that-is-not-set");

			String from_cache = redis.getSimpleCache().getString(app, key, null);

			assertTrue(Objects.equals(from_cache, null));
		}

		// Test the acid string as a key and a value!
		{
			String acid_string = createAcidString();

			CacheKey key = new CacheKey("acid-string-test://" + acid_string);

			redis.getSimpleCache().set(app, key, acid_string, -1);
			String from_cache = redis.getSimpleCache().getString(app, key, null);

			assertTrue(Objects.equals(acid_string, from_cache));
		}

		// Test TTL
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl");

			redis.getSimpleCache().set(app, key, "Hello World", 10_000);
			long value = redis.getSimpleCache().getTTL(app, key, -1);

			assertTrue(value > 8_500);
		}

		// Test TTL (unset)
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl-unset");

			redis.getSimpleCache().set(app, key, "Hello World", -1);
			long value = redis.getSimpleCache().getTTL(app, key, -1);

			assertTrue(value == -1);

			redis.getSimpleCache().set(app, key, "Hello World", 0);
			value = redis.getSimpleCache().getTTL(app, key, -1);

			assertTrue(value == -1);
		}

		// Test delete
		{
			CacheKey key = new CacheKey("ttl-delete://test-delete");

			redis.getSimpleCache().set(app, key, "Hello World", -1);

			String from_cache = redis.getSimpleCache().getString(app, key, null);
			assertTrue(Objects.equals(from_cache, "Hello World"));

			redis.getSimpleCache().delete(app, key);

			from_cache = redis.getSimpleCache().getString(app, key, null);

			assertTrue(Objects.equals(from_cache, null));

			// test delete on null data
			redis.getSimpleCache().set(app, key, "Hello World", -1);
			from_cache = redis.getSimpleCache().getString(app, key, null);
			assertTrue(Objects.equals(from_cache, "Hello World"));

			redis.getSimpleCache().set(app, key, (String) null, -1);
			from_cache = redis.getSimpleCache().getString(app, key, null);
			assertTrue(Objects.equals(from_cache, null));
		}

		// Test binary data
		{
			CacheKey key = new CacheKey("binary-data://test-binary-data");
			byte data[] = createRandomBytes(1024 * 1024);

			redis.getSimpleCache().set(app, key, data, -1);

			byte from_cache[] = redis.getSimpleCache().getBytes(app, key, null);

			assertTrue(from_cache != null);
			assertTrue(Arrays.equals(data, from_cache));
		}

		// Test scan
		{
			List<CacheKey> scan_test = new ArrayList();

			for ( int i = 0; i < 10_000; i++ )
			{
				CacheKey key = new CacheKey("scan-test://" + i);
				scan_test.add(key);
				redis.getSimpleCache().set(app, key, "" + i, -1);
			}

			AccumulateKeyScanOp op = new AccumulateKeyScanOp();

			redis.getSimpleCache().scan(app, new CacheKey("scan-test://"), op);

			assertTrue(op.keys.size() == scan_test.size());

			assertTrue(op.keys.containsAll(scan_test));
		}

		// Test that a key actually expires
		{
			CacheKey key = new CacheKey("ttl-test://test-ttl-expiration");

			redis.getSimpleCache().set(app, key, "Hello World", 1_000);

			String from_cache = redis.getSimpleCache().getString(app, key, null);
			assertTrue(Objects.equals(from_cache, "Hello World"));

			try
			{
				Thread.currentThread().sleep(2000);
			}
			catch ( Exception e )
			{
			}

			from_cache = redis.getSimpleCache().getString(app, key, null);

			assertTrue(Objects.equals(from_cache, null));
		}

	}

	static private class AccumulateKeyScanOp implements RedisScanOperation
	{
		private Set<CacheKey> keys = new HashSet();

		@Override
		public void performOperation( LowLevelRedisDriver cache, CacheKey key )
		{
			keys.add(key);
		}
	}

	private boolean isRedisLive()
	{
		try
		{
			CacheKey key = new CacheKey("live-test://test-one-key");

			redis.getSimpleCache().set(app, key, "hello world", -1);
			String get_result = redis.getSimpleCache().getString(app, key, null);

			return get_result.equalsIgnoreCase("hello world");
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}

	static private byte[] createRandomBytes( int size )
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
			ret.append((char) i);
		}

		return ret.toString();
	}
}
