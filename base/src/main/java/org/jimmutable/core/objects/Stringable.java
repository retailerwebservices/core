package org.jimmutable.core.objects;

import org.jimmutable.core.exceptions.ImmutableException;
import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Normalizer;

/**
 * When you have a class that is a thin veil over a String (for example,
 * BrandCode, a key, etc.) Stringable is your ticket.
 * 
 * You write Strinable(s) as String(s). For example:
 * 
 * writer.writeString(FIELD_BRAND_CODE, brand_code.getSimpleValue())
 * 
 * for convenience, ObjectWriter provides a writeStringable method, so you can
 * write:
 * 
 * writer.writeStringable(FIELD_BRAND_CODE, brand_code)
 * 
 * instead and it is equivilent
 * 
 * You read Stringable(s) as String(s). For example:
 * 
 * brand_code = new BrandCode(reader.getString(FIELD_BRAND_CODE,null));
 * 
 * Attempting to read or write a Stringable as an a object will result in a
 * SerializeException being thrown
 * 
 * Stringable's DO NOT (and should not) be registered with
 * ObjectReader.registerTypeName (doing so will cause an exception to be thrown)
 * 
 * @author jim.kane
 *
 */

abstract public class Stringable extends StandardImmutableObject<Stringable>
{
	static public TypeName TYPE_NAME = new TypeName("string"); public TypeName getTypeName() { return TYPE_NAME; }

	private String value;
	
	public Stringable(String value)
	{
		this.value = value;
		complete();
	}

	public Stringable(ObjectParseTree reader)
	{
		throw new SerializeException("Attempt to read a stringable object using asObject. Should always be read using getString");
	}
	
	public void write(ObjectWriter writer)
	{
		throw new SerializeException("Attempt to write a stringable object using writeObject. Should always be written using writer.writeString");
	}

	
	public int compareTo(Stringable o) 
	{
		return getSimpleValue().compareTo(o.getSimpleValue());
	}

	
	public void freeze() {}
	
	public int hashCode()  
	{
		return value.hashCode();
	}

	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof Stringable) ) return false;
		
		Stringable other = (Stringable)obj;
		
		return getSimpleValue().equals(other.getSimpleValue());
	}
	
	final public String getSimpleValue() { return value; }
	final public String toString() { return value; }
	
	final protected void setValue(String new_value)
	{
		if ( isComplete() ) throw new ImmutableException("Attempt to set the value of a Stringable after the object was frozen.");
		this.value = new_value;
	}
	
	final protected void normalizeTrim() 
	{
		setValue(Normalizer.trim(getSimpleValue()));
	}
	
	final protected void normalizeUpperCase()
	{
		setValue(Normalizer.upperCase(getSimpleValue()));
	}
	
	final protected void normalizeLowerCase()
	{
		setValue(Normalizer.lowerCase(getSimpleValue()));
	}
}
