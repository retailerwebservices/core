package org.jimmutable.core.examples.book;

import java.util.Collections;
import java.util.Map;

import org.jimmutable.core.decks.StandardImmutableMapDeck;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a map based deck
 * 
 * @author jim.kane
 *
 */
final public class BookDeckMap extends StandardImmutableMapDeck<BookDeckMap, String, Book>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.BookDeckMap");
	static public final FieldDefinition.Map FIELD_BOOKS = new FieldDefinition.Map("books", new FieldHashMap());
	
	private FieldMap<String,Book> books;
	
	public BookDeckMap()
	{
		//TODO Trevor
		this(Collections.<String, Book>emptyMap());
	}
	
	public BookDeckMap(Map<String,Book> initial_contents)
	{
		super();
		
		books = new FieldHashMap<>();
		
		if ( initial_contents != null )
		{
			this.books.putAll(initial_contents);
		}
		
		complete();
	}
	
	public BookDeckMap(ObjectParseTree t)
	{
		books = t.getMap(FIELD_BOOKS, new FieldHashMap(), ReadAs.STRING, ReadAs.OBJECT, ObjectParseTree.OnError.SKIP);
	}
	
	public TypeName getTypeName() 
	{
		return TYPE_NAME;
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeMap(FIELD_BOOKS, books, WriteAs.STRING, WriteAs.OBJECT);
	}
	

	public FieldMap<String,Book> getSimpleContents() { return books; }

	public void normalize() 
	{
	}
	
	public void validate() 
	{
		Validator.containsOnlyInstancesOf(String.class, Book.class, books);
	}
}

