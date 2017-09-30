package org.jimmutable.cloud.messaging;

import org.jimmutable.core.objects.StandardImmutableObject;

public interface IMessaging
{
	abstract public boolean sendAsync(TopicDefinition topic, StandardImmutableObject<?> message);

	abstract public boolean startListening(SubscriptionDefinition subscription, MessageListener listener);

	abstract public void sendAllAndShutdown();
}
