package org.jimmutable.cloud.cache;

import org.jimmutable.cloud.messaging.signal.SignalListener;
import org.jimmutable.core.objects.StandardObject;

public class CacheEventListener implements SignalListener
{

	@Override
	public void onMessageReceived( StandardObject message )
	{
		CacheEvent event = (CacheEvent) message;
		CacheEventUtils.log(event);
	}

}
