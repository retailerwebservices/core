package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;

/**
 * An interface representation of Storage. Abstracts notions of things like exists, upsert, deletions, etc.
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
	 *            the contents of the Storable Object
	 * @param hint_content_likely_to_be_compressible
	 * @return true if the Object was updated/inserted, else false
	 */
	public boolean upsert(StorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);

	/**
	 * @param key
	 *            key associated with Stored Object you want to retrieve the current
	 *            version of.
	 * @param default_value
	 *            If the object is not found, what would you like returned.
	 * @return Byte array of Stored object if Object was found, otherwise
	 *         default_value
	 */
	public abstract byte[] getCurrentVersion(StorageKey key, byte[] default_value);

	/**
	 * @param key
	 *            StorageKey associated with StorageObject
	 * @return true if Storage Object existed and was deleted, false otherwise
	 */
	public boolean delete(StorageKey key);

	/**
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 */
	public abstract Iterable<StorageKey> listComplex(Kind kind, Iterable<StorageKey> default_value);

	/**
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param prefix
	 * 			The prefix to filter against
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 */
	public Iterable<StorageKey> listComplex(Kind kind, StorageKeyName prefix, Iterable<StorageKey> default_value);
	
	public Iterable<ObjectIdStorageKey> listAllObjectIdsComplex(Kind kind, Iterable<ObjectIdStorageKey> default_value);

	public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value);

	public StorageMetadata getObjectMetadata(Storable obj, StorageMetadata default_value);
	
	public boolean upsert(Storable obj, Format format);

	public boolean exists(Storable obj, boolean default_value);

	public boolean delete(Storable obj);

	public boolean isReadOnly();
}
