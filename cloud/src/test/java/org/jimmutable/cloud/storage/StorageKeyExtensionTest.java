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
		assertTrue(tester.isValid("foo", "foo"));
		assertTrue(tester.isValid("FOO", "foo"));

		assertTrue(tester.isValid("0123456789", "0123456789"));

		assertTrue(tester.isValid(".txt", "txt"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("&&!(#$"));
		assertTrue(tester.isInvalid("foo-bar"));
		assertTrue(tester.isInvalid("foo bar"));
	}
}
