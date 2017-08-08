package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.pubsub.TopicId.MyConverter;

/**
 * Stringable class used to enforce our limitations on subscription id
 * 
 * @author kanej
 *
 */
public class SubscriptionId extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public SubscriptionId(String code)
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
	
	static public class MyConverter extends Stringable.Converter<SubscriptionId>
	{
		public SubscriptionId fromString(String str, SubscriptionId default_value)
		{
			try
			{
				return new SubscriptionId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
