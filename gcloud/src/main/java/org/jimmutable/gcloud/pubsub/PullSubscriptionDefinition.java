package org.jimmutable.gcloud.pubsub;

import java.util.Objects;

import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.ProjectID;

import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;

public class PullSubscriptionDefinition extends TransientImmutableObject<PullSubscriptionDefinition>
{
	private ProjectID subscription_project;
	private SubscriptionID subscription_id;
	
	private ProjectID topic_project_id;
	private TopicID topic_id;
	
	public PullSubscriptionDefinition(ProjectID subscription_project, SubscriptionID subscription_id, ProjectID topic_project, TopicID topic_id)
	{
		this.subscription_project = subscription_project;
		this.subscription_id = subscription_id;
		
		this.topic_project_id = topic_project;
		this.topic_id = topic_id;
	}
	
	public ProjectID getSimpleSubscriptionProjectID() { return subscription_project; }
	public SubscriptionID getSimpleSubscriptionID() { return subscription_id; }
	
	public ProjectID getSimpleTopicProjectID() { return topic_project_id; }
	public TopicID getSimpleTopicID() { return topic_id; }

	public int compareTo(PullSubscriptionDefinition o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimpleSubscriptionProjectID(), o.getSimpleSubscriptionProjectID());
		ret = Comparison.continueCompare(ret, getSimpleSubscriptionID(), o.getSimpleSubscriptionID());
		ret = Comparison.continueCompare(ret, getSimpleTopicProjectID(), o.getSimpleTopicProjectID());
		ret = Comparison.continueCompare(ret, getSimpleTopicID(), o.getSimpleTopicID());
		
		return ret;
	}


	public void freeze() {}
	public void normalize() {}
	public void validate() { Validator.notNull(subscription_project, subscription_id, topic_project_id, topic_id);}

	@Override
	public int hashCode() 
	{
		return Objects.hash(subscription_project, subscription_id, topic_project_id, topic_id);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof PullSubscriptionDefinition)) return false;
		
		return compareTo((PullSubscriptionDefinition)obj) == 0;
	}

	
	public String toString() 
	{
		return String.format("{subscription_project:'%s', subscription_id:'%s', topic_project_id:'%s', topic_id:'%s'}", subscription_project, subscription_id, topic_project_id, topic_id);
	}
	
	public SubscriptionName createSimpleSubscriptionName()
	{
		return SubscriptionName.create(getSimpleSubscriptionProjectID().getSimpleValue(), getSimpleSubscriptionID().getSimpleValue());
	}
	 
	public TopicName createSimpleTopicName()
	{
		return TopicName.create(topic_project_id.getSimpleValue(), topic_id.getSimpleValue());
	}
}
