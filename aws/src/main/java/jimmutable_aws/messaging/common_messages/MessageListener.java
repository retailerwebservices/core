package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.StandardObject;
/**
 * 
 * @author andrew.towe
 * This class exists so that we can listen for incoming messages.
 */
public interface MessageListener
{
	public void onMessageReceived( StandardObject message );
}
