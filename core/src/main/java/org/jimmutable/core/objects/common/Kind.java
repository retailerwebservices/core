package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * The kind stringable is frequently used as a label for a "kind" of object.
 * Legal characters are a-z, 0-9 and dash. Upper case characters are normalized
 * to lower case. Kind objects are used for StandardImmutableObject that implement Storable.
 * 
 * This object is length limited to 64 characters. Must be at least 3 characters
 * long
 * 
 * @author kanej
 *
 */
public class Kind extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public Kind(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 3);
		Validator.max(getSimpleValue().length(), 64);
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
	}
	
	static public class MyConverter extends Stringable.Converter<Kind>
	{
		public Kind fromString(String str, Kind default_value)
		{
			try
			{
				return new Kind(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
