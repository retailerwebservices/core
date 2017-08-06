package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.pubsub.TopicID.MyConverter;

public class SubscriptionID extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public SubscriptionID(String code)
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
	
	static public class MyConverter extends Stringable.Converter<SubscriptionID>
	{
		public SubscriptionID fromString(String str, SubscriptionID default_value)
		{
			try
			{
				return new SubscriptionID(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
