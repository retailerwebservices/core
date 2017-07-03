package org.jimmutable.core.examples.book;

import java.util.Collection;
import java.util.Collections;

import org.jimmutable.core.decks.StandardImmutableSetDeck;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldSet;
import org.jimmutable.core.serialization.FieldDefinition;
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
	static public final FieldDefinition.Collection FIELD_BOOKS = new FieldDefinition.Collection("books", new FieldHashSet());
	
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
}