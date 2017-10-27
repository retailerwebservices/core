package org.jimmutable.cloud.storage;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class StorageKeyExtensionTest extends StubTest
{
	private StringableTester<StorageKeyExtension> tester = new StringableTester(new StorageKeyExtension.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");

		tester.assertValid("0123456789", "0123456789");

		tester.assertValid(".txt", "txt");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");
		tester.assertInvalid("foo-bar");
		tester.assertInvalid("foo bar");
	}
}
