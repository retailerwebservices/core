package org.jimmutable.aws.http;

import org.jimmutable.core.examples.product_data.ItemAttribute;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * A query string is of the form key=value&key=value&key=value
 * 
 * This class encapsulates jimmutable's handling of query string keys. Keys are
 * normalized to lower case and any characters other than a-z, 0-9 and - are
 * stripped. keys may not be blank.
 * 
 * Underscores are normalized to dashes (i.e. _ turns into -)
 * 
 * 
 * @author kanej
 *
 */

public class QueryStringKey extends Stringable
{		
	public QueryStringKey(String non_url_encoded_value)
	{
		super(non_url_encoded_value);
	}
	
	@Override
	public void normalize() 
	{
		normalizeLowerCase();
	}

	@Override
	public void validate() 
	{	
		Validator.notNull(getSimpleValue());
		
		StringBuilder clean_value = new StringBuilder();
		
		char chars[] = getSimpleValue().toCharArray();
		
		for ( char ch : chars )
		{
			if ( ch >= 'a' && ch <= 'z' ) { clean_value.append(ch); }
			if ( ch >= '0' && ch <= '9' ) { clean_value.append(ch); }
			if ( ch == '-' ) { clean_value.append(ch); }
			if ( ch == '_') { clean_value.append('-'); }
		}
		
		setValue(clean_value.toString());
		
		Validator.min(getSimpleValue().length(), 1);
	}
	
	static public class MyConverter extends Stringable.Converter<QueryStringKey>
	{
		public QueryStringKey fromString(String str, QueryStringKey default_value)
		{
			try
			{
				return new QueryStringKey(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
