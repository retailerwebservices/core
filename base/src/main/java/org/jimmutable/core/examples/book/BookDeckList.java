package org.jimmutable.core.examples.book;

import java.util.Collection;
import java.util.Collections;

import org.jimmutable.core.decks.StandardImmutableListDeck;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a list based deck
 * 
 * @author jim.kane
 *
 */

final public class BookDeckList extends StandardImmutableListDeck<BookDeckList, Book>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.BookDeckList");
	static private final FieldName FIELD_BOOKS = new FieldName("books");
	
	private FieldList<Book> books;
	
	public BookDeckList()
	{
		this(Collections.emptyList());
	}
	
	public BookDeckList(Collection<Book> books)
	{
		super();
		
		this.books = new FieldArrayList<>();
		this.books.addAll(books);
		
		complete();
	}
	
	private BookDeckList(Builder builder)
	{
		books = new FieldArrayList<>();
	}
	
	public BookDeckList(ObjectParseTree t)
	{
		books = t.getCollection(FIELD_BOOKS, new FieldArrayList<>(), ReadAs.OBJECT, ObjectParseTree.OnError.SKIP);
	}
	
	public TypeName getTypeName() 
	{
		return TYPE_NAME;
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeCollection(FIELD_BOOKS, books, WriteAs.OBJECT);
	}

	@Override
	public FieldList<Book> getSimpleContents()
	{
		return books;
	}
	
	public void normalize() 
	{
	}
	
	public void validate() 
	{
		Validator.containsNoNulls(getSimpleContents());
		Validator.containsOnlyInstancesOf(Book.class, getSimpleContents());
	}
    
    @Override
    public Builder getBuilder()
    {
        return new Builder(this);
    }

	
	static public class Builder extends StandardImmutableListDeck.Builder<BookDeckList, Book>
	{
		public Builder()
		{
			under_construction = new BookDeckList(this);
		}
		
		public Builder(BookDeckList starting_point)
		{
			super(starting_point);
		}

		public void addBook(Book book)
		{
			if ( book == null ) return;
			under_construction.getSimpleContents().add(book);
		}
	}
}
