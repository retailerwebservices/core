package org.jimmutable.aws.http;

import org.jimmutable.aws.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class QueryStringKeyTest extends TestCase
{
	private StringableTester<QueryStringKey> tester = new StringableTester(new QueryStringKey.MyConverter());
	
	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");
		
		tester.assertValid("foo_bar", "foobar");
		tester.assertValid("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-");
		
		tester.assertValid("FoO-BAR","foo-bar");
		tester.assertValid(" fo o", "foo");
	}
	
	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");
	}
}
