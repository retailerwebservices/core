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
		assertTrue(tester.isValid("topic/topics/queue/queues", "topic/topics/queue/queues"));
		assertTrue(tester.isValid("topic/topics/queue/queues1234", "topic/topics/queue/queues1234"));
		assertTrue(tester.isValid("TOPIC/topics/queue/queues", "topic/topics/queue/queues"));
		assertTrue(tester.isValid(" TOPIC/topics/queue/queues ", "topic/topics/queue/queues"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid(" "));
		assertTrue(tester.isInvalid("1"));
		assertTrue(tester.isInvalid("foo_bar"));
		assertTrue(tester.isInvalid(".foo"));
		assertTrue(tester.isInvalid("foo."));
		assertTrue(tester.isInvalid("foo..bar"));
		assertTrue(tester.isInvalid("some_id"));
		assertTrue(tester.isInvalid("topic/topics/queue/"));
	}
}
