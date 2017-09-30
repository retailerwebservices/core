package org.jimmutable.cloud.messaging;

import org.jimmutable.core.objects.StandardImmutableObject;

public class StubMessaging extends Messaging
{

	@Override
	public boolean sendAsync(TopicDefinition topic, StandardImmutableObject<?> message)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean startListening(SubscriptionDefinition subscription, MessageListener listener)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public void sendAllAndShutdown()
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");

	}

}
