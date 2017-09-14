package jimmutable_aws.messaging;

import org.jimmutable.cloud.messaging.SubscriptionDefinition;
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
