package org.jimmutable.cloud.storage;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.threading.ExpirationCache;

@SuppressWarnings("rawtypes")
public class StandardImmutableObjectCache
{
	private ExpirationCache<ObjectReference, StandardImmutableObject> cache = new ExpirationCache<>(TimeUnit.MINUTES.toMillis(5), 100_000);

	private static final Logger logger = LogManager.getLogger(StandardImmutableObjectCache.class);

	/**
	 * Central topic for all standard immutable objects that are going to be
	 * upserted
	 */
	public static SignalTopicId TOPIC_ID = new SignalTopicId("standard-immutable-object-cache");

	public static void setupListeners()
	{
		UpsertListener upsert_listener = new UpsertListener();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSignalService().startListening(TOPIC_ID, upsert_listener);
	}

	public void put(Kind kind, ObjectId id, StandardImmutableObject object)
	{
		if (kind == null || id == null || object == null)
			return;
		put(new ObjectReference(kind, id), object);
	}

	public void put(ObjectReference object_reference, StandardImmutableObject object)
	{
		if (object_reference == null || object == null)
			return;
		cache.put(object_reference, object);
	}

	/*
	 * CODEREVIEW
	 * 
	 * Second, I though that our conversation with Jim established that this cache
	 * should not - cannot, really - be tied to Storable. The governing interface is
	 * StandardImmutableObject and ObjectReference.
	 * 
	 * So, as useful as this method seems to be, I'm not sure it's a good idea to
	 * include it, if only to prevent the brain damage of tricking some hapless
	 * future dev into thinking that this cache only works for Storables.
	 *
	 * - JMD
	 * 
	 * Response For you to get anything out of the cache, one needs a kind and an
	 * ObjectId. This is a requirement of ObjectReference, which we are using as the
	 * key in the cache. If a class does not have these methods the developer will
	 * have to supply them (or make them up). No, not everything in the cache must
	 * be a Storable, but being a Storable makes things easier. This is simply a way
	 * to save someone a little typing.
	 * 
	 */
	public <T extends Storable> boolean has(T object)
	{
		if (object == null)
			return false;
		return has(new ObjectReference(object.getSimpleKind(), object.getSimpleObjectId()));
	}

	public boolean has(Kind kind, ObjectId id)
	{
		if (kind == null || id == null)
			return false;
		return has(new ObjectReference(kind, id));
	}

	public boolean has(ObjectReference object_reference)
	{
		if (object_reference == null)
		{
			return false;
		}
		return cache.has(object_reference);
	}

	public StandardImmutableObject get(Kind kind, ObjectId id, StandardImmutableObject default_value)
	{
		if (kind == null || id == null)
		{
			return default_value;
		}
		return get(new ObjectReference(kind, id), default_value);
	}

	// CODEREVIEW I see that Panda uses getSimple and getOptional. Do you also have
	// the idea of getComplex? (because this (and the overload) would be a complex
	// method) - JMD
	// Response
	// Yes we do, but we use those methods for getting complex things from a
	// StandardImmutableObject specifically
	// the reason I called this method get is because when we want to acquire
	// something from a map or other storage
	// places, we use the method signature get. I am trying to stay consistent with
	// that methodology.
	public StandardImmutableObject get(ObjectReference reference, StandardImmutableObject default_value)
	{
		if (reference == null)
		{
			return default_value;
		}

		StandardImmutableObject standard_immutable_object = cache.getOptional(reference, default_value);
		if (default_value != null && standard_immutable_object.equals(default_value))
		{
			return default_value;
		}

		// if you did not find it in the cache go find it in storage.

		byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(reference.getSimpleKind(), reference.getSimpleObjectId(), StorageKeyExtension.JSON), null);

		if (bytes == null)
		{
			logger.error(String.format("Failed to retreive %s from storage!", reference.getSimpleValue()));
			return default_value;
		}

		try
		{
			standard_immutable_object = (StandardImmutableObject) ObjectParseTree.deserialize(new String(bytes));
		} catch (Exception e)
		{

			logger.error(String.format("Failed to serialize %s!", reference.getSimpleValue()), e);
		}

		if (standard_immutable_object != null)
		{
			// if there is something, cache it and return it.
			cache.put(reference, standard_immutable_object);
			return standard_immutable_object;
		}
		// else return default_value.
		return default_value;
	}

	public void remove(ObjectReference reference)
	{
		cache.remove(reference);
	}

	// we use this class in CloudExecutionEnvironment.

}
