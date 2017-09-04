package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.StandardImmutableObject;

public class MessagingDevLocalFileSystem extends Messaging
{

	public MessagingDevLocalFileSystem()
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
