package org.jimmutable.cloud.servlet_utils.search;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
/**
 * This is the class that we use to name our search fields
 * <li>Normalizes to lower case
 * <li>Normalizes underscore to dash
 * <li>Allowed characters a-z, 0-9, -
 * @author andrew.towe
 *
 */
public class SearchFieldId extends Stringable
{
	public static final MyConverter CONVERTER = new MyConverter();

	public SearchFieldId( String value )
	{
		super(value);
	}

	@Override
	public void normalize()
	{
		Validator.notNull(getSimpleValue());
		setValue(getSimpleValue().replace("_", "-"));
		normalizeTrim();
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
	}

	static public class MyConverter extends Stringable.Converter<SearchFieldId>
	{
		public SearchFieldId fromString( String str, SearchFieldId default_value )
		{
			try
			{
				return new SearchFieldId(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}

}
