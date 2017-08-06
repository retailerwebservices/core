package org.jimmutable.gcloud.pubsub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jimmutable.gcloud.ProjectID;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.PagedResponseWrappers.ListSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.PagedResponseWrappers.ListTopicsPagedResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ListSubscriptionsRequest;
import com.google.pubsub.v1.ListTopicsRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

public class TestApp 
{
	static public void main(String args[]) throws Exception
	{
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
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectID(projectId), new TopicID("jim-dev-topic"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectID(projectId), new TopicID("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectID(projectId), new TopicID("jim-dev-topic-3"));
	    		PubSubConfigurationUtils.createTopicIfNeeded(new ProjectID(projectId), new TopicID("jim-dev-topic-4"));
	    }
	    
	    
	    if ( true )
	    {
	    		PullSubscriptionDefinition def;

	    		def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub"), new ProjectID(projectId), new TopicID("jim-dev-topic"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		/*def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub"), new ProjectID(projectId), new TopicID("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub"), new ProjectID(projectId), new TopicID("jim-dev-topic-2"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);
	    		
	    		def = new PullSubscriptionDefinition(new ProjectID(projectId), new SubscriptionID("jim-dev-sub2"), new ProjectID(projectId), new TopicID("jim-dev-topic-3"));
	    		PubSubConfigurationUtils.createSubscriptionIfNeeded(def);*/
	    }

	    
	    
	    // Send one message
	    if ( false )
	    {
	    		sendOneMessage(projectId, topicId, "Hello World");
	    		sendOneMessage(projectId, topicId, "Somewhere, over the rainbow...");
	    }
	    
	    // Create a subscription
	    if ( false )
	    {
	    	try 
	    	{
	    		SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
	    				
	    		TopicName topicName = TopicName.create(projectId, topicId);
	    		
	    		// eg. subscriptionId = "my-test-subscription"
	    		SubscriptionName subscriptionName = SubscriptionName.create(projectId, subscriptionId);
	    		
	    		// create a pull subscription with default acknowledgement deadline
	    		Subscription subscription = subscriptionAdminClient.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
	    		
	    		System.out.println("Created new subscription "+subscription);
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	    
	   
	}
	
	
	
	static private void sendOneMessage(String projectId, String topicId, String message_str) throws Exception
	{
		TopicName topicName = TopicName.create(projectId, topicId);
		
		Publisher publisher = null;
		
		List<ApiFuture<String>> messageIdFutures = new ArrayList();

		try 
		{
			// Create a publisher instance with default settings bound to the topic
			publisher = Publisher.defaultBuilder(topicName).build();

			List<String> messages = Arrays.asList(message_str);

			// schedule publishing one message at a time : messages get automatically batched
			for (String message : messages) 
			{
				ByteString data = ByteString.copyFromUtf8(message);
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

				// Once published, returns a server-assigned message id (unique within the topic)
				ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
				messageIdFutures.add(messageIdFuture);
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			// wait on any pending publish requests.
			List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();

			for (String messageId : messageIds) 
			{
				System.out.println("published with message ID: " + messageId);
			}

			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				publisher.shutdown();
			}
		}
	}
}
