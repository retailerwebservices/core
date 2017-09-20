package jimmutable.messaging;

import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.utils.StringableTester;

import junit.framework.TestCase;

public class TopicIdTest extends TestCase
{
	private StringableTester<TopicId> tester = new StringableTester(new TopicId.MyConverter());

	public void testValid()
	{
		tester.assertValid("some-id", "some-id");
		tester.assertValid("someid1234", "someid1234");
		tester.assertValid("SOME-id", "some-id");
		tester.assertValid(" SOME-id ", "some-id");
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
		tester.assertInvalid("foo/bar");
		tester.assertInvalid("some_id");
	}
}

