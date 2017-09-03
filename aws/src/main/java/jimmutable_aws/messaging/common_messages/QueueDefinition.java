package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.storage.ApplicationId;


/**
 * 
 * @author andrew.towe
 * This class exists to help us define our queues and how they relate to the application
 */

//CODE REVIEW: This javadoc is not adequate.  Queues are resources that are owned by an application.  A queue definition defines the application that owns a queue along with the id of the topic

public class QueueDefinition extends Stringable
{
	ApplicationId application_id;
	QueueId queue_id;
	public QueueDefinition( String value )
	{
		super(value);
		// TODO Auto-generated constructor stub
	}

	public QueueDefinition( ApplicationId application_id, QueueId queue_id )
	{
		this(createStringFromComponents(application_id, queue_id));
	}

	private static String createStringFromComponents( ApplicationId application_id, QueueId queue_id )
	{
		Validator.notNull(application_id, queue_id);
		return application_id.getSimpleValue() + "/" + queue_id.getSimpleValue();
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
		queue_id = new QueueId(breakonslash[1]);
		
		// CODE REVIEW: You need to set the value here using createStringFromComponents, test this in unit test
	}

	public ApplicationId getSimpleApplicationId()
	{
		return application_id;
	}

	public QueueId getSimpleQueueId()
	{
		return queue_id;
	}

}