package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * Class used to encapsulate one element of a cache key. For example, the
 * elements of the key foo/bar/baz are foo, bar and baz
 * 
 * Legal characters include lower case letters (normalized), numbers and dashes
 * 
 * @author kanej
 *
 */
public class CachePathElement extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public CachePathElement(String str)
	{
		super(str);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
	}
	
	static public class MyConverter extends Stringable.Converter<CachePathElement>
	{
		public CachePathElement fromString(String str, CachePathElement default_value)
		{
			try
			{
				return new CachePathElement(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}

