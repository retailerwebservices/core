package org.jimmutable.cloud.storage;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.CacheActivity;
import org.jimmutable.cloud.cache.CacheEvent;
import org.jimmutable.cloud.cache.CacheEventListener;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.cloud.cache.CacheMetric;
import org.jimmutable.cloud.cache.ICache;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

@SuppressWarnings("rawtypes")
public class StandardImmutableObjectCache
{
	private ICache cache;
	private String prefix; // required
	private long max_allowed_entry_age_in_ms = TimeUnit.MINUTES.toMillis(20);
	private SignalTopicId topic_id = null;

	private static final Logger logger = LogManager.getLogger(StandardImmutableObjectCache.class);

	public StandardImmutableObjectCache( ICache cache, String prefix )
	{
		this.cache = cache;
		this.prefix = prefix;
		this.topic_id = new SignalTopicId(prefix);

	}

	public StandardImmutableObjectCache( ICache cache, String prefix, long max_allowed_entry_age_in_ms )// - replaces value in this.max_allowed_entry_age_in_ms.
	{
		this.cache = cache;
		this.prefix = prefix;
		this.max_allowed_entry_age_in_ms = max_allowed_entry_age_in_ms;
		this.topic_id = new SignalTopicId(prefix);

	}

	public void createListeners()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSignalService().startListening(this.topic_id, new CacheEventListener());
	}// - In here, register CacheEventListener for StandardImmutableObjectCache by
		// calling
		// CloudExecutionEnvironment.getSimpleCurrent().getSimpleSignalService().startListening.
		// Use the topic_id created above.

	private void createAndSendEvent( CacheActivity activity, CacheMetric metric, CacheKey key )
	{
		Builder b = new Builder(CacheEvent.TYPE_NAME);
		b.set(CacheEvent.FIELD_ACTIVITY, activity);
		b.set(CacheEvent.FIELD_METRIC, metric);
		b.set(CacheEvent.FIELD_KEY, key);
		b.set(CacheEvent.FIELD_TIMESTAMP, System.currentTimeMillis());
		CacheEvent cache_event = b.create();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSignalService().sendAsync(topic_id, cache_event);
	}

	public void put( Kind kind, ObjectId id, StandardImmutableObject object )
	{
		if ( kind == null || id == null || object == null )
			return;
		// @CR - Replace CacheKey creation with call to createCacheKey(kind, id, null)
		// and check for null before using it. -PM
		put(new CacheKey(getCachePrefix() + kind.toString() + ":" + id.toString()), object);
	}

	public void put( CacheKey cache_key, StandardImmutableObject object )
	{
		if ( cache_key == null || object == null )
			return;
		cache.put(cache_key, object, max_allowed_entry_age_in_ms);
		createAndSendEvent(CacheActivity.PUT, CacheMetric.ADD, cache_key);
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
	public <T extends Storable> boolean has( T object )
	{
		if ( object == null )
			return false;
		return has(object.getSimpleKind(), object.getSimpleObjectId());
	}

	public boolean has( Kind kind, ObjectId id )
	{
		if ( kind == null || id == null )
			return false;
		return has(new CacheKey(getCachePrefix() + kind.toString() + ":" + id.toString()));
	}

	private String getCachePrefix()
	{
		return CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId().getSimpleValue() + "://" + prefix + ":";
	}
	
	public CacheKey createCacheKey( StorageKey key)
	{
		return new CacheKey(getCachePrefix() +  key.getSimpleKind() + ":" + key.getSimpleName().getSimpleValue());
	}
	

	public CacheKey createCacheKey( Kind kind, ObjectId id )
	{
		return new CacheKey(getCachePrefix() +  kind.toString() + ":" + id.toString());
	}

	public boolean has( CacheKey cache_key )
	{
		if ( cache_key == null )
		{
			return false;
		}
		return cache.exists(cache_key);
	}

	public StandardImmutableObject get( Kind kind, ObjectId id, StandardImmutableObject default_value )
	{
		if ( kind == null || id == null )
		{
			return default_value;
		}
		return get(createCacheKey(kind, id), default_value);
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
	public StandardImmutableObject get( CacheKey reference, StandardImmutableObject default_value )
	{
		if ( reference == null )
		{
			createAndSendEvent(CacheActivity.GET, CacheMetric.MISS, reference);
			return default_value;
		}

		StandardImmutableObject standard_immutable_object = null;

		// if you did not find it in the cache go find it in storage.

		byte[] bytes = cache.getBytes(reference, null);
		if ( bytes == null )
		{
			logger.error(String.format("Failed to retreive %s from storage!", reference.getSimpleValue()));
			createAndSendEvent(CacheActivity.GET, CacheMetric.MISS, reference);
			return default_value;
		}

		try
		{
			standard_immutable_object = (StandardImmutableObject) ObjectParseTree.deserialize(new String(bytes));
		}
		catch ( Exception e )
		{

			logger.error(String.format("Failed to serialize %s!", reference.getSimpleValue()), e);
		}

		if ( standard_immutable_object != null )
		{
			// if there is something, cache it and return it.
			cache.put(reference, standard_immutable_object, max_allowed_entry_age_in_ms);
			createAndSendEvent(CacheActivity.GET, CacheMetric.HIT, reference);
			return standard_immutable_object;
		}
		// else return default_value.
		createAndSendEvent(CacheActivity.GET, CacheMetric.MISS, reference);
		return default_value;
	}

	public byte[] get( CacheKey reference, byte[] default_value )
	{
		if ( reference == null )
		{
			createAndSendEvent(CacheActivity.GET, CacheMetric.MISS, reference);
			return default_value;
		}

		// if you did not find it in the cache go find it in storage.

		byte[] bytes = cache.getBytes(reference, null);
		if ( bytes == null )
		{
			createAndSendEvent(CacheActivity.GET, CacheMetric.MISS, reference);
			return default_value;
		}

		cache.put(reference, bytes, max_allowed_entry_age_in_ms);
		createAndSendEvent(CacheActivity.GET, CacheMetric.HIT, reference);
		return bytes;
	}

	public void remove( CacheKey reference )
	{
		cache.delete(reference);
		createAndSendEvent(CacheActivity.REMOVE, CacheMetric.REMOVE, reference);
	}

	public void remove( Kind kind, ObjectId id )
	{
		if ( kind == null || id == null )
		{
			return;
		}
		CacheKey cache_key = createCacheKey(kind, id);
		cache.delete(cache_key);
		createAndSendEvent(CacheActivity.REMOVE, CacheMetric.REMOVE, cache_key);
	}

}
