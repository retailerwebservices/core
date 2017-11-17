package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

public class FacebookId extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	public FacebookId( String value )
	{
		super(value);
	}

	public FacebookId( ObjectParseTree tree )
	{
		super(tree);
	}
		
	@Override
	public void normalize()
	{
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		String simpleValue = getSimpleValue();
		Validator.notNull(simpleValue);
		Validator.containsOnlyValidCharacters(simpleValue, Validator.LETTERS, Validator.NUMBERS);
		Validator.min(simpleValue.length(), 3);
		Validator.max(simpleValue.length(), 255);
	}
	
	static public class MyConverter extends Stringable.Converter<FacebookId>
	{
		public FacebookId fromString( String str, FacebookId default_value )
		{
			try
			{
				return new FacebookId(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}
}
