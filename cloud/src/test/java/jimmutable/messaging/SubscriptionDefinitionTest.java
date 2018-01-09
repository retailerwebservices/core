package jimmutable.messaging;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class SubscriptionDefinitionTest
{
	private StringableTestingUtils<SubscriptionDefinition> tester = new StringableTestingUtils(new SubscriptionDefinition.MyConverter());
	
	@Test
	public void testValid()
	{
		assertTrue(tester.assertValid("topic/topics/queue/queues", "topic/topics/queue/queues"));
		assertTrue(tester.assertValid("topic/topics/queue/queues1234", "topic/topics/queue/queues1234"));
		assertTrue(tester.assertValid("TOPIC/topics/queue/queues", "topic/topics/queue/queues"));
		assertTrue(tester.assertValid(" TOPIC/topics/queue/queues ", "topic/topics/queue/queues"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid(" "));
		assertTrue(tester.assertInvalid("1"));
		assertTrue(tester.assertInvalid("foo_bar"));
		assertTrue(tester.assertInvalid(".foo"));
		assertTrue(tester.assertInvalid("foo."));
		assertTrue(tester.assertInvalid("foo..bar"));
		assertTrue(tester.assertInvalid("some_id"));
		assertTrue(tester.assertInvalid("topic/topics/queue/"));
	}
}
