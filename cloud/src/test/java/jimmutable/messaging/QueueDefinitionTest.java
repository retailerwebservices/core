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
		assertTrue(tester.isValid("some/ids", "some/ids"));
		assertTrue(tester.isValid("some/id1234", "some/id1234"));
		assertTrue(tester.isValid("SOME/ids", "some/ids"));
		assertTrue(tester.isValid(" SOME/ids ", "some/ids"));
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
	}
}
