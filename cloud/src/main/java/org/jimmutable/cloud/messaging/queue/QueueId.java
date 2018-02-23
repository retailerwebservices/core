package org.jimmutable.cloud.messaging.queue;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * 
 * Each Queue has an Id (encapsulated by this class)
 * 
 * Legal characters are numbers, lower case letters and dashes. Must be between
 * 3 and 64 characters
 * 
 * @author kanej
 *
 */
public class QueueId extends Stringable
{
	public QueueId( String value )
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
	
	static public class MyConverter extends Stringable.Converter<QueueId>
	{
		public QueueId fromString(String str, QueueId default_value)
		{
			try
			{
				return new QueueId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
