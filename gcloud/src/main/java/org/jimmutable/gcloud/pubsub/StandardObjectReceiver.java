package org.jimmutable.gcloud.pubsub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;

/**
 * Easy to use class that allows one to listen for messages on a given topic.
 * The messages are presumed to be serialized StandardObject(s) (JSON or XML).
 * Messages that can not be de-serialized are discarded.
 * 
 * @author kanej
 *
 */
public class StandardObjectReceiver 
{
	static private Map<SubscriptionName, MyReceiver> receivers = new ConcurrentHashMap();

	/**
	 * Begin listing on a specified subscription using a specified listener
	 * 
	 * @param def
	 *            The definition of the subscription to listen to. This function
	 *            will create or reconfigure the subscription (as needed) to match
	 *            the definition supplied
	 * 
	 * @param listener
	 *            The listener that should receive message notifications
	 * 
	 * @return True if listening has begin, false otherwise.
	 */
	synchronized static public boolean startListening(PullSubscriptionDefinition def, StandardObjectListener listener)
	{
		Validator.notNull(def, listener);
		
		SubscriptionName subscription_name = def.createSimpleSubscriptionName();
		
		// Are we already listening to this subscription?
		if ( receivers.containsKey(subscription_name) )
		{
			receivers.get(subscription_name).addListener(listener);
			return true;
		}
		
		try
		{
			MyReceiver rec = new MyReceiver(listener);
			
			PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
			
			Subscriber subscriber = Subscriber.defaultBuilder(subscription_name, rec).build();
			subscriber.startAsync();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	static private class MyReceiver implements MessageReceiver
	{
		private List<StandardObjectListener> listeners = new CopyOnWriteArrayList();
		
		public MyReceiver()
		{
			
		}
		
		public MyReceiver(StandardObjectListener listener)
		{
			addListener(listener);
		}
		
		public void addListener(StandardObjectListener listener)
		{
			if ( listener == null ) return;
			if ( listeners.contains(listener) ) return;
			
			listeners.add(listener);
		}
		
		public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) 
		{
			consumer.ack();
			
			String data = message.getData().toStringUtf8();
			
			try
			{
				StandardObject obj = StandardObject.deserialize(data);
				
				for ( StandardObjectListener listener : listeners )
					listener.onMessageReceived(obj);
			}
			catch(Exception e)
			{
				System.out.println("Unable to read message");
				System.out.println(data);
				e.printStackTrace();
				return;
			}
		}
	}
	
	
}
