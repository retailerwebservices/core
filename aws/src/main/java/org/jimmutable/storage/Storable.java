package org.jimmutable.storage;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;

/**
 *
 * @author andrew.towe This class is designed to help us with object storage.
 *         All things that can be stored will be a child of this class.
 *
 */

public abstract class Storable
{

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
	abstract public String serialize( Format format );

	
	
	
	/**
	 * @return a new Storage Key with an extension type of XML based on the Kind and
	 *         ObjectId of the Storable object.
	 */
	public StorageKey createStorageKey()
	{
		return new StorageKey(getSimpleKind(), getSimpleObjectId(), StorageKeyExtension.XML);
	}
}
