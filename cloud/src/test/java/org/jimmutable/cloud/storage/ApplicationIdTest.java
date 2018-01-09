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
		assertTrue(tester.assertValid("foo", "foo"));
		assertTrue(tester.assertValid("FOO", "foo"));

		assertTrue(tester.assertValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-"));

		assertTrue(tester.assertValid("FoO-BAR", "foo-bar"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("&&!(#$"));
		assertTrue(tester.assertInvalid("foo_bar"));
		assertTrue(tester.assertInvalid("foo bar"));
	}
}
