package org.jimmutable.cloud.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.threading.OperationPool;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.utils.IOUtils;
import org.jimmutable.core.utils.Validator;

/**
 *
 * @author andrew.towe This class is the parent of our file storage handlers. It
 *         is designed to handle all updates, insertions, checking of existence,
 *         listing of objects, and deletions.
 */

public abstract class Storage implements IStorage
{
	/**
	 * The largest {@code byte[]} that can be
	 * {@link #upsert(StorageKey, byte[], boolean) upserted} or
	 * {@link #getCurrentVersion(StorageKey, byte[]) retrieved}.
	 */
	static public final int MAX_TRANSFER_BYTES_IN_MB = 25;
	static public final int MAX_TRANSFER_BYTES_IN_BYTES = MAX_TRANSFER_BYTES_IN_MB * 1024 * 1024;
	protected StandardImmutableObjectCache cache = null;

	// Storage instance = null;
	private boolean is_readonly = false;

	protected Storage( boolean is_readOnly )
	{
		this.is_readonly = is_readOnly;
	}

	protected Storage( boolean is_read_only, StandardImmutableObjectCache cache )
	{
		this.is_readonly = is_read_only;
		this.cache = cache;
	}

	public boolean isReadOnly()
	{
		return is_readonly;
	}

	/**
	 * Retrieves the StorageMetadata associated to this Storable object, returning
	 * the default_value if lookup fails. (Object doesn't exist, or internal error)
	 */
	public StorageMetadata getObjectMetadata( Storable obj, StorageMetadata default_value )
	{
		if ( obj == null )
			return default_value;

		return getObjectMetadata(obj.createStorageKey(), default_value);
	}

	public boolean upsert( Storable obj, Format format )
	{
		Validator.notNull(obj);

		if ( isReadOnly() )
			return false;

		return upsert(obj.createStorageKey(), ObjectWriter.serialize(format, obj).getBytes(), true);
	}

	@Override
	public boolean upsert( final StorageKey key, final byte[] bytes, final boolean hint_content_likely_to_be_compressible )
	{
		Validator.max(bytes.length, MAX_TRANSFER_BYTES_IN_BYTES);

		if ( isReadOnly() )
			return false;

		boolean successful = upsertStreaming(key, new ByteArrayInputStream(bytes), hint_content_likely_to_be_compressible);
		if ( successful )
		{
			removeFromCache(key);
		}
		return successful;
	}

	@Override
	@SuppressWarnings("resource")
	public byte[] getCurrentVersion( final StorageKey key, final byte[] default_value )
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		OutputStream out = new IOUtils.LimitBytesOutputStream(bytes, MAX_TRANSFER_BYTES_IN_BYTES);
		if ( isCacheEnabled() )
		{

			byte[] byte_information = cache.get(cache.createCacheKey(key), default_value);
			if ( (byte_information != null) && (!byte_information.equals(default_value)) )
			{
				return byte_information;
			}
		}

		final boolean result = getCurrentVersionStreaming(key, out);

