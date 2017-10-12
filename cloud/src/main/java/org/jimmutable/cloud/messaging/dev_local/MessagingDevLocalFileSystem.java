package org.jimmutable.cloud.messaging.dev_local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialException;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.Messaging;
import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.cloud.messaging.QueueId;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.threading.DaemonThreadFactory;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.util.IOUtils;

/**
 * This is our local implementation of Messaging. It is designed so that a
 * person can run our messaging services without having to rely on AWS or
 * Google. Messages created are stored as .json files on the local machine, in:
 * ~/jimmutable_aws_dev/messaging/[topic def]/[queue def] This class has a
 * Single thread executor to handle the sending of messages. It is created on
 * creation of this class and can be shutdown by running the SendAllAndShutdown
 * method.
 * 
 * @author andrew.towe jim.kane
 *
 */
public class MessagingDevLocalFileSystem extends Messaging
{
	private static final Logger logger = Logger.getLogger(MessagingDevLocalFileSystem.class.getName());

	private FileSystem fs;

	/**
	 * A single, daemon (won't block application exit) thread pool is shared by all
	 * send operations
	 **/
	private ExecutorService daemon_thread_pool = DaemonThreadFactory.createDaemonFixedThreadPool(2);

	private MessageListenerDaemon message_listener_daemon;

	public MessagingDevLocalFileSystem()
	{
		super();

		fs = new FileSystem();

		// Setup and start the listener daemon
		try
		{
			message_listener_daemon = new MessageListenerDaemon(fs);

			DaemonThreadFactory factory = new DaemonThreadFactory();
			factory.newThread(message_listener_daemon);
		}
		catch ( IOException e )
		{
			logger.severe("Problem registering Message Listener Daemon");
		}
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

		daemon_thread_pool.submit(new SendMessageRunnable(fs, topic, message));

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
	synchronized public boolean startListening( SubscriptionDefinition subscription, MessageListener listener )
	{
		message_listener_daemon.addListener(subscription, listener);

		return true;
	}

	/**
	 * Single thread executor finishes all current threads, then shuts down. Single
	 * thread executor will not accept any new threads after this method is run.
	 */
	@Override
	public void sendAllAndShutdown()
	{
		try
		{
			daemon_thread_pool.shutdown();
			daemon_thread_pool.awaitTermination(100_000, TimeUnit.DAYS);
		}
		catch ( Exception e )
		{
			logger.log(Level.SEVERE, "Error in messing shutdown", e);
		}
	}
}
