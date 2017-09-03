package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.storage.ApplicationId;

/**
 * 
 * @author andrew.towe 
 * this class is designed to help us Define our Topics.
 */

//CODE REVIEW: This javaod is not adequate.  Topic's are resources that are owned by an application.  A topic definition defines the application that owns a topic along with the id of the topic

public class TopicDefinition extends Stringable
{

	private ApplicationId application_id;
	private TopicId topic_id;

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
		Validator.notNull(application_id, topic_id);
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

		String[] breakonslash = getSimpleValue().split("/");
		Validator.min(breakonslash.length, 2);
		application_id = new ApplicationId(breakonslash[0]);
		topic_id = new TopicId(breakonslash[1]);
		
		// CODE REVIEW: You need to set the value here using createStringFromComponents
	}

	public ApplicationId getSimpleApplicationId()
	{
		return application_id;
	}

	public TopicId getSimpleTopicId()
	{
		return topic_id;
	}

}
