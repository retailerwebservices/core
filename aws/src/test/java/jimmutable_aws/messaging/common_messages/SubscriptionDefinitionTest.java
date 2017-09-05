package jimmutable_aws.messaging.common_messages;

import org.jimmutable.aws.messaging.QueueDefinition;
import org.jimmutable.aws.messaging.QueueId;
import org.jimmutable.aws.messaging.SubscriptionDefinition;
import org.jimmutable.aws.messaging.TopicDefinition;
import org.jimmutable.aws.messaging.TopicId;
import org.jimmutable.storage.ApplicationId;

import junit.framework.TestCase;

public class SubscriptionDefinitionTest extends TestCase
{

	public static void testCreation()
	{
		SubscriptionDefinition subdef = new SubscriptionDefinition(
				new TopicDefinition(new ApplicationId("Development1"), new TopicId("flight_speed_of_a_swallow")),
				new QueueDefinition(new ApplicationId("Development2"), new QueueId("123456")));
		assertEquals("development1/flight_speed_of_a_swallow/development2/123456", subdef.getSimpleValue());

		subdef = new SubscriptionDefinition(new TopicDefinition("Development1/flight_speed_of_a_swallow"),
				new QueueDefinition("Development2/123456"));
		assertEquals("development1/flight_speed_of_a_swallow/development2/123456", subdef.getSimpleValue());
	}
}
