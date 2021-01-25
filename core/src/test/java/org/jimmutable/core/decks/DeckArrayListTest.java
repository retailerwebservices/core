package org.jimmutable.core.decks;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.book.BookDeckList;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DeckArrayListTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public DeckArrayListTest( String testName )
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		return new TestSuite(DeckArrayListTest.class);
	}

	public void testLibrary()
	{
		List<String> authors = new ArrayList();
		authors.add("John Steinbeck");

		Builder builder = new Builder(BookDeckList.TYPE_NAME);

		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, authors));
		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, authors));

		BookDeckList first_library = (BookDeckList) builder.create();

		assertTrue(first_library != null);

		assertEquals(first_library.getSimpleContents().size(), 2);

		assertEquals(first_library.getSimpleContents().get(0).getSimpleTitle(), "GRAPES OF WRATH");
		assertEquals(first_library.getSimpleContents().get(1).getSimpleTitle(), "OF MICE AND MEN");

		// now test an "append" builder...

		builder = new Builder(first_library);

		authors = new ArrayList();
		authors.add("Thomas Wolfe");

		builder.add(BookDeckList.FIELD_CONTENTS, new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, authors));

		BookDeckList second_library = (BookDeckList) builder.create();

		assertTrue(second_library != null);

		// Confirm that first library has not changed...
		assertEquals(first_library.getSimpleContents().size(), 2);

		assertEquals(first_library.getSimpleContents().get(0).getSimpleTitle(), "GRAPES OF WRATH");
		assertEquals(first_library.getSimpleContents().get(1).getSimpleTitle(), "OF MICE AND MEN");

		// And that second library was properly appended to...
		assertEquals(second_library.getSimpleContents().size(), 3);

		assertEquals(second_library.getSimpleContents().get(0).getSimpleTitle(), "GRAPES OF WRATH");
		assertEquals(second_library.getSimpleContents().get(1).getSimpleTitle(), "OF MICE AND MEN");
		assertEquals(second_library.getSimpleContents().get(2).getSimpleTitle(), "O LOST");

		// System.out.println(second_library.toJavaCode(Format.XML_PRETTY_PRINT,"obj"));

		// System.out.println(second_library.toJavaCode(Format.JSON_PRETTY_PRINT,"obj"));
	}

	public void testSerializationXML()
	{
		String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n", "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>", "  <type_hint>jimmutable.examples.BookDeckList</type_hint>", "  <books>", "    <type_hint>jimmutable.examples.Book</type_hint>", "    <title>GRAPES OF WRATH</title>", "    <page_count>1211</page_count>", "    <isbn>33242347234</isbn>", "    <binding>trade-paper-back</binding>", "    <authors>John Steinbeck</authors>", "  </books>", "  <books>", "    <type_hint>jimmutable.examples.Book</type_hint>", "    <title>OF MICE AND MEN</title>", "    <page_count>1211</page_count>", "    <isbn>32423423711</isbn>", "    <binding>trade-paper-back</binding>", "    <authors>John Steinbeck</authors>", "  </books>", "  <books>", "    <type_hint>jimmutable.examples.Book</type_hint>", "    <title>O LOST</title>", "    <page_count>1211</page_count>", "    <isbn>1123234234</isbn>", "    <binding>trade-paper-back</binding>", "    <authors>Thomas Wolfe</authors>", "  </books>", "</object>");

		BookDeckList obj = (BookDeckList) StandardObject.deserialize(obj_string);

		assertEquals(obj.getSimpleContents().size(), 3);

		assertEquals(obj.getSimpleContents().get(0).getSimpleTitle(), "GRAPES OF WRATH");
		assertEquals(obj.getSimpleContents().get(1).getSimpleTitle(), "OF MICE AND MEN");
		assertEquals(obj.getSimpleContents().get(2).getSimpleTitle(), "O LOST");

		// Full test
		Builder builder = new Builder(BookDeckList.TYPE_NAME);

		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
		builder.add(BookDeckList.FIELD_CONTENTS, new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));

		BookDeckList second_library = (BookDeckList) builder.create();

		assertEquals(second_library, obj);
	}

	public void testSerializationJSON()
	{
		String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s", "{", "  \"type_hint\" : \"jimmutable.examples.BookDeckList\",", "  \"books\" : [ {", "    \"type_hint\" : \"jimmutable.examples.Book\",", "    \"title\" : \"GRAPES OF WRATH\",", "    \"page_count\" : 1211,", "    \"isbn\" : \"33242347234\",", "    \"binding\" : \"trade-paper-back\",", "    \"authors\" : [ \"John Steinbeck\" ]", "  }, {", "    \"type_hint\" : \"jimmutable.examples.Book\",", "    \"title\" : \"OF MICE AND MEN\",", "    \"page_count\" : 1211,", "    \"isbn\" : \"32423423711\",", "    \"binding\" : \"trade-paper-back\",", "    \"authors\" : [ \"John Steinbeck\" ]", "  }, {", "    \"type_hint\" : \"jimmutable.examples.Book\",", "    \"title\" : \"O LOST\",", "    \"page_count\" : 1211,", "    \"isbn\" : \"1123234234\",", "    \"binding\" : \"trade-paper-back\",", "    \"authors\" : [ \"Thomas Wolfe\" ]", "  } ]", "}");

		BookDeckList obj = (BookDeckList) StandardObject.deserialize(obj_string);

		assertEquals(obj.getSimpleContents().size(), 3);

		assertEquals(obj.getSimpleContents().get(0).getSimpleTitle(), "GRAPES OF WRATH");
		assertEquals(obj.getSimpleContents().get(1).getSimpleTitle(), "OF MICE AND MEN");
		assertEquals(obj.getSimpleContents().get(2).getSimpleTitle(), "O LOST");

		// Full test
		Builder builder = new Builder(BookDeckList.TYPE_NAME);

		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
		builder.add(BookDeckList.FIELD_CONTENTS, new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
		builder.add(BookDeckList.FIELD_CONTENTS, new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));

		BookDeckList second_library = (BookDeckList) builder.create();

		assertEquals(second_library, obj);
	}
}
