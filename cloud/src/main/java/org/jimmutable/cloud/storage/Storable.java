package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;

/**
 *
 * @author andrew.towe This class is designed to help us with object storage.
 *         All things that can be stored will be a child of this class.
 *
 */

public interface Storable
{

	public static final StorageKeyExtension STORABLE_EXTENSION = StorageKeyExtension.JSON;

	/**
	 * @return The Kind for the Storable Object
	 */
	abstract public Kind getSimpleKind();

	/**
	 * 
	 * @return The ObjectId for the Storable Object
	 */
	abstract public ObjectId getSimpleObjectId();

	/**
	 * 
	 * @param format
	 *            you want the Storable Object to be formatted to
	 * @return the serialized version of the object
	 */
	abstract public String serialize(Format format);

	/**
	 * @return a new Storage Key with an extension type of XML based on the Kind and
	 *         ObjectId of the Storable object.
	 */
	default public ObjectIdStorageKey createStorageKey()
	{
		return new ObjectIdStorageKey(getSimpleKind(), getSimpleObjectId(), STORABLE_EXTENSION);
	}

	static public ObjectIdStorageKey createStorageKey(Kind kind, ObjectId id)
	{
		return new ObjectIdStorageKey(kind, id, STORABLE_EXTENSION);
	}
}
