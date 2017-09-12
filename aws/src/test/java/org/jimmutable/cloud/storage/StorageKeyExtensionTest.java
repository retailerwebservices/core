package org.jimmutable.cloud.storage;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class StorageKeyExtensionTest extends TestCase
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
