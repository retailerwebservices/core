package jimmutable_aws.messaging.common_messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.jimmutable.aws.messaging.MessageListener;
import org.jimmutable.aws.messaging.MessageStandardObject;
import org.jimmutable.aws.messaging.MessagingDevLocalFileSystem;
import org.jimmutable.aws.messaging.QueueDefinition;
import org.jimmutable.aws.messaging.QueueId;
import org.jimmutable.aws.messaging.SubscriptionDefinition;
import org.jimmutable.aws.messaging.TopicDefinition;
import org.jimmutable.aws.messaging.TopicId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.storage.ApplicationId;

import junit.framework.TestCase;

public class MessageDevLocalFileSystemTest extends TestCase
{
	static ApplicationId appId;
	static MessagingDevLocalFileSystem messagingdevlocalfilesystem;

	@Override
	protected void setUp()
	{
		appId = new ApplicationId("Development");
		messagingdevlocalfilesystem = new MessagingDevLocalFileSystem();
	}

	public static void testSendAsync() throws InterruptedException
	{
		String mainpath = System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/messaging/development/knights_in_monty_python";
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				f.mkdirs();
			}
		}

		assertTrue(messagingdevlocalfilesystem.sendAsync(new TopicDefinition(appId, new TopicId("Knights_in_Monty_Python")), new QueueId("NIII")));
		Thread.sleep(1000);// have it wait a second

		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				File[] listFiles = f.listFiles();
				assertEquals(1, listFiles.length);
				assertEquals("niii", readFile(listFiles[0]));
			}
		}
	}

	private static String readFile( File f )
	{
		FileInputStream fis = null;
		byte[] bytesArray = new byte[(int) f.length()];
		try
		{
			fis = new FileInputStream(f);
			fis.read(bytesArray); // read file into bytes[]

		}
		catch ( Exception e )
		{
			fail("Something went wierd when reading the file");
		}
		finally
		{
			try
			{
				fis.close();
			}
			catch ( IOException e )
			{
				fail("Something went weird when trying to close the file stream");
			}
		}
		return new String(bytesArray);
	}

	public static void testStartListening() throws InterruptedException
	{
		String mainpath = System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/messaging/development/monty_python_jokes";
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				f.mkdirs();
			}
		}
		TestMessageListener listener = new TestMessageListener();
		assertTrue(messagingdevlocalfilesystem.startListening(new SubscriptionDefinition(new TopicDefinition(appId, new TopicId("monty_python_jokes")), new QueueDefinition(appId, new QueueId("the-holy-grail"))), listener));
		Thread.sleep(30000);// have it wait a minute so we can setup the
		assertTrue(messagingdevlocalfilesystem.sendAsync(new TopicDefinition(appId, new TopicId("monty_python_jokes")), new QueueId("the-holy-grail")));
		Thread.sleep(60000);// have it wait a minute so it can detect changes.
		assertTrue(listener.messageDetected);
		assertEquals("the-holy-grail", new String(listener.messageContent));

		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				assertEquals(0, f.listFiles().length);
			}
		}
	}

	public static void testSendAllAndShutdown() throws InterruptedException
	{
		String mainpath = System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/messaging/development/monty_python_jokes";
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				f.mkdirs();
			}
		}
		Random r = new Random();
		for ( int i = 0; i < 100; i++ )
		{
			messagingdevlocalfilesystem.sendAsync(new TopicDefinition(appId, new TopicId("monty_python_jokes")), new QueueId("" + r.nextInt()));// putting in a bunch of random information
		}
		messagingdevlocalfilesystem.sendAllAndShutdown();
		int[] initialResults=new int[4];
		int i=0;
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				initialResults[i++]=f.listFiles().length;
			}
		}
		Thread.sleep(60000);
		i=0;
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				assertTrue(initialResults[i++]<f.listFiles().length);
			}
		}
		
	}

	@Override
	protected void tearDown()
	{
		String filePathString = System.getProperty("user.home") + "/jimmtuable_aws_dev/" + ApplicationId.getOptionalDevApplicationId(appId);// application id needs to go here
		File f = new File(filePathString);
		if ( f.exists() )
		{
			Path rootPath = Paths.get(filePathString);
			try
			{
				Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
}

class TestMessageListener implements MessageListener
{
	public byte[] messageContent;
	public boolean messageDetected = false;

	@Override
	public void onMessageReceived( @SuppressWarnings("rawtypes") StandardObject message )
	{
		messageDetected = true;
		messageContent = ((MessageStandardObject) message).getData();
	}
}
