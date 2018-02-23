package org.jimmutable.cloud.messaging.signal;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

/**
 * An implementation of signal backed by redis
 * 
 * Please note that the the Redis driver (cache.redis.Redis) has an extensive
 * unit test (Redis test) that serves as the unit test for this implementation
 * of signaling
 * 
 * @author kanej
 *
 */
public class SignalRedis implements ISignal
{
	private LowLevelRedisDriver redis;
	private ApplicationId app;
	
	public SignalRedis(ApplicationId app, LowLevelRedisDriver redis)
	{ 
		Validator.notNull(app,redis);
		this.app = app;
		this.redis = redis;
	}

	public SignalRedis(ApplicationId app)
	{ 
		this (app, new LowLevelRedisDriver(LowLevelRedisDriver.DEFAULT_HOST, LowLevelRedisDriver.DEFAULT_PORT_REDIS));
	}

    @Override
	@SuppressWarnings("rawtypes")
	public void sendAsync( SignalTopicId topic, StandardObject message )
	{
		if ( topic == null || message == null ) return;
		redis.getSimpleSignal().sendAsync(app, topic, message);
	}

    @Override
	@SuppressWarnings("rawtypes")
	public void send( SignalTopicId topic, StandardObject message )
	{
		if ( topic == null || message == null ) return;
		redis.getSimpleSignal().send(app, topic, message);
	}

	@Override
	public void startListening( SignalTopicId topic, SignalListener listener )
	{
		Validator.notNull(topic, listener);
		
		redis.getSimpleSignal().startListening(app, topic, listener);
	}
	
	
}
