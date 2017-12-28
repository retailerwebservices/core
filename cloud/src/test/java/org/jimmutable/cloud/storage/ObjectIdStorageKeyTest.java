package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class ObjectIdStorageKeyTest extends StubTest
{
	private StringableTester<ObjectIdStorageKey> tester = new StringableTester<ObjectIdStorageKey>(new ObjectIdStorageKey.MyConverter());

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
