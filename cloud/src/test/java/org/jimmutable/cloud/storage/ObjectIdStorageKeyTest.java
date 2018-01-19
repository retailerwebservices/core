package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class ObjectIdStorageKeyTest extends StubTest
{
	private StringableTestingUtils<ObjectIdStorageKey> tester = new StringableTestingUtils<StorageKey>(new ObjectIdStorageKey.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("abc/0000-0000-0000-0123.txt", "abc/0000-0000-0000-0123.txt"));
		assertTrue(tester.isValid("abc/0000-0000-00A0-a123.txt", "abc/0000-0000-00a0-a123.txt"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("&&!(#$"));

		assertTrue(tester.isInvalid("abc/0000-0000-0000-a123"));
		assertTrue(tester.isInvalid("abc/.txt"));
		assertTrue(tester.isInvalid("ab/0000-0000-00A0-a123.txt"));
	}
}
