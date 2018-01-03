package org.jimmutable.cloud.cache.redis;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.Cache;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.cloud.cache.ScanOperation;
import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.objects.StandardImmutableObject;
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

	private ExecutorService send_async_thread_pool = DaemonThreadFactory.createDaemonFixedThreadPool(1);
	private ExecutorService recieve_message_thread_pool = DaemonThreadFactory.createDaemonFixedThreadPool(10);
	
	public Redis()
	{
		this("localhost", 6379);
	}
	
	public Redis(String host, int port)
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
		config.setMaxIdle(1000 * 60);
		config.setTestOnBorrow(false);
		
		
		pool = new JedisPool(config, "localhost", 6379);
	}
	
	private byte[] createCacheKeyBytes(ApplicationId app, CacheKey key, byte default_value[])
	{
		if ( app == null || key == null ) return default_value;
		
		StringBuilder builder = new StringBuilder();
		builder.append(app.toString());
		builder.append("/");
		builder.append(key.toString());
		
		return builder.toString().getBytes(StandardCharsets.UTF_8);
	}
	
	public void cacheSet(ApplicationId app, CacheKey key, byte data[], long max_ttl)
	{
		if ( data == null )
		{
			cacheDelete(app,key);
			return;
		}
		
		byte cache_key_bytes[] = createCacheKeyBytes(app,key,null);
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
		byte cache_key_bytes[] = createCacheKeyBytes(app,key,null);
		if ( cache_key_bytes == null ) return false;
		
		try(Jedis jedis = pool.getResource();)
		{
			return jedis.exists(cache_key_bytes);
		}
	}
	
	public void cacheSet(ApplicationId app, CacheKey key, String data, long max_ttl)
	{
		byte data_bytes[] = null;
		
		if ( data != null )
			data_bytes = data.getBytes(StandardCharsets.UTF_8);
		
		cacheSet(app,key,data_bytes, max_ttl);
	}
	
	public void cacheDelete(ApplicationId app, CacheKey key)
	{
		byte cache_key_bytes[] = createCacheKeyBytes(app,key,null);
		if ( cache_key_bytes == null ) return;
		
		try(Jedis jedis = pool.getResource();)
		{
			jedis.del(cache_key_bytes);
		}
	}
	
	public long cacheTTL(ApplicationId app, CacheKey key, long default_value)
	{
		byte cache_key_bytes[] = createCacheKeyBytes(app,key,null);
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
	
	public byte[] cacheGetBytes(ApplicationId app, CacheKey key, byte default_value[])
	{
		byte cache_key_bytes[] = createCacheKeyBytes(app,key,null);
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
	
	public String cacheGetString(ApplicationId app, CacheKey key, String default_value)
	{
		byte ret_bytes[] = cacheGetBytes(app,key,null);
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
	
	public void signalSendAsync(ApplicationId app, TopicId topic, StandardObject message)
	{
		send_async_thread_pool.submit(new SignalSendRunnable(app,topic,message));
	}
	
	private class SignalSendRunnable implements Runnable
	{
		ApplicationId app;
		TopicId topic;
		StandardObject message;
		
		private SignalSendRunnable(ApplicationId app, TopicId topic, StandardObject message)
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
	
	public void signalListen(ApplicationId app, TopicId topic, MessageListener listener)
	{
		Thread t = new Thread(new ListenRunnable(app,topic,listener));
		t.start();
	}
	
	private class ListenRunnable implements Runnable
	{
		private ApplicationId app;
		private TopicId topic;
		private MessageListener listener;
		
		private ListenRunnable(ApplicationId app, TopicId topic, MessageListener listener) 
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
		private MessageListener listener;
		
		public ListenSubscriber(MessageListener listener)
		{
			Validator.notNull(listener);
			
			this.listener = listener;
		}
	
		public void onMessage( String channel, String message )
		{
			try
			{
				StandardObject obj = StandardObject.deserialize(message);
				
				recieve_message_thread_pool.submit(new OnMessageReceivedRunnable(listener, obj));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private class OnMessageReceivedRunnable implements Runnable
	{
		private MessageListener listener;
		private StandardObject message;
		
		public OnMessageReceivedRunnable(MessageListener listener, StandardObject message)
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
