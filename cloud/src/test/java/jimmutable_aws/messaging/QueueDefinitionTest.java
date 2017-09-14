package jimmutable_aws.messaging;



import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.core.utils.StringableTester;

import junit.framework.TestCase;

public class QueueDefinitionTest extends TestCase
{
	private StringableTester<QueueDefinition> tester = new StringableTester(new QueueDefinition.MyConverter());

	public void testValid()
	{
		tester.assertValid("some/ids", "some/ids");
		tester.assertValid("some/id1234", "some/id1234");
		tester.assertValid("SOME/ids", "some/ids");
		tester.assertValid(" SOME/ids ", "some/ids");
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
	}
}
