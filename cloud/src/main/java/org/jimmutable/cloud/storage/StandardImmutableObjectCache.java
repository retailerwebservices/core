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
	private ExpirationCache<ObjectReference, StandardImmutableObject> cache = new ExpirationCache<>(TimeUnit.MINUTES.toMillis(5), 100000);

	public void put( Kind kind, ObjectId id, StandardImmutableObject object )
	{
		put(new ObjectReference(kind, id), object);
	}

	public void put( ObjectReference object_reference, StandardImmutableObject object )
	{
		cache.put(object_reference, object);
	}

	public <T extends Storable> boolean has( T object)
	{
		return has(new ObjectReference(object.getSimpleKind(),object.getSimpleObjectId()));
	}
	
	public boolean has( Kind kind, ObjectId id)
	{
		return has(new ObjectReference(kind, id));
	}

	public boolean has( ObjectReference object_reference )
	{
		return cache.has(object_reference);
	}

	public StandardImmutableObject get( Kind kind, ObjectId id, StandardImmutableObject default_value )
	{
		return get(new ObjectReference(kind, id), default_value);
	}

	public StandardImmutableObject get( ObjectReference reference, StandardImmutableObject default_value )
	{
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
		return standard_immutable_object;
	}

	public void remove( ObjectReference reference )
	{
		cache.remove(reference);
	}

	public class UpsertListener implements MessageListener
	{

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
