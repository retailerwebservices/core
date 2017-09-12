package org.jimmutable.cloud.messaging;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;
/**
 * This Class is to help us handle all of our queue items
 * To make a Queueid, the value must be between 3 and 64 characters. 
 * Only Alphanumerics and dashes are accepted. All Upper case letters will be made into lower case. 
 * @author andrew.towe
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
