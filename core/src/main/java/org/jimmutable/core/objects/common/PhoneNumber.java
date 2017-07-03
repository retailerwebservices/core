package org.jimmutable.core.objects.common;

import java.util.Objects;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * Class that encapsulates a phone number (digits + type)
 * 
 * @author jim.kane
 *
 */
public class PhoneNumber extends StandardImmutableObject<PhoneNumber>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.common.PhoneNumber"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.Stringable<PhoneNumberDigits> FIELD_DIGITS = new FieldDefinition.Stringable("digits",null, PhoneNumberDigits.CONVERTER);
	static public final FieldDefinition.Enum<PhoneNumberType> FIELD_TYPE = new FieldDefinition.Enum("type",null, PhoneNumberType.CONVERTER);
	
	private PhoneNumberDigits digits; // required
	private PhoneNumberType type; // required
	
	public PhoneNumber(String digits, PhoneNumberType type)
	{
		this(new PhoneNumberDigits(digits), type);
	}
	
	public PhoneNumber(PhoneNumberDigits digits, PhoneNumberType type)
	{
		this.digits = digits;
		this.type = type;
		complete();
	}
	
	public PhoneNumber(ObjectParseTree t)
	{ 
		this.digits = t.getStringable(FIELD_DIGITS);
		this.type = t.getEnum(FIELD_TYPE);
	}

	public PhoneNumberType getSimpleType() { return type; }
	public PhoneNumberDigits getSimpleDigits() { return digits; }
	
	
	public int compareTo(PhoneNumber o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimpleType(), o.getSimpleType());
		ret = Comparison.continueCompare(ret, getSimpleDigits(), o.getSimpleDigits());
		
		
		return ret;
	}

	@Override
	public void write(ObjectWriter writer) 
	{
		writer.writeStringable(FIELD_DIGITS, getSimpleDigits());
		writer.writeEnum(FIELD_TYPE, type);
	}

	
	public void freeze() {}
	public void normalize() {}

	
	public void validate() 
	{
		Validator.notNull(digits, type);
	}

	public int hashCode() 
	{
		return Objects.hash(getSimpleDigits(), getSimpleType());
	}

	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof PhoneNumber) ) return false;
		
		PhoneNumber other = (PhoneNumber)obj;
		
		if ( !getSimpleType().equals(other.getSimpleType()) ) return false;
		if ( !getSimpleDigits().equals(other.getSimpleDigits()) ) return false;
		
		return true;
	}
}
