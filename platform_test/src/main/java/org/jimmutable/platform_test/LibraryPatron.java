
package org.jimmutable.platform_test;

import java.util.Objects;

import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.Indexable;
import org.jimmutable.cloud.elasticsearch.SearchDocumentId;
import org.jimmutable.cloud.elasticsearch.SearchDocumentWriter;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.EmailAddress;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

/**
 * This is the class we use to manage our library patrons. 
 * This class pertains all information related to a patron and all information that would be necessary to manage their interactions with a library. 
 * @author andrew.towe
 *
 */

final public class LibraryPatron extends StandardImmutableObject<LibraryPatron> implements Indexable, Storable
{
	static public final TypeName TYPE_NAME = new TypeName("LibraryPatron");

	static public final FieldDefinition.Stringable<ObjectId> FIELD_OBJECT_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static public final FieldDefinition.String FIELD_FIRST_NAME = new FieldDefinition.String("first_name", null);
	static public final FieldDefinition.String FIELD_LAST_NAME = new FieldDefinition.String("last_name", null);
	static public final FieldDefinition.Stringable<Day> FIELD_BIRTH_DATE = new FieldDefinition.Stringable<Day>("birth_date", null, Day.CONVERTER);
	static public final FieldDefinition.String FIELD_EMAIL_ADDRESS = new FieldDefinition.String("email_address", null);
	static public final FieldDefinition.String FIELD_SSN = new FieldDefinition.String("ssn", null);
	static public final FieldDefinition.Integer FIELD_NUM_BOOKS = new FieldDefinition.Integer("number_of_books_checked_out", -1);
	static public final FieldDefinition.Stringable<StorageKey> FIELD_AVATAR = new FieldDefinition.Stringable<StorageKey>("avatar", null, StorageKey.CONVERTER);

	private ObjectId id; // required
	private String first_name; // required
	private String last_name; // required
	private Day birth_date; // optional

	private EmailAddress email_address; // optional
	private String ssn; // optional
	private Integer number_of_books_checked_out; // required and > 0

	private StorageKey picture; // optional

	public LibraryPatron( ObjectId id, String first_name, String last_name, EmailAddress email_address, String ssn, Day birth_date, int number_of_books_checked_out, StorageKey picture )
	{
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email_address = email_address;
		this.ssn = ssn;
		this.birth_date = birth_date;
		this.number_of_books_checked_out = number_of_books_checked_out;
		this.picture = picture;
		complete();
	}

