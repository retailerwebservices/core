package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.StandardObject;
/**
 * 
 * @author andrew.towe
 * This class exists so that we can listen for incoming messages.
 */

// CODE REVEIW: For interfaces, javadoc would typically read something like this: "Implementing this interface to review messages from Messaging"

public interface MessageListener
{
	// Code review: Javadoc needed on this method.  You need to let the user know (a) that message will never be null (b) that this is not executed in a different thread, so one should not consumer tons of time, etc.
	public void onMessageReceived( StandardObject message );
}
