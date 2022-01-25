package org.jimmutable.cloud.messaging.queue;

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
public class QueueRedis implements IQueue
{
	private LowLevelRedisDriver redis;
	private ApplicationId app;

	public QueueRedis( ApplicationId app, LowLevelRedisDriver redis )
	{
		Validator.notNull(app, redis);
		this.app = app;
		this.redis = redis;
	}

	public QueueRedis( ApplicationId app )
	{
		this(app, new LowLevelRedisDriver());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void submitAsync( QueueId queue, StandardObject message )
	{
		if ( queue == null || message == null )
			return;

		redis.getSimpleQueue().submitAsync(app, queue, message);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean submit( QueueId queue, StandardObject message )
	{
		if ( queue == null || message == null )
			return false;

		return redis.getSimpleQueue().submit(app, queue, message);

	}

	@Override
	public void startListening( QueueId queue, QueueListener listener, int number_of_worker_threads )
	{
		Validator.notNull(queue, listener);
		Validator.min(number_of_worker_threads, 1);

		redis.getSimpleQueue().startListening(app, queue, listener, number_of_worker_threads);

	}

	public int getLength( QueueId queue_id, int default_value )
	{
		return redis.getSimpleQueue().getQueueLength(app, queue_id, default_value);
	}

	public void clearLowLevelRedisDriver( QueueId queue_id, int default_value )
	{
		redis.getSimpleQueue().clear(app, queue_id);
	}

}
