package org.jimmutable.storage;


import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
/**
 *
 * @author andrew.towe
 * This class is designed to help us with object storage. All things that can be stored will be a child of this class.
 *
 */

public abstract class Storable
{
	abstract public Kind getSimpleKind();
	abstract public ObjectId getSimpleObjectId();
	abstract public String serialize(Format format);
	public StorageKey createStorageKey()
	{
		return new StorageKey(getSimpleKind(), getSimpleObjectId(), "xml");
	}
}
