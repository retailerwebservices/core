package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class StorageKeyTest extends StubTest
{
	private StringableTestingUtils<StorageKey> tester = new StringableTestingUtils<StorageKey>(new StorageKey.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.assertValid("abc/0000-0000-0000-0123.txt", "abc/0000-0000-0000-0123.txt"));
		assertTrue(tester.assertValid("abc/0000-0000-00A0-a123.txt", "abc/0000-0000-00a0-a123.txt"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("&&!(#$"));

		assertTrue(tester.assertInvalid("abc/0000-0000-0000-a123"));
		assertTrue(tester.assertInvalid("abc/.txt"));
		assertTrue(tester.assertInvalid("ab/0000-0000-00A0-a123.txt"));
	}
}
