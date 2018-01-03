package org.jimmutable.cloud.cache.redis;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class RedisApplicationId extends Stringable
{
	static public final RedisApplicationId MESSAGING = new RedisApplicationId("$messaging");
	
	public RedisApplicationId(String value)
	{
		super(value);
	}

	@Override
	public void normalize()
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		Validator.max(getSimpleValue().length(), 64);
		
		// Allow the special value for messaging
		if ( getSimpleValue().equals("$messaging") ) 
			return; 
		
		// Otherwise, limit to letters, dash and numbers
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.DASH, Validator.LOWERCASE_LETTERS, Validator.NUMBERS);
	}

	static public class MyConverter extends Stringable.Converter<RedisApplicationId>
	{
		public RedisApplicationId fromString(String str, RedisApplicationId default_value)
		{
			try
			{
				return new RedisApplicationId(str);
			} catch (Exception e)
			{
				return default_value;
			}
		}
	}
}
