package jimmutable_aws.messaging;

import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.utils.StringableTester;

import junit.framework.TestCase;

public class TopicDefinitionTest extends TestCase
{
	private StringableTester<TopicId> tester = new StringableTester(new TopicDefinition.MyConverter());

	public void testValid()
	{
		tester.assertValid("app/some-id", "app/some-id");
		tester.assertValid("app/someid1234", "app/someid1234");
		tester.assertValid("app/SOME-id", "app/some-id");
		tester.assertValid("app/ SOME-id ", "app/some-id");
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
		tester.assertInvalid("foo-bar");
		tester.assertInvalid("some_id");
	}
}
