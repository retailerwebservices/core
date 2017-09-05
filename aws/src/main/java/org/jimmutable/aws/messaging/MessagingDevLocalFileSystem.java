package org.jimmutable.aws.messaging;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.storage.ApplicationId;
import org.jimmutable.storage.StorageKeyExtension;

/**
 * 
 * @author andrew.towe
 *
 */
public class MessagingDevLocalFileSystem extends Messaging
{
	File root;

	public MessagingDevLocalFileSystem()
	{
		super();
		if ( !ApplicationId.hasOptionalDevApplicationId() )
		{
			System.err.println("Hey -- you are trying to instantiate a dev local file system. This should not be happening in production. If you are a developer and you are trying to run this through eclipse, you need to setup the environment configurations in your run configurations");
			throw new RuntimeException();
		}
		root = new File(System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(new ApplicationId("Development")) + "/messaging");
		String pathname = root.getAbsolutePath();
		File pfile = new File(pathname);
		pfile.mkdirs();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean sendAsync( TopicDefinition topic, StandardImmutableObject message )
	{
//		if(message.) {
//			
//		}
		if ( topic == null )
		{
			topic = new TopicDefinition("");
		}
		Random rng = new Random();
		ObjectId objectId = new ObjectId(rng.nextLong());

		FileOutputStream fos;

		try
		{
			String pathname = root.getAbsolutePath() + "/" + topic.getSimpleApplicationId().getSimpleValue() + "/" + topic.getSimpleTopicId().getSimpleValue();
			File pfile = new File(pathname);
			pfile.mkdirs();
			File file = new File(pathname + "/" + objectId.getSimpleValue()+"."+StorageKeyExtension.JSON);
			fos = new FileOutputStream(file.getAbsolutePath());
			fos.write(message.toString().getBytes());
			fos.close();
		}
		catch ( Exception e )
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean startListening( SubscriptionDefinition subscription, MessageListener listener )
	{
		
		File subscription_path = new File(root.getAbsolutePath()+"/"+subscription.getSimpleTopicId().getSimpleValue());
		if(!subscription_path.exists()) {
			subscription_path.mkdirs();
		}
		Path mydir = Paths.get(subscription_path.getAbsolutePath());
		try
		{
			WatchService watcher = mydir.getFileSystem().newWatchService();
			mydir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);	//we want to only catch things that are being created. 
			
			WatchKey watckKey = watcher.take();		//this will hang if there are no watchkeys..... But we need to get out of this method so we can set up a watchkey right?
			
			List<WatchEvent<?>> events = watckKey.pollEvents();
			for ( WatchEvent<?> event : events )
			{
				if ( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
				{
//					listener.onMessageReceived(message);
				}
			} 
			return true;
		} 
		catch(NullPointerException e)
		{
			System.out.println("No watchkey found. Therefore we are not listening.");
			return false;
		}
		catch ( Exception e )
		{
			System.out.println("Error: " + e.toString());
			return false;
		}
	}

	@Override
	public void sendAllAndShutdown()
	{
		
	}

}