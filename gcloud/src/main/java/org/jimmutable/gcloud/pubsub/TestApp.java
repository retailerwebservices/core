package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.gcloud.GCloudTypeNameRegister;
import org.jimmutable.gcloud.ProjectId;
import org.jimmutable.gcloud.pubsub.messages.StandardMessageOnUpsert;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.TopicName;

public class TestApp 
{
	static public void main(String args[]) throws Exception
	{
		GCloudTypeNameRegister.registerAllTypes();
		
		String projectId = ServiceOptions.getDefaultProjectId();
		
		// Create a topic...
		
		String topicId = "jim-dev-topic";
		String subscriptionId = "jim-dev-sub";

		
	    // Create a new topic
	    if ( false )
		{
	    		TopicName topic = TopicName.create(projectId, topicId);
	    		
		    try 
		    {
		    		TopicAdminClient admin_client = TopicAdminClient.create();
		    		
		    		admin_client.createTopic(topic);
		    		
		    		System.out.printf("Topic %s:%s created.\n", topic.getProject(), topic.getTopic());
		    }
		    catch(Exception e)
		    {
		      e.printStackTrace();
		    }
	    }
	    
	    if ( false )
	    {
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectId(projectId), new TopicId("jim-dev-topic"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectId(projectId), new TopicId("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectId(projectId), new TopicId("jim-dev-topic-3"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectId(projectId), new TopicId("jim-dev-topic-4"));
	    }
	    
	    if ( true )
	    {
	    		PullSubscriptionDefinition def;

	    		def = new PullSubscriptionDefinition(ProjectId.CURRENT_PROJECT, new SubscriptionId("jim-dev-sub"), ProjectId.CURRENT_PROJECT, new TopicId("jim-dev-topic"));
	    	
	    		StandardObjectReceiver.startListening(def, new TestReceiver());
	    		
	    		new SendOneMessagePerSec().run();
	    }
	    
	    
	    if ( false )
	    {
	    		PullSubscriptionDefinition def;

	    		def = new PullSubscriptionDefinition(new ProjectId(projectId), new SubscriptionId("jim-dev-sub"), new ProjectId(projectId), new TopicId("jim-dev-topic"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		/*def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub"), new ProjectID(projectId), new TopicID("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub"), new ProjectID(projectId), new TopicID("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub2"), new ProjectID(projectId), new TopicID("jim-dev-topic-3"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);*/
	    }
	}
	
	
	static private class SendOneMessagePerSec implements Runnable
	{
		long counter = 0;
		
		public void run()
		{
			TopicId topic_id = new TopicId("jim-dev-topic"); 
			
			while(true)
			{
				StandardMessageOnUpsert message = new StandardMessageOnUpsert(new Kind("one-per-sec-test"),new ObjectId(++counter));
				
				StandardObjectPublisher.publishObject(topic_id, message);
				
				try { Thread.currentThread().sleep(1000); } catch(Exception e) { e.printStackTrace(); }
			}
		}
	}
	
	static private class TestReceiver implements StandardObjectListener
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
}

