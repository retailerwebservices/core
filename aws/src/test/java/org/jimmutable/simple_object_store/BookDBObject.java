package org.jimmutable.simple_object_store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.aws.simple_object_store.SimpleObjectStorable;
import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.ObjectID;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * This is a modified version of Book (org.jimmutable.core.examples.book) that
 * is suitable for use in a simple object store test
 * 
 * @author jim.kane
 *
 */
public class BookDBObject extends StandardImmutableObject<BookDBObject> implements SimpleObjectStorable
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.BookDBObject"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.Stringable<ObjectID> FIELD_ID = new FieldDefinition.Stringable("id", null, ObjectID.CONVERTER);
	
	static public final FieldDefinition.String FIELD_TITLE = new FieldDefinition.String("title",null);
	static public final FieldDefinition.Integer FIELD_PAGE_COUNT = new FieldDefinition.Integer("page_count",-1);
	static public final FieldDefinition.String FIELD_ISBN = new FieldDefinition.String("isbn",null);
	static public final FieldDefinition.Enum<BindingType> FIELD_BINDING = new FieldDefinition.Enum("binding", null, BindingType.CONVERTER);
	static public final FieldDefinition.Collection FIELD_AUTHORS = new FieldDefinition.Collection("authors", new FieldArrayList());
	
	private ObjectID id;
	private String title; // required, upper-case
	private int page_count; // required, must be 0 or greater
	
	private String isbn; // optional
	
	private BindingType binding; // required

	private FieldList<String> authors;
	
	public BookDBObject(ObjectParseTree t)
	{
		id = t.getStringable(FIELD_ID);
		title = t.getString(FIELD_TITLE);
		page_count = t.getInt(FIELD_PAGE_COUNT);
		isbn = t.getString(FIELD_ISBN);
		
		binding = t.getEnum(FIELD_BINDING);
		
		authors = t.getCollection(FIELD_AUTHORS, new FieldArrayList(), ReadAs.STRING, ObjectParseTree.OnError.SKIP);
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeStringable(FIELD_ID, getSimpleObjectID());
		writer.writeString(FIELD_TITLE, getSimpleTitle());
		writer.writeInt(FIELD_PAGE_COUNT, getSimplePageCount());
		writer.writeString(FIELD_ISBN, getOptionalISBN(null));
		writer.writeEnum(FIELD_BINDING, binding);
		writer.writeCollection(FIELD_AUTHORS, getSimpleAuthors(), WriteAs.STRING);
	}
	
	// copy constructor...
	public BookDBObject(ObjectID id, String title, int page_count, String isbn, BindingType binding, String author)
	{
		super();
		
		// building copy constructor.  Builder will call complete...
		this.id = id;
		this.title = title;
		this.page_count = page_count;
		this.isbn = isbn;
		this.binding = binding;
		
		this.authors = new FieldArrayList();
		this.authors.add(author);
		
		complete();
	}
	
	static private Collection<String> toCollection(String author)
	{
		List<String> ret = new ArrayList();
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
		Validator.notNull(id);
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
	
	public ObjectID getSimpleObjectID() { return this.id; }
	
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

	public int compareTo(BookDBObject other) 
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
		if ( !(obj instanceof BookDBObject) ) return false;
		
		BookDBObject other = (BookDBObject)obj;
		
		if ( !getSimpleObjectID().equals(other.getSimpleObjectID()) ) return false;
		
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

	public S3Path getStorableS3Path() 
	{
		return new S3Path(String.format("/books/%s.xml", getSimpleObjectID().getSimpleValue()));
	}
}
