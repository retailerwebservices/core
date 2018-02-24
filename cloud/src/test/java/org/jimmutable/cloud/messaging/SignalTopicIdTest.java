package org.jimmutable.cloud.messaging;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class SignalTopicIdTest
{
	private StringableTestingUtils<SignalTopicId> tester = new StringableTestingUtils(new SignalTopicId.MyConverter());

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
		assertTrue(tester.isInvalid("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"));
	}
}
