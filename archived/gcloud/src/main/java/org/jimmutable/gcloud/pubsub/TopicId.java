package org.jimmutable.gcloud.pubsub;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * Stringable class used to enforce our limitations on topic id(s) (Must be
 * lower case, contain only letters, numbers and dashes)
 * 
 * @author kanej
 *
 */
public class TopicId extends Stringable
{
	/**
	 * Every project has two standard topics, the project private topic (used to
	 * communicate between services within the same project) and the public topic
	 * (used to communicate with the outside world).
	 * 
	 */
	static public final TopicId TOPIC_PROJECT_PRIVATE = new TopicId("project-private");
	
	/**
	 * The current project's public topic.
	 * 
	 * Messages sent to this topic should be carefully designed and version
	 * controlled, because you have no control over *who* may subscribe to the
	 * public topic (as opposed to the project private topic where the only
	 * subscribers are services within the project)
	 */
	static public final TopicId TOPIC_PUBLIC = new TopicId("project-public");
	
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
		Validator.max(getSimpleValue().length(), 64);
		
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