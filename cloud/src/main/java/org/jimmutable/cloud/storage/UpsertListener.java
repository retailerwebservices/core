package org.jimmutable.cloud.storage;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectReference;

public class UpsertListener implements MessageListener
{
	@Override
	public void onMessageReceived( StandardObject message )
	{
		if ( message instanceof StandardMessageOnUpsert )
		{
			StandardMessageOnUpsert standard_message = (StandardMessageOnUpsert) message;
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleCache().remove(new ObjectReference(standard_message.getSimpleKind(), standard_message.getSimpleObjectId()));
		}
	}

}