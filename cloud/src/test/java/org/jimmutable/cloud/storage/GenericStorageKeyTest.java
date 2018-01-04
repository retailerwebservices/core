package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.common.Kind;
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
		String raw_key = "qwer/0123-SINS_of-OuR-F4TH3rs.pdf";
		tester.assertValid(raw_key, "qwer/0123-sins_of-our-f4th3rs.pdf");

		StorageKey generic_key = new GenericStorageKey(raw_key);
		assertNotNull(generic_key);
		assertNotNull(generic_key.getSimpleKind());
		assertEquals(generic_key.getSimpleKind(), new Kind("qwer"));
		
		assertNotNull(generic_key.getSimpleName());
		assertEquals(generic_key.getSimpleName(), new StorageKeyName("0123-sins_of-our-f4th3rs"));
		
		assertNotNull(generic_key.getSimpleExtension());
		assertEquals(generic_key.getSimpleExtension(), new StorageKeyExtension("pdf"));
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");

		tester.assertInvalid("abc/0000-0000-0000-a123");
		tester.assertInvalid("abc/.txt");
		tester.assertInvalid("qer/0asdf&.pdf");

		// test invalid kind
		tester.assertInvalid("ab/0000-0000-00A0-a123.txt");
		
		// test invalid id
		tester.assertInvalid("abc/0asdf&.txt");

		// test invalid extension
		tester.assertInvalid("abc/0000-0000-00A0-a123.");
	}
}
