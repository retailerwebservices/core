package org.jimmutable.gcloud.pubsub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.PagedResponseWrappers.ListTopicsPagedResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ListTopicsRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

public class TestApp 
{
	static public void main(String args[]) throws Exception
	{
		String projectId = ServiceOptions.getDefaultProjectId();
		
		// Create a topic...
		
		String topicId = "jim-dev-topic";

		
	    
	    
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
	    
	    // Does a topic exist?
	    if ( false )
	    {
	    		TopicName topic_that_exists = TopicName.create(projectId, topicId);
	    		TopicName topic_that_does_not_exist = TopicName.create(projectId, "some-random-topic");
	    		
	    		System.out.println(topicExists(topic_that_exists, false));
	    		
	    		System.out.println(topicExists(topic_that_does_not_exist, false));
	    }
	    
	    // Send one message
	    {
	    		sendOneMessage(projectId, topicId, "Hello World");
	    		sendOneMessage(projectId, topicId, "Somewhere, over the rainbow...");
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
	
	
	static public boolean topicExists(TopicName topic_name, boolean default_value)
	{
		try
		{
			
			
			TopicAdminClient topicAdminClient = TopicAdminClient.create();
			
			 ListTopicsRequest listTopicsRequest =
				      ListTopicsRequest.newBuilder()
				          .setProjectWithProjectName(ProjectName.create(topic_name.getProject()))
				          .setPageSize(1000)
				          .build();
			
			 // Assumption: No project of ours will ever have more than 1,000 topics...
			 
			 ListTopicsPagedResponse response = topicAdminClient.listTopics(listTopicsRequest);
			 
			  Iterable<Topic> topics = response.iterateAll();
			  
			  for (Topic topic : topics) 
			  {
			    if ( topic.getNameAsTopicName().getTopic().equalsIgnoreCase(topic_name.getTopic()) ) 
			    		return true;
			  }
			  
			  return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return default_value;
		}
		
	}
}
