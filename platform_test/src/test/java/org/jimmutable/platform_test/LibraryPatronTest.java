package org.jimmutable.platform_test;

import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.EmailAddress;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

public class LibraryPatronTest extends TestCase
{
	@BeforeClass
	protected void setUpTest()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(LibraryPatron.class);
	}

	@Test
	public void testUserSerialization()
	{
		LibraryPatron patron = new LibraryPatron(new ObjectId(123), "First", "last",new EmailAddress("a@g.com"),"123-45-6789", new Day(new DateTime(123)),2,new StorageKey(new Kind("Photo"), new ObjectId(123), new StorageKeyExtension("txt")));
		String serialized_value = patron.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals(serialized_value,"{\n" + 
				"  \"type_hint\" : \"LibraryPatron\",\n" + 
				"  \"id\" : {\n" + 
				"    \"type_hint\" : \"string\",\n" + 
				"    \"primitive_value\" : \"0000-0000-0000-007b\"\n" + 
				"  },\n" + 
				"  \"first_name\" : \"First\",\n" + 
				"  \"last_name\" : \"last\",\n" + 
				"  \"email_address\" : \"a@g.com\",\n" + 
				"  \"ssn\" : \"123-45-6789\",\n" + 
				"  \"birth_date\" : \"12/31/1969\",\n" + 
				"  \"number_of_books_checked_out\" : 2,\n" + 
				"  \"avatar\" : {\n" + 
				"    \"type_hint\" : \"string\",\n" + 
				"    \"primitive_value\" : \"photo/0000-0000-0000-007b.txt\"\n" + 
				"  }\n" + 
				"}");
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		LibraryPatron patron = new LibraryPatron(new ObjectId(123), "First", "last",new EmailAddress("a@g.com"),"123-45-6789", new Day(new DateTime(123)),2,new StorageKey(new Kind("Photo"), new ObjectId(123), new StorageKeyExtension("txt")));
		LibraryPatron patron2 = new LibraryPatron(new ObjectId(123), "First", "last",new EmailAddress("a@g.com"),"123-45-6789", new Day(new DateTime(123)),2,new StorageKey(new Kind("Photo"), new ObjectId(123), new StorageKeyExtension("txt")));
		assertTrue(patron.equals(patron2));
		assertEquals(0, patron.compareTo(patron2));

		patron2 = new LibraryPatron(new ObjectId(122), "First", "last",new EmailAddress("a@g.com"),"123-45-6789", new Day(new DateTime(123)),2,new StorageKey(new Kind("Photo"), new ObjectId(123), new StorageKeyExtension("txt")));
		assertFalse(patron.equals(patron2));
		assertEquals(1, patron.compareTo(patron2));

		patron2 = new LibraryPatron(new ObjectId(124), "First", "last",new EmailAddress("a@g.com"),"123-45-6789", new Day(new DateTime(123)),2,new StorageKey(new Kind("Photo"), new ObjectId(123), new StorageKeyExtension("txt")));
		assertFalse(patron.equals(patron2));
		assertEquals(-1, patron.compareTo(patron2));
	}
}
