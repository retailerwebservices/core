package org.jimmutable.cloud.storage;

import java.io.InputStream;
import java.io.OutputStream;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;

/**
 * An interface representation of Storage. s notions of things like exists,
 * upsert, deletions, etc.
 * 
 * @author salvador.salazar
 *
 */
public interface IStorage
{
	/**
	 * @param key
	 *            of the Storable object that we are looking for.
	 * @param default_value
	 *            to be returned if object is not found
	 * @return true if object is found, else Default_value
	 */
	public boolean exists(StorageKey key, boolean default_value);

	/**
	 * @param key
	 *            of the Storable Object to Update/Insert
	 * @param bytes
	 *            The bytes to put in storage. Must not be larger than
	 *            {@value Storage#MAX_TRANSFER_BYTES_IN_MB} MB.
	 * @param hint_content_likely_to_be_compressible
	 * 
	 * @return true if the Object was updated/inserted, else false
	 */
	public boolean upsert(StorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);

	/**
	 * Upsert all bytes that can be read from {@code source}. There is no limit on
	 * the size of the stream that can be upserted. {@code source} will <em>not</em>
	 * be {@link InputStream#close() closed}.
	 * 
	 * @param key
	 *            of the Storable Object to Update/Insert
	 * @param source
	 *            a stream that, when read, returns the bytes to be stored
	 * @param hint_content_likely_to_be_compressible
	 * 
	 * @return true if the Object was updated/inserted, else false
	 */
	public boolean upsertStreaming(final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible);

	/**
	 * This method will not read objects larger than
	 * {@value Storage#MAX_TRANSFER_BYTES_IN_MB} MB. If you need to retrieve large
	 * objects, use {@link #getCurrentVersionStreaming(StorageKey, OutputStream)}.
	 * 
	 * @param key
	 *            key associated with Stored Object you want to retrieve the current
	 *            version of.
	 * @param default_value
	 *            If the object is not found, what would you like returned.
	 * @return Byte array of Stored object if Object was found, otherwise
	 *         default_value
	 */
	public byte[] getCurrentVersion(StorageKey key, byte[] default_value);

	/**
	 * Get the current version of {@code key}. There is no limit on the size of the
	 * object to be retrieved. {@code sink} will be {@link OutputStream#flush()
	 * flushed} but not {@link OutputStream#close() closed}.
	 * 
	 * @param key
	 *            key associated with Stored Object you want to retrieve the current
	 *            version of.
	 * @param sink
	 *            The {@link OutputStream} where the bytes of the object will be
	 *            written
	 * @return Byte array of Stored object if Object was found, otherwise
	 *         default_value
	 */
	public boolean getCurrentVersionStreaming(final StorageKey key, final OutputStream sink);

	/**
	 * @param key
	 *            StorageKey associated with StorageObject
	 * @return true if Storage Object existed and was deleted, false otherwise
	 */
	public boolean delete(StorageKey key);

	/**
	 * Scan the {@code IStorage} system for all keys of {@link Kind} {@code kind}.
	 * Processing of the found keys is done in a managed thread pool. The thread
	 * pool is entirely managed by the {@code scan} implementation, and the call to
	 * {@code scan} blocks until all scanning <b>and</b> processing is completed.
	 * 
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param handler
	 *            A callback that will be called for each found storage key. It is
	 *            okay to do heavyweight processing in this handler, since it will
	 *            run in a thread pool managed by the {@code IStorage}
	 *            implementation.
	 * @param num_handler_threads
	 *            The number of worker threads that will be used to process found
	 *            storage keys
	 * 
	 * @return {@code true} if the scan operation completed normally. {@code false}
	 *         otherwise (e.g. encountered an error).
	 */
	public boolean scan(Kind kind, StorageKeyHandler handler, int num_handler_threads);

