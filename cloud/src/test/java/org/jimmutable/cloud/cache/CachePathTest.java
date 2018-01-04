package org.jimmutable.cloud.cache;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class CachePathTest extends StubTest
{
	private StringableTester<CachePath> tester = new StringableTester(new CachePath.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");

		tester.assertValid("FOO-BAR-123", "foo-bar-123");
		tester.assertValid("F", "f");
		
		tester.assertValid("/foo/bar", "foo/bar");
		tester.assertValid("/FOO/BAR/", "foo/bar");
		tester.assertValid("///FOO////BAR/////", "foo/bar");
		
		tester.assertValid("///foo-bar////baz-quz2/////", "foo-bar/baz-quz2");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("foo.bar");
		tester.assertInvalid("/");
	}
}
