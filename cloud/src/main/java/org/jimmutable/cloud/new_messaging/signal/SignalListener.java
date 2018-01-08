package org.jimmutable.cloud.new_messaging.signal;

import org.jimmutable.core.objects.StandardObject;

/**
 * Implement this interface to receive messages from signaling
 * 
 * @author jim.kane
 */

	
public interface SignalListener
{
	/**
	 * This is a lightweight method. Any implementation of this method should not
	 * need to take a lot of time (signals processing happens in a 10 thread
	 * listening pool). If your instance falls behind in signal processing messages
	 * may be lost
	 * 
	 * @param message
	 *            will never be null;
	 */
	public void onMessageReceived( StandardObject message );

}
