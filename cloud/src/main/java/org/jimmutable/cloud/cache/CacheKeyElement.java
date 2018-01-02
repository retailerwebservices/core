package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
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
public class CacheKeyElement extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public CacheKeyElement(String str)
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
	
	static public class MyConverter extends Stringable.Converter<CacheKeyElement>
	{
		public CacheKeyElement fromString(String str, CacheKeyElement default_value)
		{
			try
			{
				return new CacheKeyElement(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}

