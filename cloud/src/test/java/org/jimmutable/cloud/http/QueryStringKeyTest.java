package org.jimmutable.cloud.http;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class QueryStringKeyTest extends StubTest
{
	private StringableTestingUtils<QueryStringKey> tester = new StringableTestingUtils(new QueryStringKey.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.assertValid("foo", "foo"));
		assertTrue(tester.assertValid("FOO", "foo"));

		
		assertTrue(tester.assertValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-"));

		assertTrue(tester.assertValid("FoO-BAR", "foo-bar"));
		assertTrue(tester.assertValid(" fo o", "foo"));
		assertTrue(tester.assertValid("foo_bar", "foo-bar"));
		assertTrue(tester.assertValid("FOO_BAR", "foo-bar"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("&&!(#$"));
	}
}
