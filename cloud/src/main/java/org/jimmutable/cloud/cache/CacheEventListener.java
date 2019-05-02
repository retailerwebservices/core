package org.jimmutable.cloud.cache;

import org.jimmutable.cloud.messaging.signal.SignalListener;
import org.jimmutable.core.objects.StandardObject;

public class CacheEventListener implements SignalListener
{

	@Override
	public void onMessageReceived( StandardObject message )
	{
		// @CR - One small thing. It would be a little bit safer to add an instanceof check before casting (like in Log4JOneLevelChangeListener). -PM
		CacheEvent event = (CacheEvent) message;
		CacheEventUtils.log(event);
	}

}
