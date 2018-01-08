package org.jimmutable.cloud.new_messaging.queue;

import org.jimmutable.core.objects.StandardObject;

/**
 * Implement this interface to receive messages from a queue
 * 
 * @author jim.kane
 */


public interface QueueListener
{
	/**
	 * This method performs processing on the message. Unlike with signaling it *is*
	 * acceptable to perform heavy weight processing on this thread (that is, after
	 * all, the point of queues)
	 * 
	 * @param message
	 *            will never be null;
	 */
	public void onMessageReceived( StandardObject message );
}
