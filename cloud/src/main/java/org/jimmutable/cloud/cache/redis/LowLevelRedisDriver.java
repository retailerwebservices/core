package org.jimmutable.cloud.cache.redis;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.cloud.new_messaging.queue.QueueId;
import org.jimmutable.cloud.new_messaging.queue.QueueListener;
import org.jimmutable.cloud.new_messaging.signal.SignalListener;
import org.jimmutable.cloud.new_messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.threading.DaemonThreadFactory;
import org.jimmutable.core.utils.Validator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Low level driver class for Redis.
 * 
 * DO NOT MODIFY UNLESS YOU REALLY UNDERSTAND REDIS, THREADING *AND* WHAT YOU ARE DOING 
 * 
 * All methods are *fully* thread safe
 * 
 * @author kanej
 *
 */
public class LowLevelRedisDriver
{
	static public final int DEFAULT_PORT_REDIS = 6379;
	
	// CODEREVIEW Are JedisPool and Jedis thread safe? Or do we need to wrap accesses to them inside our own code?
	private JedisPool pool;
	
	private ExecutorService pool_send = DaemonThreadFactory.createDaemonFixedThreadPool(1); // CODEREVIEW Per previous conversations, I though object creation/assignment was bad outside the constructor? -JMD
	private ExecutorService pool_receive = DaemonThreadFactory.createDaemonFixedThreadPool(10);
	
	private RedisCache cache;
	private RedisSignal signal;
	private RedisQueue queue;
	
	public LowLevelRedisDriver()
	{
		this("localhost", DEFAULT_PORT_REDIS);
	}
	
	public LowLevelRedisDriver(String host, int port)
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(250);
		config.setMaxIdle(1000 * 60);
		config.setTestOnBorrow(false);
		
