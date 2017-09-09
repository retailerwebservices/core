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

import javax.management.RuntimeErrorException;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.storage.ApplicationId;
import org.jimmutable.storage.StorageKeyExtension;

import com.amazonaws.util.IOUtils;

/**
 * CODE REVIEW: The directory [home directory]/jimmutable_aws_messaging/[ApplicationId]/messaging is not right, it should be ~/jimmutable_aws_dev/messaging/[topic def]/[queue def]
 * 
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
	// CODE REVEIW: Both of these fields should be private (no modified = package private = visible to other classes in the package
	File root;
	final ExecutorService executor_service = Executors.newSingleThreadExecutor(); // CODE REVIEW: No need for final here

	public MessagingDevLocalFileSystem()
	{
		super();
		if ( !ApplicationId.hasOptionalDevApplicationId() )
		{
			System.err.println("Hey -- you are trying to instantiate a dev local file system. This should not be happening in production. If you are a developer and you are trying to run this through eclipse, you need to setup the environment configurations in your run configurations");
			throw new RuntimeException();
		}

		root = new File(System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(new ApplicationId("Development")) + "/messaging");  // CODE REVIEW: This path is wrong.  Messaging is, by it definition, intra application.  Should be ~/jimmutable_aws_dev/messaging/
		
		// CODE REVIEW: No need for this code, you can just say root.mkdirs();
		
	    String pathname = root.getAbsolutePath();
		File pfile = new File(pathname);
		pfile.mkdirs();
	}

	/**
	 * @param topic
	 *            the topic that you want the message you want the message to go to
	 * @param message
	 *            the message you want to send
	 * @return true if the thread was created, false otherwise.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean sendAsync( TopicDefinition topic, StandardImmutableObject message )
	{
		if ( message == null )
		{
			System.out.println("message is null"); // CODE REVIEW: Print nothing, just return
			return false;
		}

		// need to ask about message size?

		executor_service.submit(new Thread(new SendMessageRunnable(topic, message)));

		return true;
	}

	/**
	 * @param subscription
	 *            the subscription you want to listen to
	 * @param listener
	 *            the listener you want to handle the message
	 * @return true if the threads were created, false otherwise.
	 */
	@Override
	public boolean startListening( SubscriptionDefinition subscription, MessageListener listener )
	{
		File subscription_path = new File(root.getAbsolutePath() + "/" + subscription.getSimpleValue()); // CODE REVIEW: Use the two parameter constructor for file, namely new File(root, subscription.getSimpleValue());
		if ( !subscription_path.exists() )// if subscription does not exist, make it.
		{
			subscription_path.mkdirs();
		}

		new Thread(new ListenForMessageRunnable(subscription_path, listener)).start();

		return true;
	}

	/**
	 * Single thread executor finishes all current threads, then shuts down. Single
	 * thread executor will not accept any new threads after this method is run.
	 */

	@Override
	public void sendAllAndShutdown()
	{
		executor_service.shutdown();
	}

	// CODE REVIEW: Need private modifier
	class SendMessageRunnable implements Runnable
	{
		@SuppressWarnings("rawtypes")
		private StandardObject message;
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
			// CODE REVIEW: Replace with Validator.notNull(topic)
			if ( topic == null )
			{
				throw new RuntimeException("Topic cannot be empty");
			}
			Random rng = new Random();
			ObjectId objectId = new ObjectId(rng.nextLong());

			FileOutputStream fos;

			try
			{
				// CODE REVIEW: Use two parameter constructor for file, namely new File(root, topic.getSimpleValue);
				String pathname = root.getAbsolutePath() + "/" + topic.getSimpleValue();
				File pfile = new File(pathname);

				// CODE REIVEW: Create a helper methods List<File> listDirectoriesToPutMessagesInto() that does the listing of directories that should get messages
				// CODE REIVEW: What is withthe calls to isHidden()?  Was there an issue with hidden files/directories?   I think the code should use isDirectory() to make sure that its only trying to list directories etc.
				
				File[] listFiles = pfile.listFiles();
				if ( listFiles != null )
				{
					for ( File file_header : listFiles )
					{
						listFiles = pfile.listFiles();
						if ( !file_header.isHidden() && listFiles != null )
						{
							for ( File sub_file_header : file_header.listFiles() )
							{
								if ( !sub_file_header.isHidden() )
								{
									File file = new File(sub_file_header.getAbsolutePath() + "/" + objectId.getSimpleValue() + "." + StorageKeyExtension.JSON);
									fos = new FileOutputStream(file.getAbsolutePath());
									fos.write(ObjectWriter.serialize(Format.JSON_PRETTY_PRINT, message).getBytes());
									fos.close();
								}
							}

						}
						else {
							System.out.println("No Topics to send to. No message sent."); // CODE REVIEW: Use logging
						}
					}
				}
				else
				{
					System.out.println("No Topics to send to. No message sent."); // CODE REVIEW: Use logging
				}

			}
			catch ( Exception e )
			{
				System.out.println("Sending message Failed: " + e + "\n"); // CODE REVEIW: Use logging
				e.printStackTrace();
			}
		}
	}

	// CODE REVIEW: Needs to be private
	class ListenForMessageRunnable implements Runnable
	{
		// CODE REVIEW: Needs private modifier
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
			// CODE REVIEW: Break this into methods
			// CODE REVIEW: Also, this code does not appear write to me, you need to continue to poll events *forever* with some kind of sleep in between.  
			/**
			 * while(true)
			 * {
			 * 
			 * 		sleep(500);
			 * 
			 * 		List<WatchEvent<?>> events = watckKey.pollEvents();
			 * 
			 * 		for ( WatchEvent event : events )
			 * 		{
			 * 			handleEvent(event);
			 * 		}
			 * }
			 * 
			 * 
			 * handleEvent() should have all the code to handle the evenet
			 * 
			 */
			
			// CODE REVIEW: With the error handling code in the outer loop like that, any exception will stop the messaging service forever...
			
			
			Path my_dir = subscription_path.toPath();
			try
			{
				WatchService watcher = my_dir.getFileSystem().newWatchService();
				my_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
				WatchKey watckKey = watcher.take();

				List<WatchEvent<?>> events = watckKey.pollEvents();
				for ( WatchEvent event : events )
				{
					if ( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
					{

						Path message_path = my_dir.resolve(((Path) event.context()));
						File f = new File(message_path.toString());
						listener.onMessageReceived(StandardObject.deserialize(readFile(f)));
						f.delete();

					}
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}

		private String readFile( File f )
		{
			FileInputStream fis = null;
			
			// CODE REVEIW: This code is not write.  Read does not guarantee that it reads all of the data from a file.  I recommend you just use IOUtils. IOUtils.toByteArray(fis) (remember to close the stream afterwards)
			
			byte[] bytesArray = new byte[(int) f.length()];
			try
			{
				fis = new FileInputStream(f);
				fis.read(bytesArray); // read file into bytes[]
				return new String(bytesArray);
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
