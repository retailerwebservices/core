package org.jimmutable.cloud.new_messaging.signal;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.redis.Redis;
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
public class SignalRedis implements Signal
{
	private Redis redis;
	private ApplicationId app;
	
	public SignalRedis(ApplicationId app, Redis redis)
	{ 
		Validator.notNull(app,redis);
		this.app = app;
		this.redis = redis;
	}

	@Override
	public void sendAsync( SignalTopicId topic, StandardObject message )
	{
		if ( topic == null || message == null ) return;
		redis.signal().sendAsync(app, topic, message);
	}

	@Override
	public void send( SignalTopicId topic, StandardObject message )
	{
		if ( topic == null || message == null ) return;
		redis.signal().send(app, topic, message);
	}

	@Override
	public void startListening( SignalTopicId topic, SignalListener listener )
	{
		Validator.notNull(topic, listener);
		
		redis.signal().startListening(app, topic, listener);
	}
	
	
}
