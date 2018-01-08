package org.jimmutable.cloud.new_messaging.signal;

import org.jimmutable.core.objects.StandardObject;

/**
 * Signaling is a way to broadcast messages with "fair weather" delivery -- a
 * best effort is made to get all messages to all listeners, but delivery,
 * order, etc. are not guaranteed.
 * 
 * In signaling, when a message is sent to a topic, a copy of the message is
 * delivered to *each* listener. As a result, signaling tends to be used to
 * lightweight "heads up" type messaging between application instances. The most
 * common of these are cache control messages (e.g. hey everyone, object ID 1234
 * has been updated, clear any local cache you may have of it)
 * 
 * @author kanej
 *
 */
public interface Signal
{
	/**
	 * Send a message to the specified topic asynchronously (function returns immediately)
	 * 
	 * @param id The topic id to send the message to
	 * @param message The message to send.  A null message does nothing.
	 */
	public void sendAsync(SignalTopicId topic, StandardObject message);
	
	/**
	 * Send a message to the specified topic. Function does not return until the
	 * message has been sent or an error occours.
	 * 
	 * @param id
	 *            The topic id to send the message to
	 * @param message
	 *            The message to send. A null message does nothing.
	 */
	public void send(SignalTopicId topic, StandardObject message);
	
	/**
	 * Start listening for messages on a specified topic
	 * 
	 * @param id
	 *            The topic to start listening to
	 * @param listener
	 *            The listener that will process messages
	 */
	public void startListening(SignalTopicId topic, SignalListener listener);
}
