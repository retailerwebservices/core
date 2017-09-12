package org.jimmutable.cloud.messaging;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;


/**
 * Queues are resources that are owned by an application.  A Queue definition defines the application that owns a Queue along with the id of the queue
 * Queues Definitions have 2 components to them, the Application Id and the Queue Id
 * The Queues Definition is formatted at [ApplicationId/QueueId]
 * @author andrew.towe
 *	
 */

public class QueueDefinition extends Stringable
{
	ApplicationId application_id;
	QueueId queue_id;
	public QueueDefinition( String value )
	{
		super(value);
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
		
		setValue(createStringFromComponents(application_id, queue_id));
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