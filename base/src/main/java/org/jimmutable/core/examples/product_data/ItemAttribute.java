package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.utils.Validator;


/**
 * An example of a simple, Stringable object
 * 
 * @author jim.kane
 *
 */

public class ItemAttribute extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	static public final ItemAttribute ATTRIBUTE_BRAND = new ItemAttribute("BRAND");
	static public final ItemAttribute ATTRIBUTE_PN = new ItemAttribute("PN");
	
	
	public ItemAttribute(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeUpperCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		char chars[] = getSimpleValue().toCharArray();
		for ( char ch : chars )
		{
			if ( ch >= 'A' && ch <= 'Z' ) continue;
			if ( ch >= '0' && ch <= '9' ) continue;
			if ( ch == '_' ) continue;
			
			throw new ValidationException(String.format("Illegal character \'%c\' in item attribute %s.  Only upper case letters, numbers, and underscores are allowed", ch, getSimpleValue()));
		}
	}
	
	static public class MyConverter extends Stringable.Converter<ItemAttribute>
	{
		public ItemAttribute fromString(String str, ItemAttribute default_value)
		{
			try
			{
				return new ItemAttribute(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
