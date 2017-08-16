package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

public enum CountryCode implements StandardEnum
{
	US( "United States of America", "US", "USA"),  
	CA("Canada", "CA", "CAN");

	static public final MyConverter CONVERTER = new MyConverter();

	private String name;
	private String code_2ltr;
	private String code_3ltr;

	private CountryCode(String name, String code_2ltr, String code_3ltr )
	{
		Validator.notNull(name);
		this.name = name;
		Validator.notNull(code_2ltr);
		this.code_2ltr = Normalizer.upperCase(code_2ltr);
		Validator.notNull(code_3ltr);
		this.code_3ltr = Normalizer.upperCase(code_3ltr);
	}

	public String getSimpleCode() { return code_2ltr;	}
	public String getSimpleCode2ltr() { return code_2ltr; }
	public String getSimpleCode3ltr() { return code_3ltr; }
	public String getSimpleName() { return name; }
	public String toString()	{ return getSimpleCode(); }

	static public class MyConverter extends StandardEnum.Converter<CountryCode>
	{
		@Override
		public CountryCode fromCode(String code, CountryCode default_value)
		{
			if (code == null) return default_value;
			code = code.trim();

			for (CountryCode c : CountryCode.values()) 
			{
				if (c.getSimpleCode().equalsIgnoreCase(code))
					return c;
				if (c.getSimpleCode3ltr().equalsIgnoreCase(code))
					return c;
			}

			return default_value;
		}
	}
}
