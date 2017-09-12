package org.jimmutable.cloud.storage;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class StorageKeyTest extends TestCase
{
	private StringableTester<StorageKey> tester = new StringableTester(new StorageKey.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("abc/0000-0000-0000-0123.txt", "abc/0000-0000-0000-0123.txt");
		tester.assertValid("abc/0000-0000-00A0-a123.txt", "abc/0000-0000-00a0-a123.txt");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");
		
		tester.assertInvalid("abc/0000-0000-0000-a123");
		tester.assertInvalid("abc/.txt");
		tester.assertInvalid("ab/0000-0000-00A0-a123.txt");
	}
}
