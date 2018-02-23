package org.jimmutable.cloud.messaging.signal;

import org.jimmutable.core.objects.StandardObject;

public class SignalStub implements ISignal
{
	private static final String ERROR_MESSAGE = "This should have never been called for unit testing, use a different implementation for integration testing!";

	@Override
	public void sendAsync(SignalTopicId topic, StandardObject message) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void send(SignalTopicId topic, StandardObject message) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	@Override
	public void startListening(SignalTopicId topic, SignalListener listener) {
		throw new RuntimeException(ERROR_MESSAGE);
	}

}
