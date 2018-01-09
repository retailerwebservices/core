package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class StorageKeyExtensionTest extends StubTest
{
	private StringableTestingUtils<StorageKeyExtension> tester = new StringableTestingUtils(new StorageKeyExtension.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.assertValid("foo", "foo"));
		assertTrue(tester.assertValid("FOO", "foo"));

		assertTrue(tester.assertValid("0123456789", "0123456789"));

		assertTrue(tester.assertValid(".txt", "txt"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("&&!(#$"));
		assertTrue(tester.assertInvalid("foo-bar"));
		assertTrue(tester.assertInvalid("foo bar"));
	}
}
