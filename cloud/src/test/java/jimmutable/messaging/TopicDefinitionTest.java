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
		assertTrue(tester.assertValid("app/some-id", "app/some-id"));
		assertTrue(tester.assertValid("app/someid1234", "app/someid1234"));
		assertTrue(tester.assertValid("app/SOME-id", "app/some-id"));
		assertTrue(tester.assertValid("app/ SOME-id ", "app/some-id"));
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
		assertTrue(tester.assertInvalid("foo-bar"));
		assertTrue(tester.assertInvalid("some_id"));
	}
}