	public LibraryPatron( ObjectParseTree t )
	{
		this.id = t.getStringable(FIELD_OBJECT_ID);
		this.first_name = t.getString(FIELD_FIRST_NAME);
		this.last_name = t.getString(FIELD_LAST_NAME);
		this.birth_date = t.getStringable(FIELD_BIRTH_DATE);
		this.email_address = (EmailAddress) t.getObject(FIELD_EMAIL_ADDRESS);
		this.ssn = t.getString(FIELD_SSN);
		this.number_of_books_checked_out = t.getInt(FIELD_NUM_BOOKS);
		this.picture = t.getStringable(FIELD_AVATAR);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, first_name, last_name, birth_date, email_address, ssn, number_of_books_checked_out, picture);
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
		{
			return true;
		}
		if ( obj == null )
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}

		/**
		 * CODE REVIEW: 
		 * 
		 * This equals code is still not right,
		 * 
		 * You can simply do:  if ( !Objects.equals(getOptionalBirthDate(null), ohter.getOptionalBirthDate(null) ) return false;
		 * 
		 * Please fix.
		 */
		LibraryPatron other = (LibraryPatron) obj;
		if ( getOptionalBirthDate(null) == null )
		{
			if ( !Objects.equals(other.getOptionalBirthDate(null), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getOptionalBirthDate(null), other.getOptionalBirthDate(null)) )
		{
			return false;
		}
		if ( Objects.equals(getOptionalEmailAddress(null), null) )
		{
			if ( !Objects.equals(other.getOptionalEmailAddress(null), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getOptionalEmailAddress(null), other.getOptionalEmailAddress(null)) )
		{
			return false;
		}
		if ( Objects.equals(getSimpleFirstName(), null) )
		{
			if ( !Objects.equals(other.getSimpleFirstName(), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getSimpleFirstName(), other.getSimpleFirstName()) )
		{
			return false;
		}
		if ( Objects.equals(getSimpleObjectId(), null) )
		{
			if ( !Objects.equals(other.getSimpleObjectId(), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getSimpleObjectId(), other.getSimpleObjectId()) )
		{
			return false;
		}

		if ( Objects.equals(getSimpleLastName(), null) )

		{
			if ( !Objects.equals(other.getSimpleLastName(), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getSimpleLastName(), other.getSimpleLastName()) )
		{
			return false;
		}
		if ( Objects.equals(getSimpleNumberOfBooksCheckedOut(), null) )
		{
			if ( !Objects.equals(other.getSimpleNumberOfBooksCheckedOut(), null) )
				return false;
		}
		else if ( !Objects.equals(getSimpleNumberOfBooksCheckedOut(), other.getSimpleNumberOfBooksCheckedOut()) )
			return false;
		if ( Objects.equals(getOptionalSSN(null), null) )
		{
			if ( !Objects.equals(other.getOptionalSSN(null), null) )
				return false;
		}
		else if ( !Objects.equals(getOptionalSSN(null), other.getOptionalSSN(null)) )
		{
			return false;
		}
		if ( Objects.equals(getOptionalPicture(null), null) )
		{
			if ( !Objects.equals(other.getOptionalPicture(null), null) )
			{
				return false;
			}
		}
		else if ( !Objects.equals(getOptionalPicture(null), other.getOptionalPicture(null)) )
		{
			return false;
		}
		return true;
	}

	public String getSimpleFirstName()
	{
		return first_name;
	}

	public String getSimpleLastName()
	{
		return last_name;
	}

	public Day getOptionalBirthDate( Day default_value )
	{
		return Optional.getOptional(birth_date, null, default_value);
	}

	public boolean hasBirthDate()
	{
		return birth_date != null;
	}

	public EmailAddress getOptionalEmailAddress( EmailAddress default_value )
	{
		return Optional.getOptional(email_address, null, default_value);
	}

	public boolean hasEmailAddress()
	{
		return email_address != null;
	}

	public String getOptionalSSN( String default_value )
	{
		return Optional.getOptional(ssn, null, default_value);
	}

	public boolean hasSSN()
	{
		return ssn != null;
	}

	public Integer getSimpleNumberOfBooksCheckedOut()
	{
		return number_of_books_checked_out;
	}

	public StorageKey getOptionalPicture( StorageKey default_value )
	{
		return Optional.getOptional(picture, null, default_value);
	}

	@Override
	public String serialize( Format format )
	{
		return ObjectWriter.serialize(format, this);
	}

	@Override
	public IndexDefinition getSimpleSearchIndexDefinition()
	{
		return null;
	}

	@Override
	public SearchDocumentId getSimpleSearchDocumentId()
	{
		return new SearchDocumentId(id.getSimpleValue());
	}

	@Override
	public void writeSearchDocument( SearchDocumentWriter writer )
	{
		writer.writeObjectId(FIELD_OBJECT_ID.getSimpleFieldName(), id);
		writer.writeText(FIELD_FIRST_NAME.getSimpleFieldName(), first_name);
		writer.writeText(FIELD_LAST_NAME.getSimpleFieldName(), last_name);
		writer.writeText(FIELD_EMAIL_ADDRESS.getSimpleFieldName(), email_address.getSimpleValue());
		writer.writeTextWithSubstringMatchingSupport(FIELD_SSN.getSimpleFieldName(), ssn);
		writer.writeDay(FIELD_BIRTH_DATE.getSimpleFieldName(), birth_date);
		writer.writeLong(FIELD_NUM_BOOKS.getSimpleFieldName(), number_of_books_checked_out);
		writer.writeAtom(FIELD_AVATAR.getSimpleFieldName(), picture.getSimpleValue());
	}

	@Override
	public int compareTo( LibraryPatron other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleObjectId(), other.getSimpleObjectId());
		ret = Comparison.continueCompare(ret, getSimpleFirstName(), other.getSimpleFirstName());
		ret = Comparison.continueCompare(ret, getSimpleLastName(), other.getSimpleLastName());
		ret = Comparison.continueCompare(ret, getOptionalEmailAddress(null), other.getOptionalEmailAddress(null));
		ret = Comparison.continueCompare(ret, getOptionalSSN(null), other.getOptionalSSN(null));
		ret = Comparison.continueCompare(ret, getOptionalBirthDate(null), other.getOptionalBirthDate(null));
		ret = Comparison.continueCompare(ret, getSimpleNumberOfBooksCheckedOut(), other.getSimpleNumberOfBooksCheckedOut());
		ret = Comparison.continueCompare(ret, getOptionalPicture(null), other.getOptionalPicture(null));

		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeObject(FIELD_OBJECT_ID.getSimpleFieldName(), id.getSimpleValue());
		writer.writeString(FIELD_FIRST_NAME.getSimpleFieldName(), first_name);
		writer.writeString(FIELD_LAST_NAME.getSimpleFieldName(), last_name);
		writer.writeStringable(FIELD_EMAIL_ADDRESS.getSimpleFieldName(), email_address);
		writer.writeString(FIELD_SSN.getSimpleFieldName(), ssn);
		writer.writeStringable(FIELD_BIRTH_DATE.getSimpleFieldName(), birth_date);
		writer.writeLong(FIELD_NUM_BOOKS.getSimpleFieldName(), number_of_books_checked_out);
		writer.writeObject(FIELD_AVATAR.getSimpleFieldName(), picture.getSimpleValue());
	}

	@Override
	public void freeze()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void normalize()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void validate()
	{
		Validator.notNull(id, first_name, last_name, email_address, number_of_books_checked_out);
		Validator.min(number_of_books_checked_out,0);
	}

	@Override
	public Kind getSimpleKind()
	{
		return new Kind("library-patron");
	}

	@Override
	public ObjectId getSimpleObjectId()
	{
		return id;
	}
}