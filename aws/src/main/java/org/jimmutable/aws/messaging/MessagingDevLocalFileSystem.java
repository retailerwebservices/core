package org.jimmutable.aws.messaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.storage.ApplicationId;
import org.jimmutable.storage.StorageKeyExtension;

/**
 * This is our local implementation of Messaging. It is designed so that a
 * person can run our messaging services without having to rely on AWS or
 * Google. Messages created are stored as .json files on the local machine, in:
 * [home directory]/jimmutable_aws_messaging/[ApplicationId]/messaging This
 * class has a Single thread executor to handle the sending of messages. It is
 * created on creation of this class and can be shutdown by running the
 * SendAllAndShutdown method.
 * 
 * @author andrew.towe
 *
 */
public class MessagingDevLocalFileSystem extends Messaging
{
	File root;
	final ExecutorService executor_service = Executors.newSingleThreadExecutor();

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

	/**
	 * @param topic
	 * 		the topic that you want the message you want the message to go to
	 * @param message
	 * 		the message you want to send
	 * @return 
	 * 		true if the thread was created, false otherwise.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean sendAsync( TopicDefinition topic, StandardImmutableObject message )
	{
		if ( message == null )
		{
			System.out.println("message is null");
			return false;
		}

		// need to ask about message size?

		executor_service.submit(new Thread(new SendMessageRunnable(topic, message)));

		return true;
	}
	
	/**
	 * @param subscription
	 * 		the subscription you want to listen to
	 * @param listener
	 * 		the listener you want to handle the message
	 * @return 
	 * 		true if the threads were created, false otherwise.
	 */
	@Override
	public boolean startListening( SubscriptionDefinition subscription, MessageListener listener )
	{
		File subscription_path = new File(root.getAbsolutePath() + "/" + subscription.getSimpleTopicId().getSimpleValue());
		if ( !subscription_path.exists() )
		{
			subscription_path.mkdirs();
		}
		for ( File dir : subscription_path.listFiles() )
		{
			for ( File sub_dir : dir.listFiles() )
			{
				new Thread(new ListenForMessageRunnable(sub_dir, listener)).start();
			}
		}
		return true;
	}

	/**
	 * Single thread executor finishes all current threads, then shuts down.
	 * Single thread executor will not accept any new threads after this method is run. 
	 */
	
	@Override
	public void sendAllAndShutdown()
	{
		executor_service.shutdown();
	}

	class SendMessageRunnable implements Runnable
	{
		@SuppressWarnings("rawtypes")
		private StandardImmutableObject message;
		private TopicDefinition topic;

		@SuppressWarnings("rawtypes")
		public SendMessageRunnable( TopicDefinition topic, StandardImmutableObject message )
		{
			this.topic = topic;
			this.message = message;
		}

		@Override
		public void run()
		{
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
				for ( File file_header : pfile.listFiles() )
				{
					if ( !file_header.isHidden() )
					{
						for ( File sub_file_header : file_header.listFiles() )
						{
							if ( !sub_file_header.isHidden() )
							{
								File file = new File(sub_file_header.getAbsolutePath() + "/" + objectId.getSimpleValue() + "." + StorageKeyExtension.JSON);
								fos = new FileOutputStream(file.getAbsolutePath());
								fos.write(message.toString().getBytes());
								fos.close();
							}
						}

					}
				}

			}
			catch ( Exception e )
			{
				System.out.println("Sending message Failed.");
			}

		}

	}

	class ListenForMessageRunnable implements Runnable
	{
		File subscription_path;
		MessageListener listener;

		public ListenForMessageRunnable( File subscription_path, MessageListener listener )
		{
			this.subscription_path = subscription_path;
			this.listener = listener;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void run()// this will only find one message.
		{
			Path my_dir = subscription_path.toPath();
			try
			{
				WatchService watcher = my_dir.getFileSystem().newWatchService();
				my_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

				WatchKey watckKey = watcher.take();

				List<WatchEvent<?>> events = watckKey.pollEvents();
				for ( WatchEvent event : events )
				{
					if ( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
					{

						Path message_path = my_dir.resolve(((Path) event.context()));
						File f = new File(message_path.toString());
						listener.onMessageReceived(readFile(f));
						f.delete();

					}
				}
			}
			catch ( Exception e )
			{
				System.out.println(e.getStackTrace());
			}
		}

		private MessageStandardObject readFile( File f )
		{
			FileInputStream fis = null;
			byte[] bytesArray = new byte[(int) f.length()];
			try
			{
				fis = new FileInputStream(f);
				fis.read(bytesArray); // read file into bytes[]
				return new MessageStandardObject(bytesArray);
			}
			catch ( Exception e )
			{
				System.out.println("Something went wrong with reading the file");
				return null;
			}
			finally
			{
				try
				{
					fis.close();
				}
				catch ( IOException e )
				{
					System.out.println("Something went weird when trying to close the file stream");
				}
			}

		}
	}
}
