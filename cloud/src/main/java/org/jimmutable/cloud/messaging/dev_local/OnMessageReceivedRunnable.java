package org.jimmutable.cloud.messaging.dev_local;

import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

/**
 * A runnable that invokes a specified listener with a specified message
 * 
 * @author kanej
 *
 */
public class OnMessageReceivedRunnable implements Runnable
{
	private StandardObject message; 
	private MessageListener listener;
	
	public OnMessageReceivedRunnable(MessageListener listener, StandardObject message)
	{
		Validator.notNull(listener, message);
		this.message = message;
		this.listener = listener;
	}

	public void run()
	{
		listener.onMessageReceived(message);
	}
}
