package jimmutable.messaging.dev_local;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.RejectedExecutionException;

import javax.swing.JOptionPane;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.cloud.messaging.QueueId;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.cloud.messaging.dev_local.MessagingDevLocalFileSystem;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessagingDevLocalFileSystemTest extends StubTest
{
	private static final Kind KIND = new Kind("testKind");
	private static final TopicDefinition TOPIC_DEF_SEND = new TopicDefinition(new ApplicationId("test-application"), new TopicId("test-send"));
	private static final TopicDefinition TOPIC_DEF_LISTEN = new TopicDefinition(new ApplicationId("test-application"), new TopicId("test-listen"));
	private static final TopicDefinition TOPIC_DEF_SHUTDOWN = new TopicDefinition(new ApplicationId("test-application"), new TopicId("test-shutdown"));
	private static final ApplicationId app_id = CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId();
	static MessagingDevLocalFileSystem messagingdevlocalfilesystem;

	@Before
	public void moreSetup()
	{
		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);
		messagingdevlocalfilesystem = new MessagingDevLocalFileSystem();
	}

	@Test
	public void testSendAsync() throws InterruptedException, IOException
	{
		new File(System.getProperty("user.home") + "/jimmutable_aws_dev/messaging/test-application/test/stub/the-holy-grail/").mkdirs();// setup so queue structure is there.
		assertTrue(messagingdevlocalfilesystem.sendAsync(TOPIC_DEF_SEND, new StandardMessageOnUpsert(KIND, ObjectId.createRandomId())));
		Thread.sleep(1000);
		File file_we_are_looking_for = new File(System.getProperty("user.home") + "/jimmutable_aws_dev/messaging/test-application/test/stub/the-holy-grail/");
		assertEquals(1, file_we_are_looking_for.listFiles().length);
	}

	@Test
	public void testStartListening() throws InterruptedException
	{
		TestAppMessageListener listener = new TestAppMessageListener();
		assertTrue(messagingdevlocalfilesystem.startListening(new SubscriptionDefinition(TOPIC_DEF_LISTEN, new QueueDefinition(app_id, new QueueId("the-holy-grail"))), listener));
		ObjectId createRandomId = ObjectId.createRandomId();
		StandardMessageOnUpsert message = new StandardMessageOnUpsert(KIND, createRandomId);
		assertTrue(messagingdevlocalfilesystem.sendAsync(TOPIC_DEF_LISTEN, message));
		Thread.sleep(11000);
		assertEquals(KIND.toString(), listener.messageContent);
	}

	@Test
	public void testSendAllAndShutdown() throws InterruptedException
	{
		String mainpath = System.getProperty("user.home") + "/jimmutable_aws_dev/messaging/test-application/test/";
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
		for ( int i = 0; i < 10; i++ )
		{
			messagingdevlocalfilesystem.sendAsync(TOPIC_DEF_SHUTDOWN, new StandardMessageOnUpsert(KIND, ObjectId.createRandomId()));// putting in a bunch of random information
		}
		messagingdevlocalfilesystem.sendAllAndShutdown();
		int[] initialResults=new int[4];
		int j=0;
		for ( String queue_application_id : Arrays.asList("lancelot", "galahad") )
		{
			for ( String queue_queue_id : Arrays.asList("queue1", "queue2") )
			{
				String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
				File f = new File(filepath);
				initialResults[j++]=f.listFiles().length;
			}
		}
		boolean gotAnErrorMessage=false;
		try {
			messagingdevlocalfilesystem.sendAsync(TOPIC_DEF_SHUTDOWN, new StandardMessageOnUpsert(KIND, ObjectId.createRandomId()));// putting in a bunch of random information
		}catch(RejectedExecutionException e) {
			gotAnErrorMessage=true;
		}catch(Exception e) {
			fail();
		}
		assertTrue(gotAnErrorMessage);
	}

	@After
	public void tearDown()
	{
		String filePathString = System.getProperty("user.home") + "/jimmutable_aws_dev/messaging/test-application";// application id
		// needs to go
		// here
		File f = new File(filePathString);
		if ( f.exists() )
		{
			Path rootPath = Paths.get(filePathString);
			try
			{
				Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace();
			}
		}

	}

}

class TestAppMessageListener implements MessageListener
{
	public String messageContent;

	@Override
	public void onMessageReceived( @SuppressWarnings("rawtypes") StandardObject message )
	{
		messageContent = ((StandardMessageOnUpsert) message).getSimpleKind().toString();
	}
}
