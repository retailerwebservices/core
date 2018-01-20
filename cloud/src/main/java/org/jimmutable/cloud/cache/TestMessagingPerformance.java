package org.jimmutable.cloud.cache;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.new_messaging.signal.Signal;
import org.jimmutable.cloud.new_messaging.signal.SignalListener;
import org.jimmutable.cloud.new_messaging.signal.SignalRedis;
import org.jimmutable.cloud.new_messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.NetUtils;
import org.jimmutable.core.utils.RateLimitingEmitter;
import org.jimmutable.core.utils.Sink;
import org.jimmutable.core.utils.Source;

public class TestMessagingPerformance
{
	static private SignalTopicId topic_id = new SignalTopicId("test-topic");
	static private Signal signal;
	static private List<MessageListener> listeners = new ArrayList();
	static private Kind kind = new Kind("foo"); 
	static private RateLimitingEmitter emitter;
	
	static public void main(String args[])
	{
		CloudExecutionEnvironment.startupStubTest(new ApplicationId("test"));
		
		String host = NetUtils.extractHostFromHostPortPair(System.getenv("jimmutable_messaging_server"), "localhost");
		int port = NetUtils.extractPortFromHostPortPair(System.getenv("jimmutable_messaging_server"), LowLevelRedisDriver.DEFAULT_PORT_REDIS);
		
		System.out.println("Starting test of messaging on "+host+":"+port+" in 2 seconds");
		
		try { Thread.currentThread().sleep(2000); } catch(Exception e) {}
		
		signal = new SignalRedis(new ApplicationId("test"), new LowLevelRedisDriver (host, port);
		
		emitter = RateLimitingEmitter.startEmitter(new MessageSource(), new MessageSink(), 1.0f);
		
		MessageListener listener;
		
		for ( int i = 0; i < 10; i++ )
		{
			listener = new MessageListener();
			listeners.add(listener);
			
			signal.startListening(topic_id, listener);
		}
	
		
		Thread status_printer = new Thread(new StatusPrinter());
		status_printer.start();
	}
	
	static public class MessageSource implements Source<StandardMessageOnUpsert>
	{
		private int count = 0;

		@Override
		public StandardMessageOnUpsert getNext( StandardMessageOnUpsert default_value )
		{
			count++;
			return new StandardMessageOnUpsert(kind, new ObjectId(count)); 
		}
	}
	
	static public class MessageSink implements Sink<StandardMessageOnUpsert>
	{
		public void onEmit( StandardMessageOnUpsert value )
		{
			signal.sendAsync(topic_id, value);
		}
	}

	
	static public class MessageListener implements SignalListener
	{
		private int count;
		
		public MessageListener()
		{
			count = 0;
		}
	
		@Override
		public void onMessageReceived( StandardObject message )
		{
			count++;
		}
		
	}
	
	static public class StatusPrinter implements Runnable
	{
		int cycle_count = 0;
		
		public void run()
		{
			while(true)
			{
				try
				{
					cycle_count++;
					
					for ( MessageListener listener : listeners )
					{
						listener.count = 0;
					}
					
					long start_time = System.currentTimeMillis();
					
					Thread.currentThread().sleep(1000);
					
					int total_count = 0;
					for ( MessageListener listener : listeners )
					{
						total_count += listener.count;
					}
					
					float seconds = (float)(System.currentTimeMillis()-start_time)/1000.0f;
					float count_per_sec = (float)total_count / seconds;
					
					System.out.println(String.format("Message send rate %.2f/sec, Messages received: %.2f/sec", emitter.getRate(), count_per_sec));
					
					if ( cycle_count % 5 == 0 ) 
						emitter.setRate(emitter.getRate()*2);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
