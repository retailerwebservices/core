package org.jimmutable.cloud.storage;

import java.util.concurrent.TimeUnit;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.MessageListener;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.threading.ExpirationCache;

@SuppressWarnings("rawtypes")
public class StandardImmutableObjectCache
{
    // CODEREVIEW Why does UserManager have it's own StandardImmutableObjectCache? Shouldn't it use the one in CloudExecutionEnvironment? - JMD
    
    // CODEREVIEW Fun fact: You can write literal numbers in Java with underscores, e.g. 100_000. (I find these to be more readable.) - JMD
	private ExpirationCache<ObjectReference, StandardImmutableObject> cache = new ExpirationCache<>(TimeUnit.MINUTES.toMillis(5), 100000);

	public void put( Kind kind, ObjectId id, StandardImmutableObject object )
	{
		put(new ObjectReference(kind, id), object);
	}

	public void put( ObjectReference object_reference, StandardImmutableObject object )
	{
	    // CODEREVIEW You probably want to silently ignore null keys _and_ null values. Maps with null in them are confusing as heck. - JMD
		cache.put(object_reference, object);
	}

	/*
	 * CODEREVIEW
	 * First, I love that you're using generics!
	 * 
	 * Second, I though that our conversation with Jim established
	 * that this cache should not - cannot, really - be tied to Storable.
	 * The governing interface is StandardImmutableObject and ObjectReference.
	 * 
	 * So, as useful as this method seems to be, I'm not sure it's a good idea
	 * to include it, if only to prevent the brain damage of tricking some
	 * hapless future dev into thinking that this cache only works for Storables.
	 *
	 * - JMD
	 */
	public <T extends Storable> boolean has( T object)
	{
		return has(new ObjectReference(object.getSimpleKind(),object.getSimpleObjectId()));
	}
	
	public boolean has( Kind kind, ObjectId id)
	{
	    /*
	     * CODEREVIEW
	     * This is a matter of taste, but I want to call the question:
	     * Is the team okay that if either argument is null, then this
	     * method throws a RuntimeException? The other way to code it would
	     * be to explicitly check null and return false. 
	     * 
	     * (There's no right answer; the team has to decide how the system
	     *  should behave. For my 2 cents, I think the exception will be
	     *  unexpected in this context. Generally, I love exceptions, but I
	     *  don't think they are the right default in this case.)
	     * 
	     * - JMD
	     */
		return has(new ObjectReference(kind, id));
	}

	public boolean has( ObjectReference object_reference )
	{
		return cache.has(object_reference);
	}

	public StandardImmutableObject get( Kind kind, ObjectId id, StandardImmutableObject default_value )
	{
	    // CODEREVIEW See the above discussion about RuntimeException from the ObjectReference constructor. - JMD
		return get(new ObjectReference(kind, id), default_value);
	}

	// CODEREVIEW I see that Panda uses getSimple and getOptional. Do you also have the idea of getComplex? (because this (and the overload) would be a complex method) - JMD
	public StandardImmutableObject get( ObjectReference reference, StandardImmutableObject default_value )
	{
	    /*
	     * CODEREVIEW
	     * 
	     * So this is at least partly a matter of style. Personally,
	     * I hate to make two - possibly expensive, possibly not thread
	     * safe - calls into Maps. I prefer the pattern when where I do
	     * the get and then do my own "has" check after.
	     * 
	     * SIO obj = cache.getOptional(ref, null);
	     * if (null != obj) return obj;
	     * ...
	     * 
	     * - JMD
	     */
		if ( cache.has(reference) )
		{
			return cache.getOptional(reference, default_value);
		}

		byte[] object = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new StorageKey(reference.getSimpleKind(), reference.getSimpleObjectId(), StorageKeyExtension.JSON), new byte[0]);

		StandardImmutableObject standard_immutable_object = (StandardImmutableObject) ObjectParseTree.deserialize(new String(object));
		if ( standard_immutable_object != null )
		{
			cache.put(reference, standard_immutable_object);
		}
		
		// CODEREVIEW If standard_immutable_object _is_ null, don't you want to return default_value? - JMD
		return standard_immutable_object;
	}

	public void remove( ObjectReference reference )
	{
		cache.remove(reference);
	}

	public class UpsertListener implements MessageListener
	{
	    // CODEREVIEW Uhh.... Nothing uses UpsertListener. Shouldn't it bind somewhere? - JMD

		@Override
		public void onMessageReceived( StandardObject message )
		{
			if ( message instanceof StandardMessageOnUpsert )
			{
				StandardMessageOnUpsert standard_message = (StandardMessageOnUpsert) message;
				cache.remove(new ObjectReference(standard_message.getSimpleKind(), standard_message.getSimpleObjectId()));
			}
		}

	}
}
