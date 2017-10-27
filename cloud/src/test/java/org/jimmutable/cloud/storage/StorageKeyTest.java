package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class StorageKeyTest extends StubTest
{
	private StringableTester<StorageKey> tester = new StringableTester<StorageKey>(new StorageKey.MyConverter());

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

	
	@Test
	public void testPrettyName()
	{

		StorageKey key = new StorageKey(new Kind("blaa"), "Screen Shot 2017-09-15 at 1.33.18 PM.png", new ObjectId(1), StorageKeyExtension.JSON);

		tester.assertValid("blaa/Screen Shot 2017-09-15 at 1.33.18 PM.png~0000-0000-0000-0001.json");

		assertEquals("blaa/screen_shot_2017-09-15_at_1.33.18_pm.png~0000-0000-0000-0001.json", key.getSimpleValue());

	}

	@Test(expected = ValidationException.class)
	public void invalidPrettyName()
	{
		StringBuilder bigFileName = new StringBuilder();

		bigFileName.append("12345/");
		for (int i = 0; i < 300; i++)
		{
			bigFileName.append("c");
		}
		bigFileName.append("~0");
		bigFileName.append(".html");

		new StorageKey(bigFileName.toString());

	}
}
