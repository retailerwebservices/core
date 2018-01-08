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
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UPPERCASE_LETTERS, Validator.NUMBERS, Validator.UNDERSCORE);
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
