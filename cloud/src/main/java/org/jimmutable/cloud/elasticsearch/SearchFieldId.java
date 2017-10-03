package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

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
