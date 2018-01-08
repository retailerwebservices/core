package org.jimmutable.cloud.new_messaging;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.new_messaging.queue.QueueId;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class QueueIdTest extends StubTest
{
	private StringableTester<QueueId> tester = new StringableTester(new QueueId.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("some-id", "some-id");
		tester.assertValid("someid1234", "someid1234");
		tester.assertValid("SOME-id", "some-id");
		tester.assertValid(" SOME-id ", "some-id");
	}

	@Test
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
		tester.assertInvalid("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
	}
}
