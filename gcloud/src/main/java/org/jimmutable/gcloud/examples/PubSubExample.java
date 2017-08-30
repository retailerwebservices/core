package org.jimmutable.gcloud.examples;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.gcloud.GCloudTypeNameRegister;
import org.jimmutable.gcloud.ProjectId;
import org.jimmutable.gcloud.pubsub.PullSubscriptionDefinition;
import org.jimmutable.gcloud.pubsub.StandardObjectListener;
import org.jimmutable.gcloud.pubsub.StandardObjectPublisher;
import org.jimmutable.gcloud.pubsub.StandardObjectReceiver;
import org.jimmutable.gcloud.pubsub.SubscriptionId;
import org.jimmutable.gcloud.pubsub.TopicId;
import org.jimmutable.gcloud.pubsub.messages.StandardMessageOnUpsert;

/**
 * This example of our pub/sub library sends one StandardMessageOnUpsert message
 * per second while (simultaneously) listening for such messages and printing
 * the results to the console.
 * 
 * @author kanej
 *
 */
public class PubSubExample 
{
	static private final TopicId EXAMPLE_TOPIC = new TopicId("example-send-one-msg-per-sec");
	static private final SubscriptionId EXAMPLE_SUBSCRIPTION = new SubscriptionId("example-send-one-msg-per-sec");
	
	static public void main(String args[])
	{
		
		
		//System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "/Users/trevorbox/eclipse-workspace/maven.1502923603553/gcloud/platform-test-f5374a172bb5.json");
		
		ExampleUtils.setupExample();
		
		PullSubscriptionDefinition def;

		def = new PullSubscriptionDefinition(ProjectId.CURRENT_PROJECT, EXAMPLE_SUBSCRIPTION, ProjectId.CURRENT_PROJECT, EXAMPLE_TOPIC);
	
		StandardObjectReceiver.startListening(def, new SimpleListener());
		
		// Send 20 messages then exit
		new SendOneMessagePerSec(20).run();
	}
	
	/**
	 * This is a simple StandardObjectListener implementation. It will only respond
	 * to StandardMessageOnUpsert messages and it does so simply by printing the
	 * kind and object id out System.out
	 * 
	 * @author kanej
	 *
	 */
	static private class SimpleListener implements StandardObjectListener
	{
		public void onMessageReceived(StandardObject message) 
		{
			if ( message instanceof StandardMessageOnUpsert) 
			{
				StandardMessageOnUpsert upsert_message = (StandardMessageOnUpsert)message;
				
				System.out.printf("Upsert Message Rec %s:%s\n", upsert_message.getSimpleKind(), upsert_message.getSimpleObjectId());
			}
		}
	}
	
	/**
	 * This runnable will send one upsert message per second using StandardObjectPublisher
	 * 
	 * @author kanej
	 *
	 */
	static private class SendOneMessagePerSec implements Runnable
	{
		private long counter = 0;
		private long max_counter = Long.MAX_VALUE;
		
		public SendOneMessagePerSec() {}
		public SendOneMessagePerSec(long max_counter) { this.max_counter = max_counter; }
		
		public void run()
		{
			
			while(true)
			{
				StandardMessageOnUpsert message = new StandardMessageOnUpsert(new Kind("one-per-sec-test"),new ObjectId(++counter));
				
				StandardObjectPublisher.publishObject(EXAMPLE_TOPIC, message);
				
				try { Thread.currentThread().sleep(1000); } catch(Exception e) { e.printStackTrace(); }
				
				if ( counter > max_counter ) break;
			}
			
			boolean shutdown_result = StandardObjectPublisher.shutdown();
			System.out.println("Shutdown publisher result = "+shutdown_result);
		}
	}
}
