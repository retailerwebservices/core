package org.jimmutable.core.objects.common;

import java.util.Collection;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class PhoneNumberDeck extends StandardImmutableObject<PhoneNumberDeck>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.common.PhoneNumberDeck"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.Collection FIELD_PHONE_NUMBERS = new FieldDefinition.Collection("phone_numbers",new FieldArrayList());
	
	private FieldArrayList<PhoneNumber> phone_numbers;
	
	public PhoneNumberDeck(PhoneNumber... numbers)
	{
		phone_numbers = new FieldArrayList();
		
		for ( int i = 0; i < numbers.length; i++ )
			phone_numbers.add(numbers[i]);
		
		complete();
	}
	
	public PhoneNumberDeck(Collection<PhoneNumber> numbers)
	{
		Validator.notNull(numbers);
		
		phone_numbers = new FieldArrayList();
		phone_numbers.addAll(numbers);
		
		complete();
	}
	
	public PhoneNumberDeck(ObjectParseTree t)
	{ 
		phone_numbers = t.getCollection(FIELD_PHONE_NUMBERS, new FieldArrayList(), ReadAs.OBJECT, OnError.SKIP);
	}

	
	public void write(ObjectWriter writer) 
	{
		writer.writeCollection(FIELD_PHONE_NUMBERS, phone_numbers, WriteAs.OBJECT);
	}
	
	public int compareTo(PhoneNumberDeck o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimplePhoneNumbers().size(), o.getSimplePhoneNumbers().size());
		
		return ret;
	}

	@Override
	public void freeze() 
	{
		phone_numbers.freeze();
	}

	@Override
	public void normalize() 
	{	
	}

	@Override
	public void validate() 
	{
		Validator.notNull(phone_numbers);
		Validator.containsNoNulls(phone_numbers);
	}

	@Override
	public int hashCode() 
	{
		return phone_numbers.hashCode();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof PhoneNumberDeck) ) return false;
		
		PhoneNumberDeck other = (PhoneNumberDeck)obj;
		
		return getSimplePhoneNumbers().equals(other.getSimplePhoneNumbers());
	}

	/**
	 * Get all the phone numbers in the deck
	 * @return The phone numbers in the deck
	 */
	public FieldArrayList<PhoneNumber> getSimplePhoneNumbers() { return phone_numbers; }
	
	/**
	 * Get the primary (first) phone number of a given type in the deck
	 * 
	 * @param type
	 *            The type of number to get
	 * @param default_value
	 *            The value to return if this deck does not have any phone
	 *            numbers of the specified type
	 * @return The primary phone number of the type specified, or default_value
	 *         if no phone number of the type specified is present.
	 */
	public PhoneNumber getOptionalPrimaryPhoneNumber(PhoneNumberType type, PhoneNumber default_value)
	{
		if ( type == null ) return default_value;
		
		for ( PhoneNumber number : phone_numbers )
		{
			if ( number.getSimpleType().equals(type) ) return number;
		}
		
		return default_value;
	}
	
	/**
	 * Get the primary (first) phone number (digits) of a given type in the deck
	 * 
	 * @param type
	 *            The type of number to get
	 * @param default_value
	 *            The value to return if this deck does not have any phone
	 *            numbers of the specified type
	 * @return The primary phone number (digits) of the type specified, or
	 *         default_value if no phone number of the type specified is
	 *         present.
	 */

	public PhoneNumberDigits getOptionalPrimaryPhoneNumberDigits(PhoneNumberType type, PhoneNumberDigits default_value)
	{
		PhoneNumber ret = getOptionalPrimaryPhoneNumber(type,null);
		if ( ret == null ) return default_value;
		return ret.getSimpleDigits();
	}
}

