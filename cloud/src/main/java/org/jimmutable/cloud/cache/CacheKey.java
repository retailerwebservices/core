package org.jimmutable.cloud.cache;

import java.util.Arrays;
import java.util.List;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * $messaging/
 * application-id/cache-path://key
 * 
 * Stringable, format [CacheKeyElement]/[CachekeyElement]/... (no practical
 * limit in length)
 * 
 * ChacheKeyElement is lower case, numbers and - only
 * 
 * @author kanej
 *
 */
public class CacheKey extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	private FieldList<CacheKeyElement> elements;
	
	public CacheKey(String str)
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
		
		String arr[] = getSimpleValue().split("/");
		elements = new FieldArrayList();
		
		for ( String element : arr )
		{
			element = element.trim();
			if ( element.length() == 0 ) continue;
			
			elements.add(new CacheKeyElement(element));
		}
		
		Validator.min(elements.size(), 1);
		elements.freeze(); // freeze the elements
		
		// Normalize the string...
		{
			StringBuilder ret = new StringBuilder();
			
			for ( CacheKeyElement element : elements )
			{
				if ( ret.length() != 0 ) ret.append("/");
				
				ret.append(element);
			}
			
			setValue(ret.toString());
		}
	}
	
	static public class MyConverter extends Stringable.Converter<CacheKey>
	{
		public CacheKey fromString(String str, CacheKey default_value)
		{
			try
			{
				return new CacheKey(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	/**
	 * Get the list of elements that make up the cache key
	 * 
	 * @return The list of elements that make up the cache key
	 */
	public List<CacheKeyElement> getSimpleElements() { return elements; }
	
	/**
	 * Create the parent (e.g. the parent of foo/bar/baz is foo/bar
	 * @param default_value The value to return if the key does not have a parent
	 * 
	 * @return The parent of the cache key, or default value if the cache key does not have a parent
	 */
	public CacheKey createParent(CacheKey default_value)
	{
		if ( !hasParent() ) return default_value;
		
		StringBuilder ret = new StringBuilder();
		
		for ( int i = 0; i < elements.size()-1; i++ )
		{
			CacheKeyElement element = elements.get(i);
			
			if ( ret.length() != 0 ) ret.append("/");
			ret.append(element.toString());
		}
		
		return new CacheKey(ret.toString());
	}
	
	/**
	 * Test to see if the current cache key has a parent. foo/bar has a parent (foo)
	 * but foo does not have a parent.
	 * 
	 * @return true if the cache key has a parent, false otherwise.
	 */
	public boolean hasParent() { return elements.size() > 1; }
}


