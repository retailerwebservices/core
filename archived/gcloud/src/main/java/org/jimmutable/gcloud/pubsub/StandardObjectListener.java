package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.objects.StandardObject;

/**
 * Listener used by StandardObjectReceiver as a target for messages
 * 
 * @author kanej
 *
 */
public interface StandardObjectListener 
{
	/**
	 * Called each time a message is received
	 * 
	 * @param message
	 * 
	 */
	public void onMessageReceived(StandardObject message);
}
