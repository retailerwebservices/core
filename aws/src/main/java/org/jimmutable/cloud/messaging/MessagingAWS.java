package org.jimmutable.cloud.messaging;

import org.jimmutable.core.objects.StandardImmutableObject;

public class MessagingAWS extends Messaging
{

	public MessagingAWS()
	{
		super();
	}

	@Override
	public boolean sendAsync( TopicDefinition topic, StandardImmutableObject message )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startListening( SubscriptionDefinition subscription, MessageListener listener )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendAllAndShutdown()
	{
	
	}

}
