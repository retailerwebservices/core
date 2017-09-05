package jimmutable_aws.messaging.common_messages;

import org.jimmutable.aws.messaging.QueueDefinition;
import org.jimmutable.aws.messaging.QueueId;
import org.jimmutable.storage.ApplicationId;

import junit.framework.TestCase;

public class QueueDefinitionTest extends TestCase
{

	public static void testCreation()
	{
		QueueDefinition queue_def = new QueueDefinition("ApplicationId/Queueid");
		assertEquals("ApplicationId/Queueid", queue_def.getSimpleValue());
		
		ApplicationId applicationId = new ApplicationId("ApplicationId");
		QueueId queueId = new QueueId("Queueid");
		queue_def = new QueueDefinition("ApplicationId/Queueid");
		assertEquals("ApplicationId/Queueid", queue_def.getSimpleValue());
	}

}
