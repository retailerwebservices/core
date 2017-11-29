package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

public class HostName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	public HostName( String value )
	{
		super(value);
	}

	public HostName( ObjectParseTree tree )
	{
		super(tree);
	}
		
	private String getNecessaryInfo( String value)
	{
		Validator.notNull(value);
		String[] split = value.split("/");
		for ( String string : split )
		{
			if(string.contains(".")) {
				return string;
			}
		}
		return split[0];
	}
	
	@Override
	public void normalize()
	{
		setValue(getNecessaryInfo(getSimpleValue()));
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		String simpleValue = getSimpleValue();
		Validator.notNull(simpleValue);
		Validator.containsOnlyValidCharacters(simpleValue, Validator.DOT, Validator.UPPERCASE_LETTERS,Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
		Validator.isTrue(simpleValue.contains("."));
		String[] split = simpleValue.split("\\.");
		Validator.isTrue(split.length>=2);
		Validator.min(split[0].length(), 1);
		Validator.min(split[1].length(), 1);
	}
	
	static public class MyConverter extends Stringable.Converter<HostName>
	{
		public HostName fromString( String str, HostName default_value )
		{
			try
			{
				return new HostName(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}

}
