package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.ProjectId;

import com.google.cloud.pubsub.v1.PagedResponseWrappers.ListSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.PagedResponseWrappers.ListTopicsPagedResponse;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ListSubscriptionsRequest;
import com.google.pubsub.v1.ListTopicsRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

public class PubSubConfigurationUtils 
{
	/**
	 * If a given topic does not exist, create it
	 * 
	 * @param project_id
	 *            The project id
	 * @param topic_id
	 *            The topic id
	 * @return true if the topic existed (or was created), false if an error occours
	 */
	static public boolean createTopicIfNeeded(ProjectId project_id, TopicId topic_id)
	{
		Validator.notNull(project_id, topic_id);
		
		try
		{
			Boolean topic_exists = getComplexTopicExists(project_id, topic_id, null);
			if ( topic_exists == null ) return false;
			if ( topic_exists == true ) return true;
			
			// If here, then we need to create the topic
			
			TopicAdminClient admin_client = TopicAdminClient.create();

			admin_client.createTopic(createTopicName(project_id, topic_id));

			System.out.printf("Topic %s:%s created.\n", project_id, topic_id);


			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Check and see if a given topic exists
	 * 
	 * @param project_id
	 *            The project id of the topic
	 * @param topic_id
	 *            The topic id of the topic
	 * @param default_value
	 *            The value to return in the event of an error
	 * 
	 * @return true if the topic exists, false if the topic does not exist,
	 *         default_value in the event of an error
	 */
	static public Boolean getComplexTopicExists(ProjectId project_id, TopicId topic_id, Boolean default_value)
	{
		Validator.notNull(project_id, topic_id);
		
		try
		{
			TopicAdminClient topicAdminClient = TopicAdminClient.create();

			ListTopicsRequest listTopicsRequest =
					ListTopicsRequest.newBuilder()
					.setProjectWithProjectName(ProjectName.create(project_id.getSimpleValue()))
					.setPageSize(1000)
					.build();

			ListTopicsPagedResponse response = topicAdminClient.listTopics(listTopicsRequest);

			Iterable<Topic> topics = response.getPage().iterateAll();

			for (Topic topic : topics) 
			{
				if ( topic.getNameAsTopicName().getTopic().equalsIgnoreCase(topic_id.getSimpleValue()) ) 
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
	
	static public boolean createSubscriptionIfNeeded(PullSubscriptionDefinition subscription_definition)
	{
		Validator.notNull(subscription_definition);
		
		try 
		{
			createTopicIfNeeded(subscription_definition.getSimpleTopicProjectID(), subscription_definition.getSimpleTopicID());
			
			SubscriptionAdminClient client = SubscriptionAdminClient.create();
			
			ListSubscriptionsRequest listSubscriptionsRequest =
					ListSubscriptionsRequest.newBuilder()
					.setProjectWithProjectName(ProjectName.create(subscription_definition.getSimpleSubscriptionProjectID().getSimpleValue()))
					.setPageSize(1000)
					.build();
			
			ListSubscriptionsPagedResponse response = client.listSubscriptions(listSubscriptionsRequest);
			
			Iterable<Subscription> subscriptions = response.getPage().iterateAll();
			
			for ( Subscription subscription : subscriptions ) 
			{
				String cur_subscription_id = subscription.getNameAsSubscriptionName().getSubscription();
				
				if ( subscription_definition.getSimpleSubscriptionID().getSimpleValue().equals(cur_subscription_id) )
				{
					// Ok, so the subscription specified by the subscription_definition exists, now just make sure it is subscribed to the proper topic...
					
					String cur_topic_project_id = subscription.getTopicAsTopicNameOneof().getTopicName().getProject();
					String cur_topic_id = subscription.getTopicAsTopicNameOneof().getTopicName().getTopic();
					
					if ( subscription_definition.getSimpleTopicProjectID().getSimpleValue().equals(cur_topic_project_id) && 
					     subscription_definition.getSimpleTopicID().getSimpleValue().equals(cur_topic_id)
					     )
					{
						return true;
					}
					
					System.out.println(subscription.getNameAsSubscriptionName()+" was subscribed to the wrong topic, deleting");
					client.deleteSubscription(subscription.getNameAsSubscriptionName());
					
					break;
				}
			}
			
			// Create a new subscription
			Subscription subscription = client.createSubscription(subscription_definition.createSimpleSubscriptionName(), subscription_definition.createSimpleTopicName(), PushConfig.getDefaultInstance(), 0);
    		
    			System.out.println("Created new subscription "+subscription);
    			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Given a ProjectID and a TopicID create a TopicName object
	 * 
	 * @param project_id
	 *            The ProjectID
	 * @param topic_id
	 *            The TopicID
	 * @return A TopicName corresponding to the spedcified project and topic id
	 */
	static public TopicName createTopicName(ProjectId project_id, TopicId topic_id)
	{
		Validator.notNull(project_id, topic_id);
		
		return TopicName.create(project_id.getSimpleValue(), topic_id.getSimpleValue());
	}
	
	
}

