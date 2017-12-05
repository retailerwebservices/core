package org.jimmutable.cloud.messaging.dev_local;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.threading.DaemonThreadFactory;
import org.jimmutable.core.utils.FileUtils;

/**
 * The message listener daemon is responsible for watching the messaging file
 * system directories for new files, reading them and invoking to appropriate
 * listeners.
 * 
 * @author kanej
 *
 */
public class MessageListenerDaemon implements Runnable
{
	private static final Logger logger = Logger.getLogger(MessageListenerDaemon.class.getName());

	private Set<SubscriptionListenerPair> listeners = Collections.newSetFromMap(new ConcurrentHashMap());

	private ExecutorService daemon_thread_pool = DaemonThreadFactory.createDaemonFixedThreadPool(9);

	private WatchService watcher;

	private FileSystem fs;

	private Map<WatchKey, Path> keys;

	public MessageListenerDaemon( FileSystem fs ) throws IOException
	{
		this.keys = new HashMap<WatchKey, Path>();
		this.fs = fs;
		this.watcher = fs.getSimpleRoot().toPath().getFileSystem().newWatchService();

		daemon_thread_pool.submit(this);
	}

	public void addListener( SubscriptionDefinition definition, MessageListener listener )
	{
		fs.createSubscriptionIfNeeded(definition);

		listeners.add(new SubscriptionListenerPair(definition, listener));
		try
		{
			setupListener();
		}
		catch ( IOException e )
		{
			logger.severe("Problem registering Message Listener Daemon");
		}
	}

	private void setupListener() throws IOException
	{

		try
		{
			Path path = fs.getSimpleRoot().toPath();

			// register directory and sub-directories
			Files.walkFileTree(path, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException
				{
					register(dir);
					return FileVisitResult.CONTINUE;
				}
			});

		}
		catch ( IOException e )
		{
			logger.log(Level.SEVERE, "Could not setup listener", e);
		}

	}

	private void register( Path dir ) throws IOException
	{
		WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

		Path prev = keys.get(key);
		if ( prev == null )
		{
			logger.info(String.format("register: %s\n", dir));
		}
		else
		{
			if ( !dir.equals(prev) )
			{
				logger.info(String.format("update: %s -> %s\n", prev, dir));
			}
		}

		keys.put(key, dir);
	}

	@Override
	public void run()
	{
		WatchKey watch_key = null;

		while ( true )
		{
			try
			{
				Thread.sleep(150);

				watch_key = watcher.take();
				Path dir = keys.get(watch_key);
				List<WatchEvent<?>> events = watch_key.pollEvents();

				if ( events == null || events.isEmpty() )
					continue;

				for ( WatchEvent e : events )
					handleEvent(dir, e);
			}
			catch ( Exception e )
			{
				logger.log(Level.SEVERE, "Error with message", e);
			}

			if ( watch_key != null )
				watch_key.reset(); // need this so we can look again
		}
	}

	private void handleEvent( Path dir, WatchEvent event )
	{
		try
		{
			Path message_path = fs.getSimpleRoot().toPath().resolve(((Path) event.context()));
			String file_name = message_path.toString().substring(message_path.toString().lastIndexOf(File.separator));
			String path_to_file = dir.toString()+file_name;
			File message_file = Paths.get(path_to_file).toFile();

			StandardObject message = FileUtils.readObjectFromFile(message_file, null);

			if ( message == null )
				return;

			SubscriptionDefinition def = fs.getSubscriptionDefinitionFromFile(message_file, null);

			message_file.delete();

			if ( def == null )
			{
				logger.log(Level.WARNING, "Recevied file changed message " + (Path) event.context() + " but could not resolve subscription definition");
				return;
			}

			for ( SubscriptionListenerPair pair : listeners )
			{
				if ( pair.getSimpleSubscriptionDefinition().equals(def) )
				{
					daemon_thread_pool.submit(new OnMessageReceivedRunnable(pair.getSimpleListener(), message));
				}
			}
		}
		catch ( Exception e )
		{
			logger.log(Level.WARNING, "error handling message " + event, e);
		}
	}
}
