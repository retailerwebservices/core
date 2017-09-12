package jimmutable_aws.messaging.common_messages;


import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.cloud.messaging.QueueId;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.utils.StringableTester;

import junit.framework.TestCase;

public class SubscriptionDefinitionTest extends TestCase
{
	private StringableTester<SubscriptionDefinition> tester = new StringableTester(new SubscriptionDefinition.MyConverter());
	
	public void testValid()
	{
		tester.assertValid("topic/topics/queue/queues", "topic/topics/queue/queues");
		tester.assertValid("topic/topics/queue/queues1234", "topic/topics/queue/queues1234");
		tester.assertValid("TOPIC/topics/queue/queues", "topic/topics/queue/queues");
		tester.assertValid(" TOPIC/topics/queue/queues ", "topic/topics/queue/queues");
	}

	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid(" ");
		tester.assertInvalid("1");
		tester.assertInvalid("foo_bar");
		tester.assertInvalid(".foo");
		tester.assertInvalid("foo.");
		tester.assertInvalid("foo..bar");
		tester.assertInvalid("some_id");
		tester.assertInvalid("topic/topics/queue/");
	}
}
