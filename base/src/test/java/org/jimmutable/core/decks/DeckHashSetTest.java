package org.jimmutable.core.decks;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.book.BookDeckSet;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DeckHashSetTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DeckHashSetTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	JimmutableTypeNameRegister.registerAllTypes();
        return new TestSuite( DeckHashSetTest.class );
    }

    public void testBookSet()
    {
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	
		Builder builder = new Builder(BookDeckSet.TYPE_NAME);
		
		builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(0));
		builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(1));

		BookDeckSet first_library = (BookDeckSet)builder.create(null);
		
		assertEquals(first_library.getSimpleContents().size(),2);
		
		assert(first_library.getSimpleContents().contains(test_books.get(0)));
		assert(first_library.getSimpleContents().contains(test_books.get(1)));
		
		// now test an "append" builder...
		
		builder = new Builder(first_library);
		
		builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(2));
		builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(3));
		
		BookDeckSet second_library = (BookDeckSet)builder.create(null);
		
		// Confirm that first library has not changed...
		assertEquals(second_library.getSimpleContents().size(),3); // because book 2 and 3 are duplicates...
		
		assert(second_library.getSimpleContents().containsAll(test_books));
		
		
		System.out.println(second_library.toJavaCode(Format.XML_PRETTY_PRINT,"obj"));
		
		System.out.println(second_library.toJavaCode(Format.JSON_PRETTY_PRINT,"obj"));
    }
    


    public void testSerializationXML()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
    		     , "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
    		     , "  <type_hint>jimmutable.examples.BookDeckSet</type_hint>"
    		     , "  <books>"
    		     , "    <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "    <title>OF MICE AND MEN</title>"
    		     , "    <page_count>1211</page_count>"
    		     , "    <isbn>32423423711</isbn>"
    		     , "    <binding>trade-paper-back</binding>"
    		     , "    <authors>John Steinbeck</authors>"
    		     , "  </books>"
    		     , "  <books>"
    		     , "    <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "    <title>O LOST</title>"
    		     , "    <page_count>1211</page_count>"
    		     , "    <isbn>1123234234</isbn>"
    		     , "    <binding>trade-paper-back</binding>"
    		     , "    <authors>Thomas Wolfe</authors>"
    		     , "  </books>"
    		     , "  <books>"
    		     , "    <type_hint>jimmutable.examples.Book</type_hint>"
    		     , "    <title>GRAPES OF WRATH</title>"
    		     , "    <page_count>1211</page_count>"
    		     , "    <isbn>33242347234</isbn>"
    		     , "    <binding>trade-paper-back</binding>"
    		     , "    <authors>John Steinbeck</authors>"
    		     , "  </books>"
    		     , "</object>"
    		);

    		BookDeckSet obj = (BookDeckSet)StandardObject.deserialize(obj_string);
    	
    	
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder(BookDeckSet.TYPE_NAME);
		
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(0));
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(1));
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(2));
		
		BookDeckSet second_library = (BookDeckSet)builder.create(null);
		
		assertEquals(obj,second_library);
    }
    
    public void testSerializationJSON()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s"
    		     , "{"
    		     , "  \"type_hint\" : \"jimmutable.examples.BookDeckSet\","
    		     , "  \"books\" : [ {"
    		     , "    \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "    \"title\" : \"O LOST\","
    		     , "    \"page_count\" : 1211,"
    		     , "    \"isbn\" : \"1123234234\","
    		     , "    \"binding\" : \"trade-paper-back\","
    		     , "    \"authors\" : [ \"Thomas Wolfe\" ]"
    		     , "  }, {"
    		     , "    \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "    \"title\" : \"GRAPES OF WRATH\","
    		     , "    \"page_count\" : 1211,"
    		     , "    \"isbn\" : \"33242347234\","
    		     , "    \"binding\" : \"trade-paper-back\","
    		     , "    \"authors\" : [ \"John Steinbeck\" ]"
    		     , "  }, {"
    		     , "    \"type_hint\" : \"jimmutable.examples.Book\","
    		     , "    \"title\" : \"OF MICE AND MEN\","
    		     , "    \"page_count\" : 1211,"
    		     , "    \"isbn\" : \"32423423711\","
    		     , "    \"binding\" : \"trade-paper-back\","
    		     , "    \"authors\" : [ \"John Steinbeck\" ]"
    		     , "  } ]"
    		     , "}"
    		);

    		BookDeckSet obj = (BookDeckSet)StandardObject.deserialize(obj_string);
    	
    	
    	List<Book> test_books = new ArrayList();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder(BookDeckSet.TYPE_NAME);
		
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(0));
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(1));
    	builder.add(BookDeckSet.FIELD_BOOKS,test_books.get(2));
		
		BookDeckSet second_library = (BookDeckSet)builder.create(null);
		
		assertEquals(obj,second_library);
    }
}

