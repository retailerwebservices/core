package org.jimmutable.cloud.storage;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class ApplicationIdTest extends TestCase
{
	private StringableTester<ApplicationId> tester = new StringableTester(new ApplicationId.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");

		tester.assertValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-");

		tester.assertValid("FoO-BAR", "foo-bar");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");
		tester.assertInvalid("foo_bar");
		tester.assertInvalid("foo bar");
	}
}
