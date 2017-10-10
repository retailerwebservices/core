package org.jimmutable.cloud.messaging.dev_local;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.cloud.messaging.QueueId;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.objects.Stringable;

/**
 * Convenience methods that map topics and queues to directories on disk.
 * 
 * The path structure is ~/jimmutable_aws_dev/messaging/[topic def]/[queue def]
 * 
 * The path structure is ~/jimmutable_aws_dev/messaging/topic_application_id/topic_id/queue_application_id/queue_id
 * 
 * @author kanej
 *
 */
public class FileSystem
{
	private static final Logger logger = Logger.getLogger(FileSystem.class.getName()); 
	
	private File root;
	
	/**
	 * Create a file system using the default root directory. Equivalent to new
	 * FileSystem(getDefaultRootDir());
	 */
	public FileSystem()
	{
		this(getDefaultRootDir());
	}
	
	
	public FileSystem(File root)
	{
		this.root = root;
		root.mkdirs();
	}
	
	/**
	 * Get the root directory of the messaging "dev local" file system (~/jimmutable_aws_dev/messaging)
	 * 
	 * This method will create the path, if it does not already exist
	 * 
	 * @return The root directory of the messaging "dev local" file system
	 */
	static public File getDefaultRootDir()
	{
		return new File(new File(System.getProperty("user.home"), "jimmutable_aws_dev"), "messaging");
	}
	
	/**
	 * Get the directory (File object) that is used to store messages sent to a specified description.
	 * 
	 * ../topic_application_id/topic_id/queue_application_id/queue_id
	 * 
	 * @param definition The subscription definition 
	 * @return The directory that contains messages associated with the specified subscription definition
	 */
	public File getDirSubscription(SubscriptionDefinition definition)
	{
		File ret = getDirTopic(definition.getSimpleTopicDefinition());
		
		QueueDefinition queue = definition.getSimpleQueueDefinition();
		
		ret = new File(ret, queue.getSimpleApplicationId().toString());
		ret = new File(ret, queue.getSimpleQueueId().toString());
		
		
		
		return ret;
	}
	
	/**
	 * Given a topic definition, get the directory (File object) that corresponds to this topic on disk
	 * 
	 * @param topic The topic definition
	 * @return the directory (File object) that corresponds to this topic on disk
	 * 
	 */
	public File getDirTopic(TopicDefinition topic)
	{
		File ret = root;
		
		ret = new File(ret, topic.getSimpleApplicationId().toString());
		ret = new File(ret, topic.getSimpleTopicId().toString());
		
		return ret;
	}
	
	/**
	 * Given a subscription definition, create the directory (if needed) for the definition's messages
	 * 
	 * @param definition The subscription definition
	 */
	public void createSubscriptionIfNeeded(SubscriptionDefinition definition)
	{
		if ( definition == null ) return;
		
		File dir = getDirSubscription(definition);
		
		if ( !dir.exists() )
			dir.mkdirs();
	}
	
	/**
	 * List all topic definitions
	 * 
	 * @return A set of all topic TopicDefinition defined in the current root directory
	 */
	public Set<TopicDefinition> listAllTopics()
	{
		Set<ApplicationId> all_application_ids = listDirectoriesAsStringables(root, new ApplicationId.MyConverter());
		
		Set<TopicDefinition> ret = new HashSet();
		
		for ( ApplicationId app_id : all_application_ids )
		{
			File application_dir = new File(root, app_id.toString());
			
			Set<TopicId> topic_ids = listDirectoriesAsStringables(application_dir, new TopicId.MyConverter());
			
			for ( TopicId topic_id : topic_ids )
			{
				ret.add(new TopicDefinition(app_id,topic_id));
			}
		}
		
		return ret;
	}
	
	/**
	 * Given a topic, list all subscriptions (defined on disk)
	 * 
	 * @param topic
	 *            The topic
	 * @return A set of subscriptions associated with the topic
	 */
	public Set<SubscriptionDefinition> listAllSubscriptions(TopicDefinition topic)
	{
		File topic_dir = getDirTopic(topic);
		
		Set<ApplicationId> all_application_ids = listDirectoriesAsStringables(topic_dir, new ApplicationId.MyConverter());
		
		Set<SubscriptionDefinition> ret = new HashSet();
		
		for ( ApplicationId app_id : all_application_ids )
		{
			File application_dir = new File(topic_dir, app_id.toString());
			
			Set<QueueId> queue_ids = listDirectoriesAsStringables(application_dir, new QueueId.MyConverter());
			
			for ( QueueId queue_id : queue_ids )
			{
				ret.add(new SubscriptionDefinition(topic, new QueueDefinition(app_id, queue_id)));
			}
		}
		
		return ret;
	}
	
	/**
	 * List all subscriptions defined in the root messaging folder
	 * 
	 * @return A set of all SubscriptionDefinitions in the root messaging folder
	 */
	public Set<SubscriptionDefinition> listAllSubscriptions()
	{
		Set<TopicDefinition> topics = listAllTopics();
		
		Set<SubscriptionDefinition> ret = new HashSet();
		
		for ( TopicDefinition topic : topics )
		{
			ret.addAll(listAllSubscriptions(topic));
		}
		
		return ret;
	}
	 
	
	/**
	 * Given a root directory and a Stringable converter, return a set of all
	 * directories within the specified root directory that are (a) not hidden and
	 * (b) are instances of the specified Stringable
	 * 
	 * @param dir
	 *            The root directory
	 * @param converter
	 *            The converter for the Stringable you would like returned
	 * @return The set of Stringable objects contained in the specified root
	 *         directory
	 */
	static private <T extends Stringable> Set<T> listDirectoriesAsStringables(File dir, Stringable.Converter<T> converter)
	{
		Set<T> ret = new HashSet();
		
		if ( dir == null || !dir.isDirectory() ) return ret;
		
		File dir_contents[] = dir.listFiles();
		
		if ( dir_contents == null ) return ret;
		
		for ( File file : dir_contents )
		{
			if ( !file.isDirectory() ) continue;
			if ( file.isHidden() ) continue;
			
			T cur = converter.fromString(file.getName(), null);
			if ( cur == null ) continue;
			
			ret.add(cur);
		}
		
		return ret;
	}
	
	/**
	 * Given JSON file, return the subscription definition that the file is
	 * associated with
	 * 
	 * @param src
	 *            The json file
	 * @param default_value
	 *            The value to return if a subscription can't be extracted
	 * @return The subscription definition associated with the message, or
	 *         default_value if one can not be extracted.
	 */
	public SubscriptionDefinition getSubscriptionDefinitionFromFile(File src, SubscriptionDefinition default_value)
	{
		try
		{
			if ( !src.exists() ) return default_value;
			if ( src.isDirectory() ) return default_value;
			if ( src.isHidden() ) return default_value;
			
			if ( !src.getName().toLowerCase().endsWith(".json") ) return default_value;
			
			src = src.getParentFile();
			QueueId queue_id = new QueueId(src.getName());
			
			src = src.getParentFile();
			ApplicationId queue_app_id = new ApplicationId(src.getName());
			
			src = src.getParentFile();
			TopicId topic_id = new TopicId(src.getName());
			
			src = src.getParentFile();
			ApplicationId topic_app_id = new ApplicationId(src.getName());
			
			return new SubscriptionDefinition(new TopicDefinition(topic_app_id, topic_id), new QueueDefinition(queue_app_id, queue_id));
		}
		catch(Exception e)
		{
			
			return default_value;
		}
		
	}
	
	public File getSimpleRoot()
	{
		return root;
	}
	
}

