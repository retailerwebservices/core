package org.jimmutable.cloud.messaging.queue;

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
public interface IQueue
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
	public void submitAsync( QueueId queue, StandardObject message );

	/**
	 * Submit a message to the specified queue. Function does not return until the
	 * message has been sent or an error occurs.
	 * 
	 * @param id
	 *            The topic id to send the message to
	 * @param message
	 *            The message to send. A null message does nothing.
	 * 
	 * @return True if the message was submitted without error, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	public boolean submit( QueueId queue, StandardObject message );

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
	 * CODEREVIEW So Queue will manage the underlying thread pool? If so, that's
	 * fine, but it should be explicit. -JMD
	 */
	public void startListening( QueueId queue, QueueListener listener, int number_of_worker_threads );

	public int getLength( QueueId queue_id, int default_value );

	/**
	 * ONLY TO BE USED IN EMERGENCIES This method exists in the case that we need to
	 * clear the redis queue because it is hung up.
	 * 
	 * @param queue_id
	 * @param default_value
	 */
	public void clearLowLevelRedisDriver( QueueId queue_id );
}