package org.jimmutable.cloud.new_messaging.signal;

import org.jimmutable.core.objects.StandardObject;

/**
 * Implementing this interface to recieve messages from signaling
 * 
 * @author jim.kane
 */

	
public interface SignalListener
{
	/**
	 * This is a lightweight method. Any implementation of this method should not need to take a lot of time
	 * Should not be executed on a separate thread. 
	 * 
	 * @param message will never be null;
	 */
	public void onMessageReceived( StandardObject message );

}
