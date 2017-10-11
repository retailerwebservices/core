package org.jimmutable.cloud.messaging.dev_local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.FileUtils;
import org.jimmutable.core.utils.Validator;

/**
 * This runnable is used to send a message in the MessagingDevLocalFileSystem.
 * 
 * The operation will write a JSON file in each subscription definintion directory with the specified FileSystem
 * 
 * @author kanej
 *
 */
public class SendMessageRunnable implements Runnable
{
	private static final Logger logger = Logger.getLogger(SendMessageRunnable.class.getName()); 
	
	private FileSystem fs;
	private TopicDefinition topic;
	private StandardObject message;

	
	public SendMessageRunnable( FileSystem fs, TopicDefinition topic, StandardImmutableObject message )
	{
		Validator.notNull(fs, topic, message);
		
		this.fs = fs;
		this.topic = topic;
		this.message = message;
	}

	@Override
	public void run()
	{
		Set<SubscriptionDefinition> all_subs = fs.listAllSubscriptions();
		
		for ( SubscriptionDefinition def : all_subs )
		{
			writeMessage(def);
		}
	}
	
	private void writeMessage(SubscriptionDefinition def)
	{
		try
		{
			ObjectId object_id = ObjectId.createRandomId();
			
			File dest = new File(fs.getDirSubscription(def), object_id.toString() + ".json");
			
			// Create the directory (if need)
			if ( !dest.getParentFile().exists() ) 
				dest.getParentFile().mkdirs();
			
			FileUtils.writeFile(dest,  message, Format.JSON);
		}
		catch ( Exception e )
		{
			logger.log(Level.WARNING, "Could not send message",e);
		}
	}
}

