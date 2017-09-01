package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.storage.ApplicationId;
/**
 * 
 * @author andrew.towe
 * this class is designed to help us Define our Topics. 
 */
public class TopicDefinition extends Stringable
{

	public TopicDefinition( String value )
	{
		super(value);
	}

	public TopicDefinition( ApplicationId application_id, TopicId topic_id )
	{
		this(createStringFromComponents(application_id, topic_id));
	}

	private static String createStringFromComponents( ApplicationId application_id, TopicId topic_id )
	{
		return application_id.getSimpleValue() + "/" + topic_id.getSimpleValue();
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
	}

}
