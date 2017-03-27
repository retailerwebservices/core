package org.jimmutable.core.examples.book;

import java.util.Collection;
import java.util.Collections;

import org.jimmutable.core.decks.StandardImmutableSetDeck;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldSet;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a set based deck
 * 
 * @author jim.kane
 *
 */
final public class BookDeckSet extends StandardImmutableSetDeck<BookDeckSet, Book>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.BookDeckSet");
	static private final FieldName FIELD_BOOKS = new FieldName("books");
	
	private FieldSet<Book> books;
	
	public BookDeckSet()
	{
		this(Collections.EMPTY_LIST);
	}
	
	public BookDeckSet(Collection<Book> books)
	{
		super();
		
		this.books = new FieldHashSet();
		this.books.addAll(books);
		
		complete();
	}

	private BookDeckSet(Builder builder)
	{
		books = new FieldHashSet<>();
	}
	
	public BookDeckSet(ObjectParseTree t)
	{
		books = t.getCollection(FIELD_BOOKS, new FieldHashSet(), ReadAs.OBJECT, ObjectParseTree.OnError.SKIP);
	}
	
	public TypeName getTypeName() 
	{
		return TYPE_NAME;
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeCollection(FIELD_BOOKS, books, WriteAs.OBJECT);
	}
	
	public void normalize() 
	{	
	}
	
	public void validate() 
	{
		Validator.containsNoNulls(books);
		Validator.containsOnlyInstancesOf(Book.class, books);
	}
	
	public FieldSet<Book> getSimpleContents() { return books; }
	
	
	static public class Builder
	{
		private BookDeckSet under_construction;
		
		public Builder()
		{
			under_construction = new BookDeckSet(this);
		}
		
		public Builder(BookDeckSet starting_point)
		{
			under_construction = starting_point.deepMutableCloneForBuilder();
		}

		public void addBook(Book book)
		{
			if ( book == null ) return;
			under_construction.getSimpleContents().add(book);
		}
		
		public BookDeckSet create()
		{
			return under_construction.deepClone();
		}
	}
}