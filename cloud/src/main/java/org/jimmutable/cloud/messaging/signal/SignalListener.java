package org.jimmutable.cloud.messaging.signal;

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
	 * need to take a lot of time.
	 * 
	 * Unlike Queue, with Signal you cannot control the
	 * rate that messages come in. Also, there will likely be a LOT of messages
	 * (because they're lightweight). So, if you try to do something complicated with
	 * a signal, your application won't just lose messages, it will choke to death.
	 * 
	 * @param message
	 *            will never be null;
	 */
	@SuppressWarnings("rawtypes")
    public void onMessageReceived( StandardObject message );

}
