package org.jimmutable.core.serialization;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Validator;

/**
 * A nice encapsulation of a TypeName. TypeName(s) may only contain letters,
 * numbers, underscores, periods (.) and dollar signs ($). This matches legal
 * Java class names, BTW.
 * 
 * @author jim.kane
 *
 */

public class TypeName extends StandardImmutableObject
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.TypeName");
	static private final FieldName FIELD_NAME = new FieldName("name");
	
	static public TypeName TYPE_NAME_OBJECT = new TypeName("object");
	static public TypeName TYPE_NAME_STRING = new TypeName("string");
	
	static public TypeName TYPE_NAME_BOOLEAN = new TypeName("boolean");
	static public TypeName TYPE_NAME_CHAR = new TypeName("char");
	static public TypeName TYPE_NAME_BYTE = new TypeName("byte");
	static public TypeName TYPE_NAME_SHORT = new TypeName("short");
	static public TypeName TYPE_NAME_INT = new TypeName("int");
	static public TypeName TYPE_NAME_LONG = new TypeName("long");
	static public TypeName TYPE_NAME_FLOAT = new TypeName("float");
	static public TypeName TYPE_NAME_DOUBLE = new TypeName("double");
	
	static public TypeName TYPE_NAME_NULL = new TypeName("null");
	
	static public TypeName TYPE_NAME_MAP_ENTRY = new TypeName("MapEntry");
	
	private String name; 
	
	public TypeName(String name)
	{ 
		if ( name == null ) name = "";
		this.name = name;
		
		complete();
	}
	
	public TypeName(ObjectParseTree t)
	{
		name = t.getString(FIELD_NAME, null);
	}
	
	public TypeName getTypeName() { return TYPE_NAME; }

	public void write(ObjectWriter writer) 
	{
		writer.writeString(FIELD_NAME, getSimpleName());
	}

	public String getSimpleName() { return name; }

	public int compareTo(Object o)  
	{
		if ( !(o instanceof TypeName) ) return -1;
		
		TypeName other = (TypeName)o;
		
		return getSimpleName().compareTo(other.getSimpleName());
	}

	
	public void normalize()
	{
	}

	
	public void validate() 
	{
		Validator.notNull(name);
		
		char chars[] = name.toCharArray(); 
		
		if ( chars.length == 0 )
			throw new ValidationException("Type names must contain at least one letter");
	
		for ( int i = 0; i < chars.length; i++ )
		{
			char ch = chars[i];
			
			if ( i == 0 )
			{
				if ( ch >= 'a' && ch <= 'z') continue;
				if ( ch >= 'A' && ch <= 'Z') continue;
				throw new ValidationException("Type names must start with a letter");
			}
			else
			{
				if ( ch >= 'a' && ch <= 'z') continue;
				if ( ch >= 'A' && ch <= 'Z') continue;
				if ( ch >= '0' && ch <= '9') continue;
				if ( ch == '_' ) continue;
				if ( ch == '.' ) continue;
				if ( ch == '$' ) continue;
				
				throw new ValidationException(String.format("Illegal character '%c' in type name \"%s\"", ch, name));
			}
		}
	}
	
	public void freeze() {}
	
	public int hashCode() 
	{
		return getSimpleName().hashCode();
	}

	
	public boolean equals(Object o) 
	{
		if ( !(o instanceof TypeName) ) return false;
		
		TypeName other = (TypeName)o;
		return name.equals(other.name);
	}
	
	public boolean isPrimative()
	{
		if ( equals(TYPE_NAME_STRING) ) return true;
		if ( equals(TYPE_NAME_NULL) ) return true;
		
		if ( equals(TYPE_NAME_BOOLEAN) ) return true;
		if ( equals(TYPE_NAME_CHAR) ) return true;
		if ( equals(TYPE_NAME_BYTE) ) return true;
		if ( equals(TYPE_NAME_SHORT) ) return true;
		if ( equals(TYPE_NAME_INT) ) return true;
		if ( equals(TYPE_NAME_LONG) ) return true;
		if ( equals(TYPE_NAME_FLOAT) ) return true;
		if ( equals(TYPE_NAME_DOUBLE) ) return true;
		
		return false;
	}
}

