package org.jimmutable.aws.http;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * A query string is of the form key=value&key=value&key=value or
 * fragment&fragment&fragment
 * 
 * A query string fragment is of the form key=value
 * 
 * URL(s) support encoding (https://www.w3schools.com/tags/ref_urlencode.asp)
 * where special characters (e.g. &, =, space etc.) are written using
 * %[character code]. For example, if I have the value (decoded value) "hello
 * world", the encoded value would be hello%20world (the space has been replaced
 * with %).
 * 
 * @author kanej
 *
 */

public class QueryStringFragment extends Stringable
{
	private QueryStringKey key;
	private String encoded_value;
	private String decoded_value;
	
	/**
	 * Construct a query string fragment. The value must be properly URL encoded
	 * 
	 * @param encoded_fragment
	 */
	public QueryStringFragment(String encoded_fragment)
	{
		super(encoded_fragment);
	}
	
	/**
	 * Construct a query string fragment from a key/decoded value pair
	 * 
	 * @param key
	 *            The key of the fragment
	 * @param decoded_value
	 *            The (decoded or "not encoded") value of the fragment
	 */
	public QueryStringFragment(QueryStringKey key, String decoded_value)
	{
		this(createQueryStringFragment(key,decoded_value));
	}
	
	/**
	 * Create a properly url encoded query string fragment from its parts
	 * 
	 * @param key
	 *            The key of the fragment
	 * @param non_url_encoded_value
	 *            The (decoded or "not encoded") value of the fragment
	 * @return
	 */
	static public String createQueryStringFragment(QueryStringKey key, String decoded_value)
	{
		return String.format("%s=%s", key.getSimpleValue(), URLEncode(decoded_value));
	}

	@Override
	public void normalize() 
	{
	}

	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		
		String parts[] = getSimpleValue().split("=");
		
		if ( parts.length != 2 ) throw new ValidationException("no = in query string entry");
		
		parts[0] = URLDecode(parts[0]);
		parts[1] = URLDecode(parts[1]);
		
		key = new QueryStringKey(parts[0]);
		decoded_value = parts[1];
		encoded_value = URLEncode(parts[1]);
		
		setValue(createQueryStringFragment(key,decoded_value));
	}
	
	public QueryStringKey getSimpleKey() { return key; }
	
	/**
	 * Get the decoded (not encoded) value of the fragment.
	 * 
	 * IN ALMOST ALL CASES YOU WANT THE DECODED VALUE
	 * 
	 * @return The decoded value of the fragment
	 */
	public String getSimpleDecodedValue() { return decoded_value; }
	
	/**
	 * The encoded value of the fragment.
	 * 
	 * @return The URL encoded value of the fragment. Generally, this is *not* the
	 *         value you want, you want the decoded value...
	 */
	public String getSimpleEncodedValue() { return encoded_value; }
	
	static public String URLEncode(String value)
	{
		try
		{
			return URLEncoder.encode(value, "UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException("Somehow UTF-8 is not a valid character encoding.  This is not, strictly speaking, possible");
		}
	}
	
	static public String URLDecode(String value)
	{
		try
		{
			return URLDecoder.decode(value, "UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException("Somehow UTF-8 is not a valid character encoding.  This is not, strictly speaking, possible");
		}
	}
	
	static public class MyConverter extends Stringable.Converter<QueryStringFragment>
	{
		public QueryStringFragment fromString(String str, QueryStringFragment default_value)
		{
			try
			{
				return new QueryStringFragment(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
