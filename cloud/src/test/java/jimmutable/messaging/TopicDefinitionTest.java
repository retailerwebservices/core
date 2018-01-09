package jimmutable.messaging;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class TopicDefinitionTest
{
	private StringableTestingUtils<TopicId> tester = new StringableTestingUtils(new TopicDefinition.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("app/some-id", "app/some-id"));
		assertTrue(tester.isValid("app/someid1234", "app/someid1234"));
		assertTrue(tester.isValid("app/SOME-id", "app/some-id"));
		assertTrue(tester.isValid("app/ SOME-id ", "app/some-id"));
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
		assertTrue(tester.isInvalid("foo-bar"));
		assertTrue(tester.isInvalid("some_id"));
	}
}
