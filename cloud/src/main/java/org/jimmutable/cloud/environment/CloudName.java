package org.jimmutable.cloud.environment;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class CloudName extends Stringable
{
	static public CloudName UNIT_TEST_CLOUD = new CloudName("unit-test");
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public CloudName(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
		
		if ( getSimpleValue().startsWith("-") ) throw new ValidationException(String.format("Cloud name %s is invalid, bucket names may not start with a dash (-) ", getSimpleValue()));
		if ( getSimpleValue().endsWith("-") ) throw new ValidationException(String.format("Cloud name %s is invalid, bucket names may not end with a dash (-) ", getSimpleValue()));
		if ( getSimpleValue().contains("--") ) throw new ValidationException(String.format("Cloud name %s is invalid, bucket names may not contain two dashes in a row (--) ", getSimpleValue()));
	}
	
	static public class MyConverter extends Stringable.Converter<CloudName>
	{
		public CloudName fromString(String str, CloudName default_value)
		{
			try
			{
				return new CloudName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
