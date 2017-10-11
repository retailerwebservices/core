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
import java.util.List;
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

import com.amazonaws.util.IOUtils;

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
	
	private Set<SubscriptionListenerPair> listeners =  Collections.newSetFromMap(new ConcurrentHashMap());
	
	private ExecutorService daemon_thread_pool = DaemonThreadFactory.createDaemonFixedThreadPool(9);
	
	private FileSystem fs;
	
	public MessageListenerDaemon(FileSystem fs)
	{
		this.fs = fs;
		
		daemon_thread_pool.submit(this);
	}
	
	public void addListener(SubscriptionDefinition definition, MessageListener listener)
	{
		listeners.add(new SubscriptionListenerPair(definition, listener));
		
		fs.createSubscriptionIfNeeded(definition);
	}
	
	private WatchService setupListener()
	{
		WatchService watcher = null;
		try
		{
			watcher = fs.getSimpleRoot().toPath().getFileSystem().newWatchService();
			fs.getSimpleRoot().toPath().register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		}
		catch ( IOException e )
		{
			logger.log(Level.SEVERE, "Could not setup listener", e);
		}
		return watcher;
	}

	@Override
	public void run()
	{
		WatchService watcher = setupListener();
		WatchKey watch_key = null;
		
		while ( true )
		{
			try
			{
				Thread.sleep(150);

				watch_key = watcher.take();

				List<WatchEvent<?>> events = watch_key.pollEvents();
				
				if ( events == null || events.isEmpty() ) continue;
				
				for ( WatchEvent e : events )
					handleEvent(e);
			}
			catch ( Exception e )
			{
				logger.log(Level.SEVERE, "Error with message",e);
			}
			
			if ( watch_key != null )
				watch_key.reset(); // need this so we can look again
		}
	}

	private void handleEvent( WatchEvent event ) 
	{
		try
		{
			Path message_path = fs.getSimpleRoot().toPath().resolve(((Path) event.context()));
			File message_file = message_path.toFile();
			
			StandardObject message = FileUtils.readObjectFromFile(message_file, null);
			
			message_file.delete();
			
			if ( message == null ) return;
			

			SubscriptionDefinition def = fs.getSubscriptionDefinitionFromFile(message_file, null);
			
			if ( def == null )
			{
				logger.log(Level.WARNING, "Recevied file changed message "+(Path) event.context()+" but could not resolve subscription definition");
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
		catch(Exception e)
		{
			logger.log(Level.WARNING, "error handling message "+event, e);
		}
	}
}
