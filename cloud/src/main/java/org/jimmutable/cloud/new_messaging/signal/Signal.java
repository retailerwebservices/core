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
/*
 * CODEREVIEW
 * Possibly a nitpick, but this should be ISignal. For the other backplane
 * services, IFoo is the interface, Foo is the abstract root class, and FooBar
 * is the implementation of IFoo using Bar driver. Now, if you want to rename
 * everything to Foo, AbstractFoo, and FooBar, I'm okay with that too (prefer
 * it actually). But we should have a standard one way or the other.
 * -JMD
 */
public interface Signal
{
	/**
	 * Send a message to the specified topic asynchronously (function returns immediately)
	 * 
	 * @param id The topic id to send the message to
	 * @param message The message to send.  A null message does nothing.
	 */
	@SuppressWarnings("rawtypes")
    public void sendAsync(SignalTopicId topic, StandardObject message);
	
	/**
	 * Send a message to the specified topic. Function does not return until the
	 * message has been sent or an error occurs.
	 * 
	 * @param id
	 *            The topic id to send the message to
	 * @param message
	 *            The message to send. A null message does nothing.
	 */
	@SuppressWarnings("rawtypes")
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
