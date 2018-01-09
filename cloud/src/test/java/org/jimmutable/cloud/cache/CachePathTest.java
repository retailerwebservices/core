package org.jimmutable.cloud.cache;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class CachePathTest extends StubTest
{
	private StringableTestingUtils<CachePath> tester = new StringableTestingUtils(new CachePath.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("foo", "foo"));
		assertTrue(tester.isValid("FOO", "foo"));

		assertTrue(tester.isValid("FOO-BAR-123", "foo-bar-123"));
		assertTrue(tester.isValid("F", "f"));
		
		assertTrue(tester.isValid("/foo/bar", "foo/bar"));
		assertTrue(tester.isValid("/FOO/BAR/", "foo/bar"));
		assertTrue(tester.isValid("///FOO////BAR/////", "foo/bar"));
		
		assertTrue(tester.isValid("///foo-bar////baz-quz2/////", "foo-bar/baz-quz2"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("foo.bar"));
		assertTrue(tester.isInvalid("/"));
	}
}
