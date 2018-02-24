package org.jimmutable.cloud.cache;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.messaging.queue.IQueue;
import org.jimmutable.cloud.messaging.queue.QueueId;
import org.jimmutable.cloud.messaging.queue.QueueListener;
import org.jimmutable.cloud.messaging.queue.QueueRedis;
import org.jimmutable.cloud.messaging.signal.ISignal;
import org.jimmutable.cloud.messaging.signal.SignalListener;
import org.jimmutable.cloud.messaging.signal.SignalRedis;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.NetUtils;
import org.jimmutable.core.utils.RateLimitingEmitter;
import org.jimmutable.core.utils.Sink;
import org.jimmutable.core.utils.Source.CountSource;

public class TestMessagingPerformance
{
	static private SignalTopicId topic_id = new SignalTopicId("test-topic");
	static private QueueId queue_id = new QueueId("test-queue");
	static private Kind kind = new Kind("foo"); 
	
	static public void main(String args[])
	{
		CommandLineParser parser = new DefaultParser();
		
		try
		{
			CommandLine cmd = parser.parse(createOptions(), args);
			
			String test = cmd.getOptionValue("test");
			
			if ( test == null || (!test.equalsIgnoreCase("signal") && !test.equalsIgnoreCase("queue")) )
			{
				onUsageError("You must specify a test with -test signal or -test queue");
			}
			
			String mode = cmd.getOptionValue("mode");
			
			if ( mode == null || (!mode.equalsIgnoreCase("source") && !mode.equalsIgnoreCase("sink")) )
			{
				onUsageError("You must specify a mode with -mode source or -mode sink");
			}
			
			String host = NetUtils.extractHostFromHostPortPair(cmd.getOptionValue("server"), "localhost");
			int port = NetUtils.extractPortFromHostPortPair(cmd.getOptionValue("server"), LowLevelRedisDriver.DEFAULT_PORT_REDIS);
			
			CloudExecutionEnvironment.startupStubTest(new ApplicationId("test"));
			
			if ( test.equalsIgnoreCase("signal") && mode.equalsIgnoreCase("source") ) testSignalSource(host, port);
			if ( test.equalsIgnoreCase("signal") && mode.equalsIgnoreCase("sink") ) testSignalSink(host, port);
			if ( test.equalsIgnoreCase("queue") && mode.equalsIgnoreCase("source") ) testQueueSource(host, port);
			if ( test.equalsIgnoreCase("queue") && mode.equalsIgnoreCase("sink") ) testQueueSink(host, port);
		}
		catch(Exception e)
		{
			onUsageError("command line parse error: "+e);
		}
	}
	
