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
		assertTrue(tester.isValid("foo", "foo"));
		assertTrue(tester.isValid("FOO", "foo"));

		
		assertTrue(tester.isValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-"));

		assertTrue(tester.isValid("FoO-BAR", "foo-bar"));
		assertTrue(tester.isValid(" fo o", "foo"));
		assertTrue(tester.isValid("foo_bar", "foo-bar"));
		assertTrue(tester.isValid("FOO_BAR", "foo-bar"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("&&!(#$"));
	}
}
