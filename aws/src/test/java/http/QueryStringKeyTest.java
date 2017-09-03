package http;

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
		tester.assertValidStringable("foo", "foo");
		tester.assertValidStringable("FOO", "foo");
		
		tester.assertValidStringable("foo_bar", "foobar");
		tester.assertValidStringable("abcdefghijklmnopqrstuvwxyz0123456789-", "abcdefghijklmnopqrstuvwxyz0123456789-");
		
		tester.assertValidStringable("FoO-BAR","foo-bar");
		tester.assertValidStringable(" fo o", "foo");
	}
	
	public void testInvalid()
	{
		tester.assertInvalidStringable(null);
		tester.assertInvalidStringable("");
		tester.assertInvalidStringable("&&!(#$");
	}
}
