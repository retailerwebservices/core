package jimmutable.messaging;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class TopicIdTest
{
	private StringableTestingUtils<TopicId> tester = new StringableTestingUtils(new TopicId.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("some-id", "some-id"));
		assertTrue(tester.isValid("someid1234", "someid1234"));
		assertTrue(tester.isValid("SOME-id", "some-id"));
		assertTrue(tester.isValid(" SOME-id ", "some-id"));
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
		assertTrue(tester.isInvalid("foo/bar"));
		assertTrue(tester.isInvalid("some_id"));
	}
}

