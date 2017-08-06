package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class TopicId extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public TopicId(String code)
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
	}
	
	static public class MyConverter extends Stringable.Converter<TopicId>
	{
		public TopicId fromString(String str, TopicId default_value)
		{
			try
			{
				return new TopicId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}