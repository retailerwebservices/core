package org.jimmutable.cloud.storage;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class GenericStorageKeyTest extends StubTest
{
	private StringableTester<GenericStorageKey> tester = new StringableTester<GenericStorageKey>(new GenericStorageKey.MyConverter());

	@Test
	public void testValid()
	{
		// ObjectIdStorageKey objects can all be GenericStorageKey objects
		tester.assertValid("abc/0000-0000-0000-0123.txt", "abc/0000-0000-0000-0123.txt");
		tester.assertValid("abc/0000-0000-00A0-a123.txt", "abc/0000-0000-00a0-a123.txt");
		
		// GenericStorageKey objects can also be arbitraty length
		tester.assertValid("qwer/0123-SINS_of-OuR-F4TH3rs.pdf", "qwer/0123-sins_of-our-f4th3rs.pdf");
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
		tester.assertInvalid("qer/0asdf&.pdf");
	}
}
