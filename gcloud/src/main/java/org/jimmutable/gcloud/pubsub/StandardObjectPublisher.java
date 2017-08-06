package org.jimmutable.gcloud.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.gcloud.ProjectID;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

public class StandardObjectPublisher 
{
	static private Map<TopicID, Publisher> my_publishers = new ConcurrentHashMap();
	
	static public boolean publishObject(TopicID topic, StandardObject object)
	{
		if ( topic == null ) return false; // can't send anything to a null topic
		if ( object == null ) return false; // can't send a null object
		
		if ( !my_publishers.containsKey(topic) )
		{
			ensureTopicSetup(topic);
		}
		
		try
		{
			Publisher publisher = my_publishers.get(topic);
			if ( publisher == null ) return false; // A serious error has occurred, unable to send the message
			
			String message = object.serialize(Format.JSON);
			
			ByteString data = ByteString.copyFromUtf8(message);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
			publisher.publish(pubsubMessage);
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	synchronized static public boolean ensureTopicSetup(TopicID topic_id)
	{
		if ( topic_id == null ) return false; // can't setup a  null topic
		if ( my_publishers.containsKey(topic_id) ) return true; // topic is already setup and ready to go
		
		boolean topic_ready = PubSubConfigurationUtils.createTopicIfNeeded(ProjectID.CURRENT_PROJECT, topic_id);
		if ( !topic_ready ) return false;
		
		TopicName topic_name = PubSubConfigurationUtils.createTopicName(ProjectID.CURRENT_PROJECT, topic_id);
				
		try
		{
			Publisher publisher = Publisher.defaultBuilder(topic_name).build();
			my_publishers.put(topic_id, publisher);
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try { Thread.currentThread().sleep(1000); } catch(Exception e2) {} // the 1 second delay is to give the google cloud some breathing room before we try again to setup the publisher
			return false;
		}
	}
}
 