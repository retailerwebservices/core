package org.jimmutable.cloud.messaging;

import org.jimmutable.core.objects.StandardObject;
/**
 * Implementing this interface to review messages from Messaging 
 * @author andrew.towe
 */

public interface MessageListener
{
	/**
	 * This is a lightweight method. Any implementation of this method should not need to take a lot of time
	 * Should not be executed on a separate thread. 
	 * @param message will never be null;
	 */
	public void onMessageReceived( StandardObject message );
}
