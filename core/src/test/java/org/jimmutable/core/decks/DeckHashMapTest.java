package org.jimmutable.core.decks;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.book.BookDeckMap;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DeckHashMapTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DeckHashMapTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	JimmutableTypeNameRegister.registerAllTypes();
        return new TestSuite( DeckHashMapTest.class );
    }

    public void testBookSet()
    {
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
		Builder builder = new Builder(BookDeckMap.TYPE_NAME);
		
		builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_first_book",test_books.get(0));
		builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_second_book",test_books.get(1));

		BookDeckMap first_library = (BookDeckMap)builder.create(null);
		
		assertTrue(first_library != null);
		
		assertEquals(first_library.getSimpleContents().size(),2);
		
		assertTrue(first_library.getSimpleContents().containsKey("jim_first_book"));
		assertTrue(first_library.getSimpleContents().containsKey("jim_second_book"));
		
		// now test an "append" builder...
		
		builder = new Builder(first_library);
		
		builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_first_book",test_books.get(2));
		builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_third_book",test_books.get(3));
		
		BookDeckMap second_library = (BookDeckMap)builder.create(null);
		
		assertTrue(second_library != null);
		
		// Confirm that first library has not changed...
		assertEquals(second_library.getSimpleContents().size(),3); 
		
		System.out.println(second_library.toJavaCode(Format.XML_PRETTY_PRINT,"obj"));
		
		System.out.println(second_library.toJavaCode(Format.JSON_PRETTY_PRINT,"obj"));
    }
    
    public void testSerializationXML()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
    		     , "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
    		     , "  <type_hint>jimmutable.examples.BookDeckMap</type_hint>"
    		     , "  <books>"
    		     , "    <type_hint>MapEntry</type_hint>"
    		     , "    <key>jim_second_book</key>"
    		     , "    <value>"
    		     , "      <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "      <title>OF MICE AND MEN</title>"
    		     , "      <page_count>1211</page_count>"
    		     , "      <isbn>32423423711</isbn>"
    		     , "      <binding>trade-paper-back</binding>"
    		     , "      <authors>John Steinbeck</authors>"
    		     , "    </value>"
    		     , "  </books>"
    		     , "  <books>"
    		     , "    <type_hint>MapEntry</type_hint>"
    		     , "    <key>jim_third_book</key>"
    		     , "    <value>"
    		     , "      <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "      <title>O LOST</title>"
    		     , "      <page_count>1211</page_count>"
    		     , "      <isbn>1123234234</isbn>"
    		     , "      <binding>trade-paper-back</binding>"
    		     , "      <authors>Thomas Wolfe</authors>"
    		     , "    </value>"
    		     , "  </books>"
    		     , "  <books>"
    		     , "    <type_hint>MapEntry</type_hint>"
    		     , "    <key>jim_first_book</key>"
    		     , "    <value>"
    		     , "      <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "      <title>O LOST</title>"
    		     , "      <page_count>1211</page_count>"
    		     , "      <isbn>1123234234</isbn>"
    		     , "      <binding>trade-paper-back</binding>"
    		     , "      <authors>Thomas Wolfe</authors>"
    		     , "    </value>"
    		     , "  </books>"
    		     , "</object>"
    		);

    		BookDeckMap obj = (BookDeckMap)StandardObject.deserialize(obj_string);
    	
    	assertEquals(obj.getSimpleContents().size(),3); 
    	assertTrue(obj.getSimpleContents().containsKey("jim_first_book"));
    	assertTrue(obj.getSimpleContents().containsKey("jim_second_book"));
    	assertTrue(obj.getSimpleContents().containsKey("jim_third_book"));
    	
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder(BookDeckMap.TYPE_NAME);
    	
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_first_book",test_books.get(2));
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_second_book",test_books.get(1));
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_third_book",test_books.get(3));
    	
    	BookDeckMap second_library = (BookDeckMap)builder.create(null);
    	
    	assertEquals(obj,second_library);
    }
    
    public void testSerializationJSON()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s"
    		     , "{"
    		     , "  \"type_hint\" : \"jimmutable.examples.BookDeckMap\","
    		     , "  \"books\" : [ {"
    		     , "    \"type_hint\" : \"MapEntry\","
    		     , "    \"key\" : \"jim_second_book\","
    		     , "    \"value\" : {"
    		     , "      \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "      \"title\" : \"OF MICE AND MEN\","
    		     , "      \"page_count\" : 1211,"
    		     , "      \"isbn\" : \"32423423711\","
    		     , "      \"binding\" : \"trade-paper-back\","
    		     , "      \"authors\" : [ \"John Steinbeck\" ]"
    		     , "    }"
    		     , "  }, {"
    		     , "    \"type_hint\" : \"MapEntry\","
    		     , "    \"key\" : \"jim_third_book\","
    		     , "    \"value\" : {"
    		     , "      \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "      \"title\" : \"O LOST\","
    		     , "      \"page_count\" : 1211,"
    		     , "      \"isbn\" : \"1123234234\","
    		     , "      \"binding\" : \"trade-paper-back\","
    		     , "      \"authors\" : [ \"Thomas Wolfe\" ]"
    		     , "    }"
    		     , "  }, {"
    		     , "    \"type_hint\" : \"MapEntry\","
    		     , "    \"key\" : \"jim_first_book\","
    		     , "    \"value\" : {"
    		     , "      \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "      \"title\" : \"O LOST\","
    		     , "      \"page_count\" : 1211,"
    		     , "      \"isbn\" : \"1123234234\","
    		     , "      \"binding\" : \"trade-paper-back\","
    		     , "      \"authors\" : [ \"Thomas Wolfe\" ]"
    		     , "    }"
    		     , "  } ]"
    		     , "}"
    		);

    		BookDeckMap obj = (BookDeckMap)StandardObject.deserialize(obj_string);
    	
    	assertEquals(obj.getSimpleContents().size(),3); 
    	assertTrue(obj.getSimpleContents().containsKey("jim_first_book"));
    	assertTrue(obj.getSimpleContents().containsKey("jim_second_book"));
    	assertTrue(obj.getSimpleContents().containsKey("jim_third_book"));
    	
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder(BookDeckMap.TYPE_NAME);
    	
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_first_book",test_books.get(2));
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_second_book",test_books.get(1));
    	builder.addMapEntry(BookDeckMap.FIELD_BOOKS,"jim_third_book",test_books.get(3));
    	
    	BookDeckMap second_library = (BookDeckMap)builder.create(null);
    	
    	assertEquals(obj,second_library);
    }
}
