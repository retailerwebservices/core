package org.jimmutable.cloud.messaging;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.StandardImmutableObject;

public abstract class Messaging
{

	static Messaging instance = null;

	protected Messaging()
	{
		// TODO Auto-generated constructor stub
	}

	public static Messaging getSimpleInstance()
	{
		if ( instance == null )
		{
			if ( ApplicationId.hasOptionalDevApplicationId() )
			{
				instance = new MessagingDevLocalFileSystem();
			} else
			{
				instance = new MessagingAWS();
			}
		}
		return instance;
	}
	/**
	 * <b> ANY MESSAGE YOU SEND SHOULD BE SMALL </b>
	 * Preferably 100kb. our maximum is 256kb. 
	 * 
	 * If you are implementing this:
	 * 1) you should account for creating the topic if it does not exist
	 * 2) this function needs to return <b>IMMEDIATELY!</b>
	 * 
	 * @param topic that you want the message to be sent to 
	 * @param message the object you want sent
	 * @return true if sending message succeeded, false otherwise. 
	 */

	abstract public boolean sendAsync( TopicDefinition topic, StandardImmutableObject message );
	
	/**
	 * This method will begin listening for messages
	 * This method will not create any duplicate Listeners
	 * Can be very slow, you should call this method very infrequently
	 * 
	 * If you are Implementing this method, you should be able to handle the creation of the topic and subscription if they are not there. 
	 * 
	 * @param subscription that you want to start listening on
	 * @param listener that you want to be used. 
	 * @return true if the starting of listening, false otherwise 
	 */
	abstract public boolean startListening( SubscriptionDefinition subscription, MessageListener listener );
	
	/**
	 * Used to send all messages before shutting down
	 * this will block any attempt to shutdown until all pending messages are sent. 
	 * this will free any resources that are used to implement the asynchronous sending mechanism
	 * 
	 */
	abstract public void sendAllAndShutdown();
}
