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
	 * need to take a lot of time (signals processing happens in a small thread
	 * listening pool). If your instance falls behind in signal processing messages
	 * may be lost
	 * 
	 * @param message
	 *            will never be null;
	 */
    /*
     * CODEREVIEW
     * Does a common cache.deleteAsync count as not "a lot of time"? If that's the most
     * common use for signaling, then that use case should be supported. Otherwise,
     * every client will require it's own thread pool etc. to process messages, and in
     * that case, I'd rather bake it into the library so it's done right.
     * -JMD
     */
	@SuppressWarnings("rawtypes")
    public void onMessageReceived( StandardObject message );

}
