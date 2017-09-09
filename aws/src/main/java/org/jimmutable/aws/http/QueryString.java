package org.jimmutable.aws.http;

import java.util.Arrays;
import java.util.List;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * A stringable used to encapsulate jimmutable's handling of query strings
 * 
 * A query string is of the form key=value&key=value&key=value or
 * fragment&fragment&fragment
 * 
 * Our implementation of keys is case insensitive and strips all characters
 * other than letters, numbers and dashes. Underscores are normalized into
 * dashes.
 * 
 * A query string may contain multiple values for a given key. Order is
 * preserved (since the string is stored as a list of fragments). That being
 * said, most of the time handlers do not use this property and will only look
 * at the value associated with the first key.
 * 
 * @author kanej
 *
 */
public class QueryString extends Stringable
{
	private FieldList<QueryStringFragment> fragments;
	
	public QueryString(String str)
	{
		super(str);
	}
	
	public QueryString(List<QueryStringFragment> fragments)
	{
		super(createStringFromFragments(fragments));
	}
	
	public QueryString(QueryStringFragment ...fragments)
	{
		super(createStringFromFragments(Arrays.asList(fragments)));
	}
	
	public void normalize() 
	{	
	}
	 
	static public String createStringFromFragments(List<QueryStringFragment> fragments)
	{
		if ( fragments == null ) return "";
		
		StringBuilder ret = new StringBuilder();
		
		for ( QueryStringFragment fragment : fragments )
		{
			if ( ret.length() != 0 )
				ret.append("&");
			
			ret.append(fragment.getSimpleValue());
		}
		
		return ret.toString();
	}

	public void validate() 
	{	
		Validator.notNull(getSimpleValue());
		
		String fragment_strings[] = getSimpleValue().split("\\&");
		
		fragments = new FieldArrayList();
		
		for ( String fragment_string : fragment_strings )
		{
			try
			{
				QueryStringFragment fragment = new QueryStringFragment(fragment_string);
				fragments.add(fragment);
			}
			catch(Exception e)
			{
				// Bad fragment, skip
			}
		}
		
		fragments.freeze();
		
		setValue(createStringFromFragments(fragments));
	}
	
	public Iterable<QueryStringFragment> getSimpleQueryStringFragments() { return fragments; }
	
	/**
	 * Test the query string to see if it contains at least one value associated
	 * with a specified key
	 * 
	 * @param key
	 *            The key to check for. If key is null, the function will always
	 *            return false
	 * @return True if the query string contains at least one value for a given key,
	 *         false otherwise
	 */
	public boolean containsKey(QueryStringKey key) 
	{
		return getOptionalValue(key,0,null) != null;
	}
	
	/**
	 * Convenience method. Roughly equivalent to containsKey(new
	 * QueryStringKey(key))
	 * 
	 * @param key
	 *            The key to check for. If key is null or key is otherwise
	 *            malformed, the function will always return false
	 * @return True if the query string contains at least one value for a given key,
	 *         false otherwise
	 */
	public boolean containsKey(String key)
	{
		return getOptionalValue(key,0,null) != null;
	}
	
	/**
	 * Get the value associated with a specified key
	 * 
	 * @param key
	 *            The key whose value should be returned.
	 * @param index
	 *            The index of the value that should be returned. To get the first
	 *            value associated with this key, index = 0
	 * @param default_value
	 *            The value to return if the specified key does not exist
	 * @return The value associated with the specified instance of a given key, or
	 *         default_value if this key instance does not exist
	 */
	public String getOptionalValue(QueryStringKey key, int index, String default_value)
	{
		if ( key == null || index < 0 ) return default_value;
		
		int count = 0;
		
		for ( QueryStringFragment fragment : getSimpleQueryStringFragments() )
		{
			if ( fragment.getSimpleKey().equals(key) )
			{
				if ( count == index ) return fragment.getSimpleFragmentDecodedValue();
				count++;
			}
		}
		
		return default_value;
	}
	
	/**
	 * Convenience method, roughly equivalent to getOptionalValue(new
	 * QueryStringKey(key), index, default_value)
	 * 
	 * @param key
	 *            The key whose value should be returned.
	 * @param index
	 *            The index of the value that should be returned. To get the first
	 *            value associated with this key, index = 0
	 * @param default_value
	 *            The value to return if the specified key does not exist
	 * @return The value associated with the specified instance of a given key, or
	 *         default_value if this key instance does not exist
	 */
	public String getOptionalValue(String key, int index, String default_value)
	{
		if ( key == null || index < 0 ) return default_value;
		
		try
		{
			return getOptionalValue(new QueryStringKey(key), index, default_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Convenience method for getting the first value associated with a specified
	 * key. Equivalent to getOptionalValue(key,0,default_value)
	 * 
	 * @param key
	 *            The key of the value to get
	 * @param default_value
	 *            The value to return if key does not exist in the query string
	 * @return The value associated with the specified key, or default_value
	 *         otherwise
	 */
	public String getOptionalValue(String key, String default_value)
	{
		return getOptionalValue(key,0,default_value);
	}
	
	/**
	 * Convenience method for getting the first value associated with a specified
	 * key. Equivalent to getOptionalValue(key,0,default_value)
	 * 
	 * @param key
	 *            The key of the value to get
	 * @param default_value
	 *            The value to return if key does not exist in the query string
	 * @return The value associated with the specified key, or default_value
	 *         otherwise
	 */
	public String getOptionalValue(QueryStringKey key, String default_value)
	{
		return getOptionalValue(key,0,default_value);
	}
	
	static public class MyConverter extends Stringable.Converter<QueryString>
	{
		public QueryString fromString(String str, QueryString default_value)
		{
			try
			{
				return new QueryString(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
