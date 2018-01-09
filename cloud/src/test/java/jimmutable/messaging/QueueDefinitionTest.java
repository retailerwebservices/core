package jimmutable.messaging;



import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.messaging.QueueDefinition;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class QueueDefinitionTest
{
	private StringableTestingUtils<QueueDefinition> tester = new StringableTestingUtils(new QueueDefinition.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.assertValid("some/ids", "some/ids"));
		assertTrue(tester.assertValid("some/id1234", "some/id1234"));
		assertTrue(tester.assertValid("SOME/ids", "some/ids"));
		assertTrue(tester.assertValid(" SOME/ids ", "some/ids"));
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
	}
}