		pool = new JedisPool(config, host, port);
		cache = new RedisCache();
		queue = new RedisQueue();
		signal = new RedisSignal();
	}
	
	public boolean isRedisUp()
	{
		try(Jedis jedis = pool.getResource();)
		{
			String str = jedis.ping();
			return str.equalsIgnoreCase("PONG");
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Access the cache functionality of the Redis driver
	 * 
	 * @return
	 */
    // CODEREVIEW Tsk, tsk. This should be getSimpleCache() (just like in CEE). -JMD
	public RedisCache cache() { return cache; }
	
	/**
	 * Access the siganl functionality of the Redis driver
	 * 
	 * @return
	 */
    // CODEREVIEW Tsk, tsk. This should be getSimpleSignal() (just like in CEE). -JMD
	public RedisSignal signal() { return signal; }
	
	/**
	 * Access the queue functionality of the Redis driver
	 * 
	 * @return
	 */
	// CODEREVIEW Tsk, tsk. This should be getSimpleQueue() (just like in CEE). -JMD
	public RedisQueue queue() { return queue; }
	
	public class RedisCache
	{
		private byte[] createKeyBytes(ApplicationId app, CacheKey key, byte default_value[])
		{
			if ( app == null || key == null ) return default_value;
			
			StringBuilder builder = new StringBuilder();
			builder.append(app.toString());
			builder.append("/");
			builder.append(key.toString());
			
			return builder.toString().getBytes(StandardCharsets.UTF_8);
		}
		
		/**
		 * Set the value of a key in the cache (optionally with a maximum time to live).
		 * Setting with null data has the effect of deleting the key from the cache.
		 * Null keys and app ids result in the function doing nothing
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The key to set
		 * @param data
		 *            The data to write
		 * @param max_ttl
		 *            The maximum length of time that the data will remain in the cache
		 *            (in ms). Zero or negative values have the meaning of "infinite"
		 */
		// CODEREVIEW Since there is an explicit delete operation, why support delete on null? -JMD
		public void set(ApplicationId app, CacheKey key, byte data[], long max_ttl)
		{
			if ( data == null )
			{
				delete(app,key);
				return;
			}
			
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return;
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.set(cache_key_bytes, data);
				
				if ( max_ttl > 0 )
				{ 
					max_ttl /= 1000;
					jedis.expire(cache_key_bytes, (int)max_ttl);
					// CODEREVIEW If redis doesn't support ms resolution, why not just make the interface seconds? -JMD
				}
			}
		}
		
		/**
		 * Test for the existence of a key. Null app or key results in a false return
		 * value
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The cache key
		 * 
		 * @return
		 */
		public boolean exists(ApplicationId app, CacheKey key)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return false;
			
			try(Jedis jedis = pool.getResource();)
			{
				return jedis.exists(cache_key_bytes);
			}
		}
		
		/**
		 * Set a string value.
		 *
		 * Setting with null data has the effect of deleting the key from the cache.
		 * Null keys and app ids result in the function doing nothing
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The key to set
		 * @param data
		 *            The data to write
		 * @param max_ttl
		 *            The maximum length of time that the data will remain in the cache
		 *            (in ms). Zero or negative values have the meaning of "infinite"
		 */
		public void set(ApplicationId app, CacheKey key, String data, long max_ttl)
		{
			byte data_bytes[] = null;
			
			if ( data != null )
				data_bytes = data.getBytes(StandardCharsets.UTF_8);
			
			set(app,key,data_bytes, max_ttl);
		}
		
		/**
		 * Delete a key (and its value) from the cache.
		 * 
		 * Does nothing if app or key is null.  Does nothing if the key does not exist
		 * 
		 * @param app The application id
		 * @param key The key to delete
		 */
		public void delete(ApplicationId app, CacheKey key)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return;
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.del(cache_key_bytes);
			}
		}
		
		/**
		 * Get the remaining time to live (TTL) for a key. If the key does not have a
		 * TTL default value is returned. If the key does not exist, then default_value
		 * is returned
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The key
		 * @param default_value
		 *            The value to return in the event the key does not have a TTL or an
		 *            error occurs
		 * @return
		 */
		public long getTTL(ApplicationId app, CacheKey key, long default_value)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return default_value;
			
			try(Jedis jedis = pool.getResource();)
			{
				Long ret = jedis.ttl(cache_key_bytes);
				
				if ( ret == null ) return default_value;
				
				if ( ret.longValue() <= 0 ) return default_value;
				return ret.longValue()*1000; // convert to ms
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
		
		/**
		 * Get a value from the cache as a byte array
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The key
		 * @param default_value
		 *            The value to return in the event of an error
		 * 
		 * @return The value of key or default_value if any error occurs
		 */
		public byte[] getBytes(ApplicationId app, CacheKey key, byte default_value[])
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return default_value;
			
			try(Jedis jedis = pool.getResource();)
			{
				byte ret[] = jedis.get(cache_key_bytes);
				if ( ret == null ) return default_value;
				
				return ret;
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
		
		/**
		 * Get a value from the cache as a String
		 * 
		 * @param app
		 *            The application id
		 * @param key
		 *            The key
		 * @param default_value
		 *            The value to return in the event of an error
		 * 
		 * @return The value of key or default_value if any error occurs
		 */
		
		public String getString(ApplicationId app, CacheKey key, String default_value)
		{
			byte ret_bytes[] = getBytes(app,key,null);
			if ( ret_bytes == null ) return default_value;
			
			return new String(ret_bytes,StandardCharsets.UTF_8);
		}
		
		/**
		 * Scan (call operation.performOperation on all keys with the specified prefix) 
		 * 
		 * @param app The application id
		 * @param prefix The ScanOperation
		 * @param operation
		 */
		public void scan(ApplicationId app, CacheKey prefix, RedisScanOperation operation)
		{
			String app_str = app.getSimpleValue();
			
			Validator.notNull(app, prefix, operation);
			
			try(Jedis jedis = pool.getResource();)
			{
				ScanParams params = new ScanParams();
				
				if ( prefix != null )
					params = params.match(app+"/"+prefix+"*");
				
				params.count(100);
				
				String cursor = "0";
				
				while(true)
				{
					ScanResult<String> result = jedis.scan(cursor, params);
					
					for ( String key : result.getResult() )
					{
						try
						{
							key = key.substring(app_str.length()+1);
							
							operation.performOperation(LowLevelRedisDriver.this, new CacheKey(key));
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
	
	public class RedisSignal
	{
		/**
		 * Asynchronously send a message to a signal
		 * 
		 * @param app
		 *            The application id
		 * @param topic
		 *            The topic
		 * @param message
		 *            The message to send
		 */
		@SuppressWarnings("rawtypes")
        public void sendAsync(ApplicationId app, SignalTopicId topic, StandardObject message)
		{
			if ( app == null || topic == null || message == null ) return;
			
			pool_send.submit(new SignalSendRunnable(app,topic,message));
		}
		
		/**
		 * Send a message to a signal
		 * 
		 * @param app
		 *            The application id
		 * @param topic
		 *            The topic
		 * @param message
		 *            The message to send
		 */
		@SuppressWarnings("rawtypes")
        public void send(ApplicationId app, SignalTopicId topic, StandardObject message)
		{
			if ( app == null || topic == null || message == null ) return;
			
			new SignalSendRunnable(app,topic,message).run();
		}
		
		/**
		 * Begin listening for messages on a given topic
		 * 
		 * @param app
		 *            The application id
		 * @param topic
		 *            The topic
		 * @param listener
		 *            The listener
		 */
		public void startListening(ApplicationId app, SignalTopicId topic, SignalListener listener)
		{
			Validator.notNull(app, topic, listener);
			
			Thread t = new Thread(new ListenRunnable(app,topic,listener));
			t.start();
		}
	
        @SuppressWarnings("rawtypes")
		private class SignalSendRunnable implements Runnable
		{
			ApplicationId app;
			SignalTopicId topic;
			StandardObject message;
			
            private SignalSendRunnable(ApplicationId app, SignalTopicId topic, StandardObject message)
			{
				Validator.notNull(app, topic, message);
				
				this.app = app;
				this.topic = topic;
				this.message = message;
			}
			public void run()
			{
				try(Jedis jedis = pool.getResource();)
				{
				    // CODEREVIEW I suggest encapusulating this magic string in a getKey method like RedisQueue -JMD
					jedis.publish(app+"/"+topic, message.serialize(Format.JSON));
				}
			}
		}
	
		
		
		private class ListenRunnable implements Runnable
		{
			private ApplicationId app;
			private SignalTopicId topic;
			private SignalListener listener;
			
			private ListenRunnable(ApplicationId app, SignalTopicId topic, SignalListener listener) 
			{
				Validator.notNull(app,topic,listener);
				
				this.app = app;
				this.topic = topic;
				this.listener = listener;
			}
			public void run()
			{
                // CODEREVIEW Why do you have to keep re-subscribing to the signal topic? -JMD
				while(true)
				{
					try
					{
						try(Jedis jedis = pool.getResource();)
						{
						    // CODEREVIEW Magic string should be encapsulated in a getKey method (or similar) -JMD
							jedis.subscribe(new ListenSubscriber(listener), app+"/"+topic);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					try { Thread.sleep(1000); } catch(Exception e) {}
				}
			}
		}
		
		private class ListenSubscriber extends JedisPubSub
		{
			private SignalListener listener;
			
			public ListenSubscriber(SignalListener listener)
			{
				Validator.notNull(listener);
				
				this.listener = listener;
			}
		
            @SuppressWarnings("rawtypes")
			public void onMessage( String channel, String message )
			{
				try
				{
					StandardObject obj = StandardObject.deserialize(message);
					
					pool_receive.submit(new OnMessageReceivedRunnable(listener, obj));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
        @SuppressWarnings("rawtypes")
		private class OnMessageReceivedRunnable implements Runnable
		{
			private SignalListener listener;
			private StandardObject message;
			
			public OnMessageReceivedRunnable(SignalListener listener, StandardObject message)
			{
				Validator.notNull(listener, message);
				this.listener = listener;
				this.message = message;
			}
			
			public void run()
			{
				listener.onMessageReceived(message);
			}
		}
	}
	
	public class RedisQueue
	{
		Random r = new Random();
		
		private String getKey(ApplicationId app, QueueId queue_id)
		{
			return "$queue/"+app+"/"+queue_id;
		}
		
		/**
		 * Get the current queue length
		 * 
		 * @param app
		 * @param queue_id
		 * @param default_value
		 * @return
		 */
		public int getQueueLength(ApplicationId app, QueueId queue_id, int default_value)
		{
			if ( app == null || queue_id == null ) return default_value;
			
			try(Jedis jedis = pool.getResource();)
			{
				Long ret = jedis.llen(getKey(app,queue_id));
				
				if ( ret == null ) return default_value;
				if ( ret.longValue() < 0 ) return default_value;
				
				return ret.intValue();
			}
		}
		
		/**
		 * Clear a queue
		 * 
		 * @param app
		 * @param queue
		 */
		public void clear(ApplicationId app, QueueId queue_id)
		{
			Validator.notNull(app,queue_id);
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.del(getKey(app,queue_id));
			}
		}
		
		/**
		 * Submit a message to a queue (asynchronously) 
		 * @param app
		 * @param queue_id
		 * @param message
		 */
		@SuppressWarnings("rawtypes")
        public void submitAsync(ApplicationId app, QueueId queue_id, StandardObject message)
		{
			if ( app == null || queue_id == null || message == null ) return;
			
			Runnable send_task = new Runnable()
			{
				public void run() 
				{
					try(Jedis jedis = pool.getResource();)
					{
						jedis.lpush(getKey(app,queue_id), message.serialize(Format.JSON));
						
						if ( r.nextInt(100) == 52 ) // about once per one hundred inserts, trim to 10_000 elements, for performance
						{
							jedis.ltrim(getKey(app,queue_id), 0, 10_000);
						}
					}
				}
			};
			
			pool_send.submit(send_task);
		}
		
		/**
		 * Submit a message to a queue (synchronously) 
		 * 
		 * @param app
		 * @param queue
		 * @param message
		 */
		@SuppressWarnings("rawtypes")
        public void submit(ApplicationId app, QueueId queue_id, StandardObject message)
		{
		    /*
		     * CODEREVIEW
		     * submitAsync and submit should share code if possible.
		     * One idea is to externalize the Runnable from submitAsync into a
		     * private nested class. Then call send_task.run() in submit.
		     * -JMD
		     */
			if ( app == null || queue_id == null || message == null ) return;
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.lpush(getKey(app,queue_id), message.serialize(Format.JSON));
				
				if ( r.nextInt(100) == 52 ) // about once per one hundred inserts, trim to 10_000 elements, for performance
				{
					jedis.ltrim(getKey(app,queue_id), 0, 10_000);
				}
			}
		}
		
		/**
		 * Begin processing messages from a queue with the specified number of worker threads
		 * 
		 * @param app
		 * @param queue
		 * @param listener
		 * @param num_worker_threads
		 */
		public void startListening(ApplicationId app, QueueId queue_id, QueueListener listener, int num_worker_threads)
		{
			Validator.notNull(app, queue_id, listener);
			Validator.min(num_worker_threads, 1);
			
			for ( int i = 0; i < num_worker_threads; i++ )
			{
				Thread t = new Thread(new ListenRunnable(app, queue_id, listener));
				t.start();
			}
		}
		
        @SuppressWarnings("rawtypes")
		private class ListenRunnable implements Runnable
		{
			private ApplicationId app;
			private QueueId queue_id;
			private QueueListener listener;
			
			private ListenRunnable(ApplicationId app, QueueId queue_id, QueueListener listener) 
			{
				Validator.notNull(app,queue,listener);
				
				this.app = app;
				this.queue_id = queue_id;
				this.listener = listener;
			}
			
			public void run()
			{
				while(true)
				{
					try
					{
						StandardObject message = popOneObject(null);
						
						if ( message == null ) 
						{
							// If there is no message, sleep for 1/2 second before trying again
							try { Thread.sleep(500); } catch(Exception e) {}
							continue;
						}
						else
						{
							listener.onMessageReceived(message);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
            private StandardObject popOneObject(StandardObject default_value)
			{
				try(Jedis jedis = pool.getResource();)
				{
					String obj_str = jedis.rpop(getKey(app,queue_id));
					
					if ( obj_str == null ) return default_value;
					
					return StandardObject.deserialize(obj_str); 
				}
				catch(Exception e)
				{
					return default_value;
				}
			}
		}
	}
}
