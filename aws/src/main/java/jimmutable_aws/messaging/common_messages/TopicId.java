package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class TopicId extends Stringable
{
	public static TopicId application_public= new TopicId("public");
	public static TopicId application_private= new TopicId("private");

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
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE,Validator.LOWERCASE_LETTERS,Validator.NUMBERS);

	}

}
