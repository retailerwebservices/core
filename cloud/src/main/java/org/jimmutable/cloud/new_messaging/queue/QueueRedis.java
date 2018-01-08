package org.jimmutable.cloud.new_messaging.queue;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

/**
 * An implementation of queue backed by redis.
 * 
 * Please note that the the Redis driver (cache.redis.Redis) has an extensive
 * unit test (Redis test) that serves as the unit test for this implementation
 * of queueing
 * 
 * @author kanej
 *
 */
public class QueueRedis implements Queue
{
	private LowLevelRedisDriver redis;
	private ApplicationId app;
	
	public QueueRedis(ApplicationId app, LowLevelRedisDriver redis)
	{ 
		Validator.notNull(app,redis);
		this.app = app;
		this.redis = redis;
	}
	
	public void submitAsync( QueueId queue, StandardObject message )
	{
		if ( queue == null || message == null ) return;
		
		redis.queue().submitAsync(app, queue, message);
	}

	@Override
	public void submit( QueueId queue, StandardObject message )
	{
		if ( queue == null || message == null ) return;
		
		redis.queue().submit(app, queue, message);
		
	}

	@Override
	public void startListening( QueueId queue, QueueListener listener, int number_of_worker_threads )
	{
		Validator.notNull(queue, listener);
		Validator.min(number_of_worker_threads, 1);
		
		redis.queue().startListening(app, queue, listener, number_of_worker_threads);
		
	}

}
