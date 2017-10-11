package org.jimmutable.cloud.messaging.dev_local;

import java.util.Objects;

import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.SubscriptionDefinition;
import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Validator;

public class SubscriptionListenerPair extends TransientImmutableObject<SubscriptionListenerPair>
{
	private SubscriptionDefinition definition;
	private MessageListener listener;
	
	public SubscriptionListenerPair(SubscriptionDefinition definition, MessageListener listener)
	{
		this.definition = definition;
		this.listener = listener; 
		
		super.complete();
	}
	
	public SubscriptionDefinition getSimpleSubscriptionDefinition() { return definition; }
	public MessageListener getSimpleListener() { return listener; }

	@Override
	public int compareTo( SubscriptionListenerPair o )
	{
		return getSimpleSubscriptionDefinition().compareTo(o.getSimpleSubscriptionDefinition());
	}

	@Override
	public void freeze() {}

	@Override
	public void normalize() {}

	@Override
	public void validate()
	{
		Validator.notNull(definition, listener);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(definition, listener);
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof SubscriptionListenerPair) ) return false;
		
		SubscriptionListenerPair other = (SubscriptionListenerPair)obj;
		
		if (!Objects.equals(getSimpleSubscriptionDefinition(), other.getSimpleSubscriptionDefinition()) ) return false;
		
		if ( other.getSimpleListener() == getSimpleListener() ) return true;
		if ( Objects.equals(other.getSimpleListener(), getSimpleListener())) return true;
		
		return false;
	}

	@Override
	public String toString()
	{
		return String.format("{ sub = %s, listener = %s }", definition.toString(), listener.toString());
	}
}

