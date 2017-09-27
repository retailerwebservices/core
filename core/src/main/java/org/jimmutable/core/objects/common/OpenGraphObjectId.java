package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

public class OpenGraphObjectId extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();

	public OpenGraphObjectId( String s )
	{
		super(s);
	}

	public OpenGraphObjectId( ObjectParseTree reader )
	{
		super(reader);

	}

	@Override
	public void normalize()
	{

	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		Validator.max(getSimpleValue().length(), 128);
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.NUMBERS);
	}

	static public class MyConverter extends Stringable.Converter<OpenGraphObjectId>
	{
		public OpenGraphObjectId fromString( String str, OpenGraphObjectId default_value )
		{
			try
			{
				return new OpenGraphObjectId(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}
}