	/**
	 * Scan the {@code IStorage} system for all keys of {@link Kind} {@code kind}.
	 * Processing of the found keys is done in a managed thread pool. The thread
	 * pool is entirely managed by the {@code scan} implementation, and the call to
	 * {@code scan} blocks until all scanning <b>and</b> processing is completed.
	 * 
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param prefix
	 *            Only keys that start with {@code prefix} will be processed
	 * @param handler
	 *            A callback that will be called for each found storage key. It is
	 *            okay to do heavyweight processing in this handler, since it will
	 *            run in a thread pool managed by the {@code IStorage}
	 *            implementation.
	 * @param num_handler_threads
	 *            The number of worker threads that will be used to process found
	 *            storage keys
	 * 
	 * @return {@code true} if the scan operation completed normally. {@code false}
	 *         otherwise (e.g. encountered an error).
	 */
	public boolean scan(Kind kind, StorageKeyName prefix, StorageKeyHandler handler, int num_handler_threads);

	/**
	 * Scan the {@code IStorage} system for all keys of {@link Kind} {@code kind}.
	 * Processing of the found keys is done in a managed thread pool. The thread
	 * pool is entirely managed by the {@code scan} implementation, and the call to
	 * {@code scan} blocks until all scanning <b>and</b> processing is completed.
	 * </p>
	 * This version of {@code scan} will return <em>only</em> {@link ObjectId
	 * ObjectId's}. This is useful in applications where object storage is mixed.
	 * 
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param handler
	 *            A callback that will be called for each found storage key. It is
	 *            okay to do heavyweight processing in this handler, since it will
	 *            run in a thread pool managed by the {@code IStorage}
	 *            implementation.
	 * @param num_handler_threads
	 *            The number of worker threads that will be used to process found
	 *            storage keys
	 * 
	 * @return {@code true} if the scan operation completed normally. {@code false}
	 *         otherwise (e.g. encountered an error).
	 */
	public boolean scanForObjectIds(Kind kind, StorageKeyHandler handler, int num_handler_threads);

	/**
	 * Scan the {@code IStorage} system for all keys of {@link Kind} {@code kind}.
	 * Processing of the found keys is done in a managed thread pool. The thread
	 * pool is entirely managed by the {@code scan} implementation, and the call to
	 * {@code scan} blocks until all scanning <b>and</b> processing is completed.
	 * </p>
	 * This version of {@code scan} will return <em>only</em> {@link ObjectId
	 * ObjectId's}. This is useful in applications where object storage is mixed.
	 * 
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param prefix
	 *            Only keys that start with {@code prefix} will be processed
	 * @param handler
	 *            A callback that will be called for each found storage key. It is
	 *            okay to do heavyweight processing in this handler, since it will
	 *            run in a thread pool managed by the {@code IStorage}
	 *            implementation.
	 * @param num_handler_threads
	 *            The number of worker threads that will be used to process found
	 *            storage keys
	 * 
	 * @return {@code true} if the scan operation completed normally. {@code false}
	 *         otherwise (e.g. encountered an error).
	 */
	public boolean scanForObjectIds(Kind kind, StorageKeyName prefix, StorageKeyHandler handler, int num_handler_threads);

	public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value);

	public StorageMetadata getObjectMetadata(Storable obj, StorageMetadata default_value);

	public boolean upsert(Storable obj, Format format);

	public boolean exists(Storable obj, boolean default_value);

	public boolean delete(Storable obj);

	public boolean isReadOnly();

	/**
	 * <b>***Use this for downloading large files***</b>
	 * <br>
	 * <br>
	 * Get the current version of {@code key} if the file is expected to be greater than 100MB.
	 * There is no limit on the size of the object to be retrieved. {@code sink}
	 * will be {@link OutputStream#flush() flushed} but not
	 * {@link OutputStream#close() closed}.
	 * 
	 * @param key
	 *            key associated with Stored Object you want to retrieve the current
	 *            version of.
	 * @param sink
	 *            The {@link OutputStream} where the bytes of the object will be
	 *            written
	 * @return Byte array of Stored object if Object was found, otherwise
	 *         default_value
	 */
	public boolean getThreadedCurrentVersionStreaming(StorageKey storage_key, OutputStream out);
}
