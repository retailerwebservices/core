package org.jimmutable.cloud.messaging;

import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
/**
 * 
 * @author andrew.towe
 *This class is designed to help us monitor our messaging topics
 */

//CODE REVIEW: The javadoc comment for this class is not correct.  This class is a stringable that enforces our limitations on topic id(s)... namely, min of 3 characters, max of 64 characters, a-z, 0-9 and dashes


public class TopicId extends Stringable
{
	public static TopicId application_public = new TopicId("public");
	public static TopicId application_private = new TopicId("private");

	public TopicId( String value )
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
