package org.jimmutable.core.objects.common;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class ObjectID  extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	private long long_value;
	
	public ObjectID(String code)
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
		
		char chars[] = getSimpleValue().toCharArray();
		
		StringBuilder clean_value = new StringBuilder();
		
		for ( char ch : chars )
		{
			if ( ch == '-' ) continue;
			
			if ( ch >= '0' && ch <= '9' )
			{
				clean_value.append(ch);
				if ( clean_value.length() == 12 ) break; // maximum number of digits is 12
				continue;
			}
			
			throw new ValidationException(String.format("Illegal character \'%c\' in object id %s.  Only numbers and dashes (-) are allowed.", ch, getSimpleValue()));
		}
		
		try
		{
			long_value = Long.parseUnsignedLong(clean_value.toString());
			
			String normalized_value = String.format("%012d", long_value);
			
			normalized_value = String.format("%s-%s-%s",
					normalized_value.substring(0, 4),
					normalized_value.substring(4, 8),
					normalized_value.substring(8, normalized_value.length())
					);
			
			setValue(normalized_value);
		}
		catch(Exception e)
		{
			throw new ValidationException(String.format("Invalid object id %s",getSimpleValue()));
		}
	}
	
	public long getSimpleLongValue()
	{
		return long_value;
	}
	
	static public class MyConverter extends Stringable.Converter<ObjectID>
	{
		public ObjectID fromString(String str, ObjectID default_value)
		{
			try
			{
				return new ObjectID(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	static public ObjectID randomID()
	{
		long value = (long)(Math.random()*9999_9999_9999l);
		return new ObjectID(""+value);
	}
}