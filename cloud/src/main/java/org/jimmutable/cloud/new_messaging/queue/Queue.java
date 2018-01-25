package org.jimmutable.cloud.new_messaging.queue;

import org.jimmutable.core.objects.StandardObject;

/**
 * Queuing attempts to provide roughly first in first out "fair weather"
 * delivery to one (and only one) subscriber.
 * 
 * Delivery is not guaranteed to be precisely first in first out, but will be
 * approximately first in first out.
 * 
 * Delivery is not guaranteed. When a queue exceeds approximately 10,000
 * messages, excess messages will be thrown away (oldest messages first). Also,
 * under certain extreme circumstances (crashes etc.) messages may be lost.
 * 
 * If a message is delivered, it is, however, guaranteed to be delivered to
 * *only one* subscriber. There is no way for the same message to be delivered
 * to two different subscribers.
 * 
 * Queuing is widely used to distribute work among a pool of processors. If n
 * subscribers listen for messages on a queue, each subscribe can expect to
 * receive approximately 1/n of the messages
 * 
 * @author kanej
 */
/*
 * CODEREVIEW
 * Possibly a nitpick, but this should be IQueue. For the other backplane
 * services, IFoo is the interface, Foo is the abstract root class, and FooBar
 * is the implementation of IFoo using Bar driver. Now, if you want to rename
 * everything to Foo, AbstractFoo, and FooBar, I'm okay with that too (prefer
 * it actually). But we should have a standard one way or the other.
 * -JMD
 */
public interface Queue
{
	/**
	 * Asynchronously submit a message to the specified queue. The function returns
	 * immediately in all cases (error or success)
	 * 
	 * @param id
	 *            The topic id to send the message to
	 * @param message
	 *            The message to send. A null message does nothing.
	 */
	@SuppressWarnings("rawtypes")
    public void submitAsync(QueueId queue, StandardObject message);
	
	/**
	 * Submit a message to the specified queue. Function does not return until the
	 * message has been sent or an error occurs.
	 * 
	 * @param id
	 *            The topic id to send the message to
	 * @param message
	 *            The message to send. A null message does nothing.
	 */
	@SuppressWarnings("rawtypes")
	// CODEREVIEW What happens if an error occurs? How is that reported to the client? -JMD
    public void submit(QueueId queue, StandardObject message);
	
	/**
	 * Start listening for messages on a specified queue
	 * 
	 * @param id
	 *            The queue to start listening to
	 * @param listener
	 *            The listener that will process messages
	 * @param number_of_worker_threads
	 *            The number of worker threads to process messages with
	 */
	/*
     * CODEREVIEW
     * So Queue will manage the underlying thread pool?
     * If so, that's fine, but it should be explicit.
     * -JMD
	 */
	public void startListening(QueueId queue, QueueListener listener, int number_of_worker_threads);
}