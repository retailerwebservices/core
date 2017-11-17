package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

public class DomainName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	public DomainName( String value )
	{
		super(value);
	}

	public DomainName( ObjectParseTree tree )
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
		Validator.containsOnlyValidCharacters(simpleValue, Validator.DOT, Validator.UPPERCASE_LETTERS,Validator.LOWERCASE_LETTERS, Validator.NUMBERS);
		Validator.isTrue(simpleValue.contains("."));
		String[] split = simpleValue.split("\\.");
		Validator.isTrue(split.length==2);
		Validator.min(split[0].length(), 1);
		Validator.min(split[1].length(), 1);
	}
	
	static public class MyConverter extends Stringable.Converter<DomainName>
	{
		public DomainName fromString( String str, DomainName default_value )
		{
			try
			{
				return new DomainName(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}
}
