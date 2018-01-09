package jimmutable.messaging.dev_local;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.dev_local.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSystemTest
{
	private File root;
	
	private FileSystem fs;
	
	
	@Before
	public void setupMessagingDir()
	{
		root = getTempDir();
		
		System.out.println("Messaging root; "+root);
		
		fs = new FileSystem(root);
		
		fs.createSubscriptionIfNeeded(new SubscriptionDefinition("app-one","public", "app-two", "thumbnail-creator"));
		fs.createSubscriptionIfNeeded(new SubscriptionDefinition("app-one","public", "app-two", "user-emailer"));
		fs.createSubscriptionIfNeeded(new SubscriptionDefinition("app-one","public", "app-one", "public-counter"));
		
		fs.createSubscriptionIfNeeded(new SubscriptionDefinition("app-one","private", "app-one", "search-updater"));
		
		fs.createSubscriptionIfNeeded(new SubscriptionDefinition("app-two","public", "app-one", "thumb-analytics"));
	}
	
	@After
	public void tearDown()
	{
		try { delete(root); } catch(Exception e) { e.printStackTrace(); }
	}
	
	
	static private void delete( File f ) throws IOException
	{
		if ( f.isDirectory() )
		{
			for ( File c : f.listFiles() )
				delete(c);
		}
		if ( !f.delete() )
			throw new FileNotFoundException("Failed to delete file: " + f);
	}
	
	private File getTempDir()
	{
		try
		{
			File tmp = File.createTempFile("foo", "bar");
			
			if ( tmp.exists() ) tmp.delete();
			
			File tmp_dir = tmp.getParentFile();
			
			int count = 0;
			
			
			
			while(true)
			{
				File ret = new File(tmp_dir, String.format("messaging%d", count));
				count++;
				
				if ( ret.exists() ) continue; 
				
				return ret;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Test
	public void testListSubscriptions()
	{
		Set<SubscriptionDefinition> all_subs = fs.listAllSubscriptions();
		
		
		assertTrue(all_subs.contains(new SubscriptionDefinition("app-one","public", "app-two", "thumbnail-creator")));
		assertTrue(all_subs.contains(new SubscriptionDefinition("app-one","public", "app-two", "user-emailer")));
		assertTrue(all_subs.contains(new SubscriptionDefinition("app-one","public", "app-one", "public-counter")));
		
		assertTrue(all_subs.contains(new SubscriptionDefinition("app-one","private", "app-one", "search-updater")));
		
		assertTrue(all_subs.contains(new SubscriptionDefinition("app-two","public", "app-one", "thumb-analytics")));
		
		assertTrue(all_subs.size() == 5);
	}
	
	@Test
	public void testListAllTopics()
	{
		Set<TopicDefinition> topics = fs.listAllTopics();
		
		assertTrue(topics.size() == 3);
		
		assertTrue(topics.contains(new TopicDefinition("app-one","public")));
		assertTrue(topics.contains(new TopicDefinition("app-one","private")));
		assertTrue(topics.contains(new TopicDefinition("app-two","public")));
	}
}

