package org.jimmutable.core.objects.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.core.utils.Validator.ValidCharacters;

/**
 * A stringable class used to represent a numerical Object Id. Id(s) are 16
 * digit long string printed in lower case hex with dashes separating the string
 * into 4 groups for easy reading.
 * 
 * Negatives object id(s) are not allowed (normalized to positive)
 * 
 * Some example Id(s)
 * 
 * 42fb-e16d-95ac-8274
 * 10c1-4296-742d-5e8d
 * 2232-f768-2d2f-86d6
 * 
 * @author jim.kane
 *
 */
public class ObjectId  extends Stringable
{
	static private Random random = new Random();
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	private long long_value;
	
	public ObjectId(long value)
	{ 
		super(prettyPrintObjectId(value));
	}
	
	public ObjectId(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		
		long_value = parseObjectId(getSimpleValue(),-1);
		
		if ( long_value < 0 ) 
			throw new ValidationException(String.format("Invalid ObjectId %s",getSimpleValue()));
		
		setValue(prettyPrintObjectId(long_value));
	}
	
	/**
	 * Get the value of the Id as a long
	 * @return The value of the Id as a long
	 */
	public long getSimpleLongValue()
	{
		return long_value;
	}
	
	static public class MyConverter extends Stringable.Converter<ObjectId>
	{
		public ObjectId fromString(String str, ObjectId default_value)
		{
			try
			{
				return new ObjectId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}

	/**
	 * Generate a new, random ObjectId.  Deprecated because the naming convention is incorrect, use createRandomId instead
	 * 
	 * @return A new, random ObjectId
	 */
	 @Deprecated
	static public ObjectId randomID()
	{
		return createRandomId();
	}
	
	/**
	 * Generate a new, random ObjectId
	 * 
	 * @return A new, random ObjectId
	 */
	static public ObjectId createRandomId()
	{
		return new ObjectId(random.nextLong());
	}
	
	/**
	 * Parse a pretty printed object id
	 * 
	 * @param pretty_printed_object_id
	 *            A pretty printed object id
	 * @param default_value
	 *            The value to return if the pretty printed ID can not be parsed.
	 *            Because negative object id(s) are not allowed, using -1 as a
	 *            default value is a good way to test if the parse worked. (i.e. the
	 *            only way to get -1 back from this function is if the parse failed)
	 * @return the long object id, or default_value if the input can not be parsed
	 */
	static public long parseObjectId(String pretty_printed_object_id, long default_value)
	{
		if ( pretty_printed_object_id == null || pretty_printed_object_id.length() == 0 ) return default_value;
		
		if ( pretty_printed_object_id.startsWith("0x") ) 
			pretty_printed_object_id = pretty_printed_object_id.substring(2);
		
		StringBuilder tmp = new StringBuilder();
		
		char chars[] = pretty_printed_object_id.toCharArray();
		
		for ( char ch : chars )
		{
			if ( ch >= '0' && ch <= '9' ) { tmp.append(ch); continue; }
			if ( ch >= 'a' && ch <= 'f' ) { tmp.append(ch); continue; }
			if ( ch >= 'A' && ch <= 'F' ) { tmp.append(ch); continue; }
			
			if ( ch == '-' ) continue;
			if ( ch == '_' ) continue;
			if ( ch == '.' ) continue;
			if ( ch == ' ' ) continue;
			if ( ch == '\t' ) continue;
			if ( ch == '\r' ) continue;
			if ( ch == '\n' ) continue;
			
			
			return default_value;
		}
		
		try
		{
			return Long.parseLong(tmp.toString(), 16);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return default_value;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new ObjectId(1));
		System.out.println(new ObjectId(2));
		System.out.println(new ObjectId(3));
		System.out.println(new ObjectId(4));
		System.out.println(new ObjectId(5));
		System.out.println(new ObjectId(Long.MAX_VALUE));
		System.out.println(new ObjectId(Long.MAX_VALUE-1));
		System.out.println(new ObjectId(Long.MAX_VALUE-16));
	}
	
	/**
	 * Given an object id as a long (value), return the pretty printed version of it
	 * 
	 * Negative object id(s) will be normalized to positive
	 * 
	 * @param value
	 *            The object id (as a long)
	 * @return A pretty printed version of the object id.
	 */
	static public String prettyPrintObjectId(long value)
	{
		if ( value < 0 ) value = -value;
		
		String str = String.format("%016x", value);
		
		char chars[] = str.toCharArray();
		
		StringBuilder ret = new StringBuilder();
		
		for ( int i = 0; i < chars.length; i++ )
		{
			ret.append(chars[i]);
			
			if ( i == 3 || i == 7 || i == 11 ) ret.append('-');
		}
		
		return ret.toString();
	}
	
}