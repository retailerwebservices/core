package org.jimmutable.cloud.cache;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class CachePathElementTest extends StubTest
{
	private StringableTestingUtils<CachePathElement> tester = new StringableTestingUtils(new CachePathElement.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("foo", "foo"));
		assertTrue(tester.isValid("FOO", "foo"));

		assertTrue(tester.isValid("FOO-BAR-123", "foo-bar-123"));
		assertTrue(tester.isValid("F", "f"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("/foo"));
		assertTrue(tester.isInvalid("foo/"));
		assertTrue(tester.isInvalid("foo.bar"));
	}
}
