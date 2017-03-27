package org.jimmutable.core.examples.book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * A simple example StandardImmutableObject
 * 
 * @author jim.kane
 *
 */
final public class Book extends StandardImmutableObject<Book>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.Book"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static private final FieldName FIELD_TITLE = new FieldName("title");
	static private final FieldName FIELD_PAGE_COUNT = new FieldName("page_count");
	static private final FieldName FIELD_ISBN = new FieldName("isbn");
	static private final FieldName FIELD_BINDING = new FieldName("binding");
	static private final FieldName FIELD_AUTHORS = new FieldName("authors");
	
	private String title; // required, upper-case
	private int page_count; // required, must be 0 or greater
	
	private String isbn; // optional
	
	private BindingType binding; // required

	private FieldList<String> authors;
	
	// builder constructor...
	private Book(Builder builder)
	{
		super();
		
		// building constructor.  Builder will call complete...
		this.authors = new FieldArrayList<>();
	}
	
	public Book(ObjectParseTree t)
	{
		title = t.getString(FIELD_TITLE, null);
		page_count = t.getInt(FIELD_PAGE_COUNT, -1);
		isbn = t.getString(FIELD_ISBN, null);
		binding = BindingType.fromCode(t.getString(FIELD_BINDING, null),null);
		
		authors = t.getCollection(FIELD_AUTHORS, new FieldArrayList(), ReadAs.STRING, ObjectParseTree.OnError.SKIP);
	}
	
	@Override
	public void write(ObjectWriter writer) 
	{
		writer.writeString(FIELD_TITLE, getSimpleTitle());
		writer.writeInt(FIELD_PAGE_COUNT, getSimplePageCount());
		writer.writeString(FIELD_ISBN, getOptionalISBN(null));
		writer.writeString(FIELD_BINDING, getSimpleBinding().toString());
		writer.writeCollection(FIELD_AUTHORS, getSimpleAuthors(), WriteAs.STRING);
	}
	
	// copy constructor...
	public Book(String title, int page_count, String isbn, BindingType binding, Collection<String> authors)
	{
		super();
		
		// building copy constructor.  Builder will call complete...
		this.title = title;
		this.page_count = page_count;
		this.isbn = isbn;
		this.binding = binding;
		
		this.authors = new FieldArrayList<>(authors);
		
		complete();
	}
	
	// copy constructor...
	public Book(String title, int page_count, String isbn, BindingType binding, String author)
	{
		this(title,page_count,isbn,binding,toCollection(author));
	} 
	
	static private Collection<String> toCollection(String author)
	{
		List<String> ret = new ArrayList<>();
		ret.add(author);
		return ret;
	}

	/**
	 * Normalize the book object (convert the title to upper case)
	 */
	public void normalize() 
	{
		title = Normalizer.upperCase(title);
	}
	
	/**
	 * Validate the object 
	 */
	public void validate()
	{
		Validator.notNull(title);
		Validator.min(page_count, 0);
		
		Validator.notNull(binding);
		
		Validator.containsNoNulls(authors);
	}
	
	/**
	 * Freeze the object
	 */
	public void freeze()
	{
		authors.freeze();
	}
	
	public String getSimpleTitle() { return title; } 
	public int getSimplePageCount() { return page_count; }
	
	public BindingType getSimpleBinding() { return binding; }
	public List<String> getSimpleAuthors() { return authors; }
	
	public boolean hasISBN() { return isbn != null; }
	public String getOptionalISBN(String default_value)
	{
		if ( isbn == null ) return default_value;
		return isbn;
	}

	public int compareTo(Book other) 
	{
		int ret = Comparison.startCompare();
		
		Comparison.continueCompare(ret, getSimpleTitle(), other.getSimpleTitle());
		Comparison.continueCompare(ret, getSimplePageCount(), other.getSimplePageCount());
		Comparison.continueCompare(ret, getSimpleBinding(), other.getSimpleBinding());
		Comparison.continueCompare(ret, getOptionalISBN(null), other.getOptionalISBN(null));
		Comparison.continueCompare(ret, getSimpleAuthors().size(), other.getSimpleAuthors().size());
		
		return ret;
	}

	public int hashCode() 
	{
		return Objects.hash(getSimpleTitle(), getSimplePageCount(), getOptionalISBN(null), getSimpleBinding());
	}

	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof Book) ) return false;
		
		Book other = (Book)obj;
		
		if ( !getSimpleTitle().equals(other.getSimpleTitle()) ) return false;
		if ( getSimplePageCount() != other.getSimplePageCount() ) return false;
		
		if ( !Objects.equals(isbn, other.isbn) ) return false;
		
		if ( getSimpleBinding() != other.getSimpleBinding() ) return false;
		
		if ( !getSimpleAuthors().equals(other.getSimpleAuthors()) ) return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return getSimpleTitle() + " by " + getSimpleAuthors();
	}
	
	static public class Builder
	{
		private Book under_construction;
		
		public Builder()
		{
			under_construction = new Book(this);
		}
		
		public Builder(Book starting_point)
		{
			under_construction = starting_point.deepMutableCloneForBuilder();
		}
		
		public void setTitle(String title) 
		{ 
			under_construction.title = title;
		}
		
		public void setPageCount(int count)
		{
			under_construction.page_count = count;
		}
		
		public void setISBN(String isbn)
		{
			under_construction.isbn = isbn;
		}
		
		public void addAuthor(String author)
		{
			under_construction.authors.add(author);
		}
		
		public void setBindingType(BindingType type)
		{
			under_construction.binding = type;
		}
		
		public Book create()
		{
			return under_construction.deepClone();
		}
	}
}

