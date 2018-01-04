package org.jimmutable.cloud.cache;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class CachePathElementTest extends StubTest
{
	private StringableTester<CachePathElement> tester = new StringableTester(new CachePathElement.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");

		tester.assertValid("FOO-BAR-123", "foo-bar-123");
		tester.assertValid("F", "f");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("/foo");
		tester.assertInvalid("foo/");
		tester.assertInvalid("foo.bar");
	}
}
