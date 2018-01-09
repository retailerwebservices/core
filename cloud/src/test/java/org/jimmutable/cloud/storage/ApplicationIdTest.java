package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class ApplicationIdTest extends StubTest
{
	private StringableTestingUtils<ApplicationId> tester = new StringableTestingUtils(new ApplicationId.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("foo", "foo"));
		assertTrue(tester.isValid("FOO", "foo"));

		assertTrue(tester.isValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-"));

		assertTrue(tester.isValid("FoO-BAR", "foo-bar"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("&&!(#$"));
		assertTrue(tester.isInvalid("foo_bar"));
		assertTrue(tester.isInvalid("foo bar"));
	}
}
