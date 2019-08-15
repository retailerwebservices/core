package org.jimmutable.cloud.messaging.queue;

import org.jimmutable.core.objects.StandardObject;

public class QueueStub implements IQueue
{
	private static final String ERROR_MESSAGE = "This should have never been called for unit testing, use a different implementation for integration testing!";

	@Override
	public void submitAsync(QueueId queue, StandardObject message) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public boolean submit(QueueId queue, StandardObject message) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void startListening(QueueId queue, QueueListener listener, int number_of_worker_threads) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public int getLength( QueueId queue_id, int default_value )
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

}
