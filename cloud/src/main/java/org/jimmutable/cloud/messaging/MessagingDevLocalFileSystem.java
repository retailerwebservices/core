package org.jimmutable.cloud.messaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.RuntimeErrorException;
import javax.sql.rowset.serial.SerialException;

import org.jimmutable.core.utils.Validator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;

import com.amazonaws.util.IOUtils;

/**
 * This is our local implementation of Messaging. It is designed so that a
 * person can run our messaging services without having to rely on AWS or
 * Google. Messages created are stored as .json files on the local machine, in:
 * ~/jimmutable_dev/messaging/[topic def]/[queue def] This class has a
 * Single thread executor to handle the sending of messages. It is created on
 * creation of this class and can be shutdown by running the SendAllAndShutdown
 * method.
 * 
 * @author andrew.towe
 *
 */
public class MessagingDevLocalFileSystem extends Messaging
{

	private File root;
	private ExecutorService executor_service = Executors.newSingleThreadExecutor();
	private static final Logger logger = LogManager.getLogger(MessagingDevLocalFileSystem.class.getName());

	public MessagingDevLocalFileSystem()
	{
		super();
		
		if ( !ApplicationId.hasOptionalDevApplicationId() )
		{
			System.err.println("Hey -- you are trying to instantiate a dev local file system. This should not be happening in production. If you are a developer and you are trying to run this through eclipse, you need to setup the environment configurations in your run configurations");
			throw new RuntimeException();
		}

		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);
		
		root = new File(System.getProperty("user.home"), "/jimmutable_dev/messaging");
		root.mkdirs();
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
		File subscription_path = new File(root.getAbsolutePath(), subscription.getSimpleValue());
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

	private class SendMessageRunnable implements Runnable
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
			Validator.notNull(topic);
			ObjectId objectId = new ObjectId(new Random().nextLong());

			FileOutputStream fos;

			try
			{
				File pfile = new File(root.getAbsolutePath(), topic.getSimpleValue());

				// CODE REIVEW: What is with the calls to isHidden()? Was there an issue with
				// hidden files/directories?
				// CODE ANSWER: We were having issues with Hidden directories (The one I found
				// was ./DStore)

				for ( File sub_file_header : listDirectoriesToPutMessagesInto(pfile) )
				{
					writeFile(objectId, sub_file_header);
				}
			}
			catch ( Exception e )
			{
				logger.log(Level.WARN, "Could not send message",e);
			}
		}

		private List<File> listDirectoriesToPutMessagesInto( File pfile )
		{
			List<File> filesToReturn = new ArrayList<File>();
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
								filesToReturn.add(sub_file_header);
							}
						}
					}
				}
			}
			return filesToReturn;
		}

		private void writeFile( ObjectId objectId, File sub_file_header ) throws FileNotFoundException, IOException
		{
			FileOutputStream fos;
			File file = new File(sub_file_header.getAbsolutePath(), objectId.getSimpleValue() + "." + StorageKeyExtension.JSON);
			fos = new FileOutputStream(file.getAbsolutePath());
			fos.write(ObjectWriter.serialize(Format.JSON_PRETTY_PRINT, message).getBytes());
			fos.close();
		}
	}

	private class ListenForMessageRunnable implements Runnable
	{

		private MessageListener listener;
		private Path my_dir;

		public ListenForMessageRunnable( File subscription_path, MessageListener listener )
		{
			this.my_dir = subscription_path.toPath();
			this.listener = listener;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void run()
		{
			WatchService watcher = setupListener();
			WatchKey watchKey = null;
			while ( true )
			{
				try
				{
					Thread.sleep(500);

					watchKey = watcher.take();

					List<WatchEvent<?>> events = watchKey.pollEvents();
					for ( WatchEvent event : events )
					{
						if ( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
						{
							handleEvent(event);
						}
					}
				}
				catch ( Exception e )
				{
					logger.log(Level.ERROR, "Could not hear message",e);
				}
				watchKey.reset(); // need this so we can look again
			}
		}

		private void handleEvent( WatchEvent event ) throws SerialException
		{
			Path message_path = my_dir.resolve(((Path) event.context()));
			File f = new File(message_path.toString());
			listener.onMessageReceived(StandardObject.deserialize(readFile(f)));
			f.delete();
		}

		private WatchService setupListener()
		{
			WatchService watcher = null;
			try
			{
				watcher = my_dir.getFileSystem().newWatchService();
				my_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
			}
			catch ( IOException e )
			{
				logger.log(Level.ERROR, "Could not setup listener",e);
			}
			return watcher;
		}

		private String readFile( File f )
		{
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(f);
				byte[] byteArray = IOUtils.toByteArray(fis);
				fis.close();
				return new String(byteArray);
			}
			catch ( Exception e )
			{
				logger.log(Level.ERROR, "Something went wrong with reading the file", e);
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
					logger.log(Level.ERROR, "Something went weird when trying to close the file stream", e);
				}
			}

		}
	}
}
