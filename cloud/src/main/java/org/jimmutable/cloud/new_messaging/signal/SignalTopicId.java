package org.jimmutable.cloud.new_messaging.signal;

import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * 
 * You send a signal to a topic. This class is used to encapsulate a topic id
 * 
 * Legal characters are numbers, lower case letters and dashes. Must be between
 * 3 and 64 characters
 * 
 * @author kanej
 *
 */

public class SignalTopicId extends Stringable
{
	public SignalTopicId( String value )
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
		Validator.min(getSimpleValue().length(), 3);
		Validator.max(getSimpleValue().length(), 64);
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.DASH, Validator.LOWERCASE_LETTERS,
				Validator.NUMBERS);  

	}
	static public class MyConverter extends Stringable.Converter<SignalTopicId>
	{
		public SignalTopicId fromString(String str, SignalTopicId default_value)
		{
			try
			{
				return new SignalTopicId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