	static private void testSignalSource(String host, int port)
	{
		System.out.println(String.format("Starting signal source %s:%d in 2 sec", host, port));
		silentSleep(2000);
		
		LowLevelRedisDriver redis = new LowLevelRedisDriver (host, port);
		ISignal signal = new SignalRedis(new ApplicationId("test"), redis);
		
		RateLimitingEmitter message_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SendSignal(signal), 1.0f);
		RateLimitingEmitter status_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SendSignalStatusPrinter(redis, message_emitter), 1.0f);
	}
	
	static public class SendSignal implements Sink<Integer>
	{
		private ISignal signal;
		
		public SendSignal(ISignal signal)
		{
			this.signal = signal;
		}
		
		public void onEmit( Integer value )
		{
			signal.sendAsync(topic_id, new StandardMessageOnUpsert(kind,new ObjectId(value)));
		}
	}
	
	static public class SendSignalStatusPrinter implements Sink<Integer>
	{
		private RateLimitingEmitter message_emitter;
		private LowLevelRedisDriver redis;
		
		public SendSignalStatusPrinter(LowLevelRedisDriver redis, RateLimitingEmitter message_emitter)
		{
			this.redis = redis;
			this.message_emitter = message_emitter;
		}
		
		public void onEmit( Integer count )
		{
			System.out.println(String.format("signal source %d: Redis Up: %b, Signal send rate: %.2f/sec", count, redis.isRedisUp(), message_emitter.getRate()));
			
			if ( count.intValue() % 5 == 0 ) 
				message_emitter.setRate(message_emitter.getRate()*2.0f);
		}
	}
	
	static private void testQueueSource(String host, int port)
	{
		System.out.println(String.format("Starting queue source %s:%d in 2 sec", host, port));
		silentSleep(2000);
		
		LowLevelRedisDriver redis = new LowLevelRedisDriver (host, port);
		QueueRedis queue = new QueueRedis(new ApplicationId("test"), redis);
		
		RateLimitingEmitter message_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SendQueue(queue), 1.0f);
		RateLimitingEmitter status_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SendQueueStatusPrinter(redis, message_emitter, queue), 1.0f);
	}
	
	static public class SendQueue implements Sink<Integer>
	{
		private IQueue queue;
		
		public SendQueue(IQueue queue)
		{
			this.queue = queue;
		}
		
		public void onEmit( Integer value )
		{
			queue.submitAsync(queue_id, new StandardMessageOnUpsert(kind,new ObjectId(value)));
		}
	}
	
	static public class SendQueueStatusPrinter implements Sink<Integer>
	{
		private RateLimitingEmitter message_emitter;
		private LowLevelRedisDriver redis;
		private QueueRedis queue;
		
		public SendQueueStatusPrinter(LowLevelRedisDriver redis, RateLimitingEmitter message_emitter, QueueRedis queue)
		{
			this.redis = redis;
			this.message_emitter = message_emitter;
			this.queue = queue;
		}
		
		public void onEmit( Integer count )
		{
			System.out.println(String.format("queue source %d: Redis Up: %b, Queue send rate: %.2f/sec, queue length: %,d", count, redis.isRedisUp(), message_emitter.getRate(), queue.getLength(queue_id,0)));
			
			if ( count.intValue() % 5 == 0 ) 
				message_emitter.setRate(message_emitter.getRate()*2.0f);
		}
	}
	
	static private void testSignalSink(String host, int port)
	{
		System.out.println(String.format("Starting signal sink %s:%d in 2 sec", host, port));
		silentSleep(2000);
		
		LowLevelRedisDriver redis = new LowLevelRedisDriver (host, port);
		ISignal signal = new SignalRedis(new ApplicationId("test"), redis);
		
		MySignalListener listener = new MySignalListener();
		
		signal.startListening(topic_id, listener);

		
		RateLimitingEmitter status_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SinkSignalStatusPrinter(redis, listener), 1.0f);
	}
	
	static private class MySignalListener implements SignalListener
	{
		public int count = 0;

		@Override
		public void onMessageReceived( StandardObject message )
		{
			count++;
		}
	}
	
	static public class SinkSignalStatusPrinter implements Sink<Integer>
	{
		private LowLevelRedisDriver redis;
		private MySignalListener listener;
		
		private int last_count = 0;
		private long last_time = System.currentTimeMillis();
		
		public SinkSignalStatusPrinter(LowLevelRedisDriver redis, MySignalListener listener)
		{
			this.redis = redis;
			this.listener = listener;
		}
		
		public void onEmit( Integer count )
		{
			int processed_in_last_interval = listener.count - last_count;
			long time_in_last_interval = System.currentTimeMillis() - last_time;
			
			float processed_per_second = (float)processed_in_last_interval*1000.0f/(float)time_in_last_interval;
			
			System.out.println(String.format("signal sink %d: Redis Up: %b, Process Rate %.2f/sec", count, redis.isRedisUp(), processed_per_second));
			
			last_count = listener.count;
			last_time = System.currentTimeMillis();
		}
	}
	
	
	
	static private void testQueueSink(String host, int port)
	{
		System.out.println(String.format("Starting queue sink %s:%d in 2 sec", host, port));
		silentSleep(2000);
		
		LowLevelRedisDriver redis = new LowLevelRedisDriver (host, port);
		QueueRedis queue = new QueueRedis(new ApplicationId("test"), redis);
		
		MyQueueListener listener = new MyQueueListener();
		
		queue.startListening(queue_id, listener, 1);
		
		RateLimitingEmitter status_emitter = RateLimitingEmitter.startEmitter(new CountSource(), new SinkQueueStatusPrinter(redis, listener), 1.0f);
	}
	
	static private class MyQueueListener implements QueueListener
	{
		public int count = 0;

		@Override
		public void onMessageReceived( StandardObject message )
		{
			count++;
		}
	}
	
	static public class SinkQueueStatusPrinter implements Sink<Integer>
	{
		private LowLevelRedisDriver redis;
		private MyQueueListener listener;
		
		private int last_count = 0;
		private long last_time = System.currentTimeMillis();
		
		public SinkQueueStatusPrinter(LowLevelRedisDriver redis, MyQueueListener listener)
		{
			this.redis = redis;
			this.listener = listener;
		}
		
		public void onEmit( Integer count )
		{
			
			int processed_in_last_interval = listener.count - last_count;
			long time_in_last_interval = System.currentTimeMillis() - last_time;
			
			float processed_per_second = (float)processed_in_last_interval*1000.0f/(float)time_in_last_interval;
			
			System.out.println(String.format("queue sink %d: Redis Up: %b, Process Rate %.2f/sec", count, redis.isRedisUp(), processed_per_second));
			
			last_count = listener.count;
			last_time = System.currentTimeMillis();
		}
	}
	
	static private void silentSleep(long ms)
	{
		try { Thread.currentThread().sleep(2000); } catch(Exception e) {}
	}
	
	static public void onUsageError(String message)
	{
		System.out.println(message);
		System.out.println();
		onHelp();
		System.out.flush();
		System.exit(1);
	}
	
	static public void onHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "test_messaging", createOptions() );
	}
	
	static public Options createOptions()
	{
		Option help = new Option("help", "print this help message");
		

		Option test   = Option.builder( "test" )
                .hasArg()
                .desc("specify which type of test to run")
                .argName("signal | queue")
                .build();
		
		Option mode   = Option.builder( "mode" )
                .hasArg()
                .desc("specify if the instance is to generate messages or process them")
                .argName("source | sink")
                .build();
		
		Option server   = Option.builder( "server" )
                .hasArg()
                .desc("specify the redis server to use")
                .argName("host:port")
                .build();
		
		Options options = new Options();
		
		options.addOption(help);
		
		options.addOption(test);
		options.addOption(mode);
	
		
		options.addOption(server);
		
		return options;
	}
	
	/*static public void run()
	{
		CloudExecutionEnvironment.startupStubTest(new ApplicationId("test"));
		
		String host = NetUtils.extractHostFromHostPortPair(System.getenv("jimmutable_messaging_server"), "localhost");
		int port = NetUtils.extractPortFromHostPortPair(System.getenv("jimmutable_messaging_server"), LowLevelRedisDriver.DEFAULT_PORT_REDIS);
		
		System.out.println("Starting test of messaging on "+host+":"+port+" in 2 seconds");
		
		try { Thread.currentThread().sleep(2000); } catch(Exception e) {}
		
		signal = new SignalRedis(new ApplicationId("test"), new LowLevelRedisDriver (host, port));
		
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
	*/

	
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
}
