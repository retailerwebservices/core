package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.storage.ApplicationId;

/**
 * @author andrew.towe
 * This class helps us defind our subscriptions. It is based on topic of the subscription and a queue
 */

//CODE REVIEW: This javadoc is not adequate.  Subscriptions are resources that are *not* owned by an application (subscriptions are frequently between applications).  A subscription definition connects a topic with a queue (hence, it is a pair of topic and queue


public class SubscriptionDefinition extends Stringable
{
	private TopicDefinition topic_definition;
	private QueueDefinition queue_definition;

	public SubscriptionDefinition( String value )
	{
		super(value);
	}

	public SubscriptionDefinition( TopicDefinition topic_definition, QueueDefinition queue_definition )
	{
		this(createStringFromComponents(topic_definition, queue_definition));
	}

	private static String createStringFromComponents( TopicDefinition topic_definition,
			QueueDefinition queue_definition )
	{
		Validator.notNull(topic_definition, queue_definition);
		return topic_definition.getSimpleValue() + "/" + queue_definition.getSimpleValue();
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
		Validator.min(breakonslash.length, 4);
		topic_definition = new TopicDefinition(breakonslash[0] + "/" + breakonslash[1]);
		queue_definition = new QueueDefinition(breakonslash[2] + "/" + breakonslash[3]);

		// CODE REVEIEW: You need to set the value here with createStringFromComponents.  A good example would be  foo / bar / baz / quz -> foo/bar/baz/quz
	}

	public TopicDefinition getSimpleApplicationId()
	{
		return topic_definition;
	}

	public QueueDefinition getSimpleTopicId()
	{
		return queue_definition;
	}
}
