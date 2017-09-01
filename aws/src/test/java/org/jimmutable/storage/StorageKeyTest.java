package org.jimmutable.storage;

import junit.framework.TestCase;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public class StorageKeyTest extends TestCase
{
	public void testParsing()
	{
		StorageKey key = new StorageKey("alpha/1234.txt");
		assertEquals("alpha", key.getSimpleKind().getSimpleValue());
		assert (key.getSimpleObjectId().getSimpleValue().contains("1234"));
		assertEquals("text/txt", key.getSimpleExtension().getSimpleMimeType());
	}

	public void testFancyParsing()
	{
		Kind kind = new Kind("alpha");
		ObjectId objectId = new ObjectId("1234");
		StorageKeyExtension extension = new StorageKeyExtension("txt");
		StorageKey key = new StorageKey(kind, objectId, extension);
		assertEquals("alpha", key.getSimpleKind().getSimpleValue());
		assert (key.getSimpleObjectId().getSimpleValue().contains("1234"));
		assertEquals("text/txt", key.getSimpleExtension().getSimpleMimeType());
	}

	public void testBadParsing()
	{
		boolean fail = true;
		try
		{
			StorageKey key = new StorageKey("foo");
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		} catch ( Exception e )
		{
			System.out.println();
		}
		assertFalse(fail);
		fail = true;
		try
		{
			StorageKey key = new StorageKey("foo/bar");
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		} catch ( Exception e )
		{
			System.out.println();
		}
		assertFalse(fail);
	}

	public void testNullParsing()
	{
		boolean fail = true;
		try
		{
			StorageKey key = new StorageKey(null);
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		} catch ( Exception e )
		{
			System.out.println();
		}
		assertFalse(fail);
		fail = true;
		try
		{
			ObjectId objectId = new ObjectId("1234");
			StorageKeyExtension extension = new StorageKeyExtension("txt");
			StorageKey key = new StorageKey(null, objectId, extension);
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		}
		assertFalse(fail);
		fail = true;
		try
		{
			Kind kind = new Kind("alpha");
			StorageKeyExtension extension = new StorageKeyExtension("txt");
			StorageKey key = new StorageKey(kind, null, extension);
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		}
		assertFalse(fail);
		fail = true;
		try
		{
			Kind kind = new Kind("alpha");
			ObjectId objectId = new ObjectId("1234");
			StorageKey key = new StorageKey(kind, objectId, null);
		} catch ( org.jimmutable.core.exceptions.ValidationException e )
		{
			fail = false;
		}
		assertFalse(fail);
	}
}
