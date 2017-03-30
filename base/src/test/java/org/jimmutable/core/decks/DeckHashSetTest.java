package org.jimmutable.core.decks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.book.BookDeckSet;
import org.jimmutable.core.examples.book.BookDeckSet.Builder;
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
    	List<Book> test_books = new ArrayList<>();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	
		Builder builder = new Builder();
		
		builder.addBook(test_books.get(0));
		builder.addBook(test_books.get(1));

		BookDeckSet first_library = builder.create();
		
		assertEquals(first_library.getSimpleContents().size(),2);
		
		assert(first_library.getSimpleContents().contains(test_books.get(0)));
		assert(first_library.getSimpleContents().contains(test_books.get(1)));
		
		// now test an "append" builder...
		
		builder = first_library.createBuilder();
		
		builder.addBook(test_books.get(2));
		builder.addBook(test_books.get(3));
		
		BookDeckSet second_library = builder.create();
		
		// Confirm that first library has not changed...
		assertEquals(second_library.getSimpleContents().size(),3); // because book 2 and 3 are duplicates...
		
		assert(second_library.getSimpleContents().containsAll(test_books));
		
		
		System.out.println(second_library.toJavaCode(Format.XML_PRETTY_PRINT,"obj"));
		
		System.out.println(second_library.toJavaCode(Format.JSON_PRETTY_PRINT,"obj"));
    }
    
    public void testCloneOperations()
    {
        final Book grapes_of_wrath = new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck");
        final Book of_mice_and_men = new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck");
        final Book o_lost = new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe");
        
        // Init
        Builder builder = new Builder();
        
        builder.addBook(grapes_of_wrath);
        builder.addBook(of_mice_and_men);
        
        BookDeckSet first_library = builder.create();
        
        assertEquals(first_library.getSimpleContents().size(),2);
        
        assertTrue(first_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(first_library.getSimpleContents().contains(of_mice_and_men));
        
        // Add
        BookDeckSet second_library = first_library.cloneAdd(o_lost);
        
        assertEquals(first_library.getSimpleContents().size(), 2);
        assertTrue(first_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(first_library.getSimpleContents().contains(of_mice_and_men));
        
        assertEquals(second_library.getSimpleContents().size(), 3);
        assertTrue(second_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(second_library.getSimpleContents().contains(of_mice_and_men));
        assertTrue(second_library.getSimpleContents().contains(o_lost));
        
        // Remove
        BookDeckSet third_library = second_library.cloneRemove(of_mice_and_men);
        
        assertEquals(second_library.getSimpleContents().size(), 3);
        assertTrue(second_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(second_library.getSimpleContents().contains(of_mice_and_men));
        assertTrue(second_library.getSimpleContents().contains(o_lost));
        
        assertEquals(third_library.getSimpleContents().size(), 2);
        assertTrue(third_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(third_library.getSimpleContents().contains(o_lost));
        
        // Clear
        BookDeckSet fourth_library = third_library.cloneClear();
        
        assertEquals(third_library.getSimpleContents().size(), 2);
        assertTrue(third_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(third_library.getSimpleContents().contains(o_lost));
        
        assertEquals(fourth_library.getSimpleContents().size(), 0);
        
        // AddAll
        BookDeckSet fifth_library = fourth_library.cloneAddAll(Arrays.asList(grapes_of_wrath, of_mice_and_men));
        
        assertEquals(fourth_library.getSimpleContents().size(), 0);
        
        assertEquals(fifth_library.getSimpleContents().size(), 2);
        assertTrue(fifth_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(fifth_library.getSimpleContents().contains(of_mice_and_men));
        
        assertEquals(first_library, fifth_library);
        
        // RetainAll
        BookDeckSet sixth_library = second_library.cloneRetainAll(Arrays.asList(grapes_of_wrath, o_lost));
        
        assertEquals(second_library.getSimpleContents().size(), 3);
        assertTrue(second_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(second_library.getSimpleContents().contains(of_mice_and_men));
        assertTrue(second_library.getSimpleContents().contains(o_lost));
        
        assertEquals(sixth_library.getSimpleContents().size(), 2);
        assertTrue(sixth_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(sixth_library.getSimpleContents().contains(o_lost));

        // RemoveAll
        BookDeckSet seventh_library = second_library.cloneRemoveAll(Arrays.asList(grapes_of_wrath, o_lost));
        
        assertEquals(second_library.getSimpleContents().size(), 3);
        assertTrue(second_library.getSimpleContents().contains(grapes_of_wrath));
        assertTrue(second_library.getSimpleContents().contains(of_mice_and_men));
        assertTrue(second_library.getSimpleContents().contains(o_lost));
        
        assertEquals(seventh_library.getSimpleContents().size(), 1);
        assertTrue(seventh_library.getSimpleContents().contains(of_mice_and_men));
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
    	
    	
    	List<Book> test_books = new ArrayList<>();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder();
		
		builder.addBook(test_books.get(0));
		builder.addBook(test_books.get(1));
		builder.addBook(test_books.get(2));
		
		BookDeckSet second_library = builder.create();
		
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
    	
    	
    	List<Book> test_books = new ArrayList<>();
    	
    	test_books.add(new Book("Grapes of Wrath", 1211, "33242347234", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("Of Mice and Men", 1211, "32423423711", BindingType.TRADE_PAPER_BACK, "John Steinbeck"));
    	test_books.add(new Book("O Lost", 1211, "1123234234", BindingType.TRADE_PAPER_BACK, "Thomas Wolfe"));
    	
    	Builder builder = new Builder();
		
		builder.addBook(test_books.get(0));
		builder.addBook(test_books.get(1));
		builder.addBook(test_books.get(2));
		
		BookDeckSet second_library = builder.create();
		
		assertEquals(obj,second_library);
    }
}