		if ( result )
		{
			try
			{
				addToCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()), (StandardImmutableObject) StandardObject.deserialize(new String(bytes.toByteArray(), "UTF8")));
			}
			catch ( Exception e )
			{
				LogManager.getRootLogger().error("Failure to make into a StandardImmutableObject " + key.toString() + ". This object is not in the cache.", e);
			}
			return bytes.toByteArray();
		}
		else
		{
			return default_value;
		}
	}

	public boolean exists( Storable obj, boolean default_value )
	{
		if ( obj == null )
			return default_value;

		try
		{
			return exists(obj.createStorageKey(), false);
		}
		catch ( Exception e )
		{
			LogManager.getRootLogger().error("Failure to list object " + obj, e);
			return default_value;
		}
	}

	public boolean delete( Storable obj )
	{
		if ( obj == null )
			return false;
		if ( isReadOnly() )
			return false;

		boolean successful = delete(obj.createStorageKey());

		return successful;
	}

	@Override
	public boolean scan( Kind kind, StorageKeyHandler handler, int num_handler_threads )
	{
		return scan(kind, null, handler, num_handler_threads);
	}

	@Override
	public boolean scan( final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads )
	{
		return scanImpl(kind, prefix, handler, num_handler_threads, false);
	}

	@Override
	public boolean scanForObjectIds( Kind kind, StorageKeyHandler handler, int num_handler_threads )
	{
		return scanForObjectIds(kind, null, handler, num_handler_threads);
	}

	@Override
	public boolean scanForObjectIds( final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads )
	{
		return scanImpl(kind, prefix, handler, num_handler_threads, true);
	}

	private boolean scanImpl( final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads, final boolean only_object_ids )
	{
		Scanner scanner = createScanner(kind, prefix, only_object_ids);
		OperationPool pool = new OperationPool(scanner, num_handler_threads);

		scanner.setSink(( StorageKey key ) ->
		{
			pool.submitOperation(new StorageKeyHandlerWorker(handler, key));
		});

		OperationRunnable.Result result = OperationRunnable.execute(pool, OperationRunnable.Result.ERROR);
		return OperationRunnable.Result.SUCCESS == result;
	}

	protected boolean isCacheEnabled()
	{
		// - returns true if cache is not null
		return cache != null;
	}

	protected byte[] getComplexCurrentVersionFromCache( final StorageKey key, final byte[] default_value )// - Try to load the data from StandardImmutableObjectCache if isCacheEnabled()
																											// is true, otherwise, return default_value.
	{
		if ( isCacheEnabled() )
		{
			// @CR - I had an oversight in the design. I thought we could extract info from
			// StorageKey and use the other get method:
			// public StandardImmutableObject get( Kind kind, ObjectId id,
			// StandardImmutableObject default_value ).
			// Instead of creating the CacheKey here, use the new method createCacheKey from
			// StandardImmutableObjectCache.
			// See comments in StandardImmutableObjectCache for more details.
			// -PM
			return cache.get(cache.createCacheKey(key), default_value);
		}
		return default_value;
	}

	protected void addToCache( Kind kind, ObjectId id, StandardImmutableObject object )// - calls cache.put to add it to the cache.
	{
		if ( isCacheEnabled() )
		{
			// Instead of creating the CacheKey here, use the new method
			// createCacheKey(kind, id, null) from StandardImmutableObjectCache. -PM
			cache.put(cache.createCacheKey(kind, id), object);
		}
	}

	protected void removeFromCache( Kind kind, ObjectId id )// - calls cache.remove if isCacheEnabled() is true.
	{
		if ( isCacheEnabled() )
		{
			cache.remove(kind, id);
		}
	}

	protected void removeFromCache( StorageKey key )// - calls cache.remove if isCacheEnabled() is true.
	{
		if ( isCacheEnabled() )
		{
			cache.remove(cache.createCacheKey(key));
		}
	}

	static private class StorageKeyHandlerWorker extends OperationRunnable
	{
		private final StorageKeyHandler handler;
		private final StorageKey key;

		public StorageKeyHandlerWorker( final StorageKeyHandler handler, final StorageKey key )
		{
			this.handler = handler;
			this.key = key;
		}

		@Override
		protected Result performOperation() throws Exception
		{
			if ( null == handler )
				return Result.SUCCESS;

			handler.handle(key);

			return Result.SUCCESS;
		}
	}

	/**
	 * This class does the main listing operation for scan*. It runs in it's own
	 * thread and throws each StorageKey it finds into another OperationRunnable
	 * running in a common pool.
	 *
	 * @author Jeff Dezso
	 */
	abstract protected class Scanner extends OperationRunnable
	{
		private final Kind kind;
		private final StorageKeyName prefix;
		private final boolean only_object_ids;

		private Consumer<StorageKey> sink;

		public Scanner( final Kind kind, final StorageKeyName prefix, final boolean only_object_ids )
		{
			Validator.notNull(kind, "kind");

			this.kind = kind;
			this.prefix = prefix;
			this.only_object_ids = only_object_ids;
		}

		protected Kind getSimpleKind()
		{
			return kind;
		}

		protected boolean hasPrefix()
		{
			return null != prefix;
		}

		protected StorageKeyName getOptionalPrefix( StorageKeyName default_value )
		{
			if ( null != prefix )
			{
				return prefix;
			}

			return default_value;
		}

		protected boolean onlyObjectIds()
		{
			return only_object_ids;
		}

		/**
		 * The sink has to be set after construction to avoid a race condition between
		 * construction of the OperationPool and construction the seed OperationRunnable
		 * 
		 * @param handler
		 */
		public void setSink( Consumer<StorageKey> sink )
		{
			this.sink = sink;
		}

		protected void emit( StorageKey key )
		{
			if ( null != sink )
			{
				sink.accept(key);
			}
		}
	}

	abstract protected Scanner createScanner( final Kind kind, final StorageKeyName prefix, final boolean only_object_ids );
}
