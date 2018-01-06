package org.jimmutable.cloud.cache.redis;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.Cache;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.cloud.cache.ScanOperation;
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

public class Redis
{
	private JedisPool pool;
	
	private ExecutorService pool_send = DaemonThreadFactory.createDaemonFixedThreadPool(1);
	private ExecutorService pool_receive = DaemonThreadFactory.createDaemonFixedThreadPool(10);
	
	private RedisCache cache;
	private RedisSignal signal;
	private RedisQueue queue;
	
	public Redis()
	{
		this("localhost", 6379);
	}
	
	public Redis(String host, int port)
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(250);
		config.setMaxIdle(1000 * 60);
		config.setTestOnBorrow(false);
		
		
		pool = new JedisPool(config, "localhost", 6379);
		cache = new RedisCache();
		queue = new RedisQueue();
		signal = new RedisSignal();
	}
	
	public RedisCache cache() { return cache; }
	public RedisSignal signal() { return signal; }
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
				}
			}
		}
		
		public boolean exists(ApplicationId app, CacheKey key)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return false;
			
			try(Jedis jedis = pool.getResource();)
			{
				return jedis.exists(cache_key_bytes);
			}
		}
		
		public void set(ApplicationId app, CacheKey key, String data, long max_ttl)
		{
			byte data_bytes[] = null;
			
			if ( data != null )
				data_bytes = data.getBytes(StandardCharsets.UTF_8);
			
			set(app,key,data_bytes, max_ttl);
		}
		
		public void delete(ApplicationId app, CacheKey key)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return;
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.del(cache_key_bytes);
			}
		}
		
		public long getTTL(ApplicationId app, CacheKey key, long default_value)
		{
			byte cache_key_bytes[] = createKeyBytes(app,key,null);
			if ( cache_key_bytes == null ) return default_value;
			
			try(Jedis jedis = pool.getResource();)
			{
				Long ret = jedis.ttl(cache_key_bytes);
				
				if ( ret == null ) return default_value;
				
				if ( ret.longValue() <= 0 ) return default_value;
				return ret.longValue()*1000; // conver to ms
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
		
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
		
		public String getString(ApplicationId app, CacheKey key, String default_value)
		{
			byte ret_bytes[] = getBytes(app,key,null);
			if ( ret_bytes == null ) return default_value;
			
			return new String(ret_bytes,StandardCharsets.UTF_8);
		}
		
		public void scan(ApplicationId app, Cache cache, CacheKey prefix, ScanOperation operation)
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
							
							operation.performOperation(cache, new CacheKey(key));
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
		public void sendAsync(ApplicationId app, SignalTopicId topic, StandardObject message)
		{
			pool_send.submit(new SignalSendRunnable(app,topic,message));
		}
		
		public void send(ApplicationId app, SignalTopicId topic, StandardObject message)
		{
			new SignalSendRunnable(app,topic,message).run();
		}
		
		public void startListening(ApplicationId app, SignalTopicId topic, SignalListener listener)
		{
			Thread t = new Thread(new ListenRunnable(app,topic,listener));
			t.start();
		}
	
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
				while(true)
				{
					try
					{
						try(Jedis jedis = pool.getResource();)
						{
							jedis.subscribe(new ListenSubscriber(listener), app+"/"+topic);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					try { Thread.currentThread().sleep(1000); } catch(Exception e) {}
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
		
		private String getKey(ApplicationId app, QueueId queue)
		{
			return "$queue/"+app+"/"+queue;
		}
		
		public int getQueueLength(ApplicationId app, QueueId queue, int default_value)
		{
			if ( app == null || queue == null ) return default_value;
			
			try(Jedis jedis = pool.getResource();)
			{
				Long ret = jedis.llen(getKey(app,queue));
				
				if ( ret == null ) return default_value;
				if ( ret.longValue() < 0 ) return default_value;
				
				return ret.intValue();
			}
		}
		
		public void clear(ApplicationId app, QueueId queue)
		{
			Validator.notNull(app,queue);
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.del(getKey(app,queue));
			}
		}
		
		public void submitAsync(ApplicationId app, QueueId queue, StandardObject message)
		{
			Validator.notNull(app, queue, message);
			
			Runnable send_task = new Runnable()
			{
				public void run() 
				{
					try(Jedis jedis = pool.getResource();)
					{
						jedis.lpush(getKey(app,queue), message.serialize(Format.JSON));
						
						if ( r.nextInt(100) == 52 ) // about once per one hundred inserts, trim to 10_000 elements, for performance
						{
							jedis.ltrim(getKey(app,queue), 0, 10_000);
						}
					}
				}
			};
			
			pool_send.submit(send_task);
		}
		
		public void submit(ApplicationId app, QueueId queue, StandardObject message)
		{
			Validator.notNull(app, queue, message);
			
			try(Jedis jedis = pool.getResource();)
			{
				jedis.lpush(getKey(app,queue), message.serialize(Format.JSON));
				
				if ( r.nextInt(100) == 52 ) // about once per one hundred inserts, trim to 10_000 elements, for performance
				{
					jedis.ltrim(getKey(app,queue), 0, 10_000);
				}
			}
		}
		
		public void startListening(ApplicationId app, QueueId queue, QueueListener listener, int num_worker_threads)
		{
			Validator.notNull(app, queue, listener);
			
			for ( int i = 0; i < num_worker_threads; i++ )
			{
				Thread t = new Thread(new ListenRunnable(app, queue, listener));
				t.start();
			}
		}
		
		private class ListenRunnable implements Runnable
		{
			private ApplicationId app;
			private QueueId queue;
			private QueueListener listener;
			
			private ListenRunnable(ApplicationId app, QueueId queue, QueueListener listener) 
			{
				Validator.notNull(app,queue,listener);
				
				this.app = app;
				this.queue = queue;
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
							try { Thread.currentThread().sleep(500); } catch(Exception e) {}
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
					String obj_str = jedis.rpop(getKey(app,queue));
					
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
