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
	// CODE REVIEW: These methods need javadoc
	
	abstract public Kind getSimpleKind();
	abstract public ObjectId getSimpleObjectId();
	abstract public String serialize(Format format);
	
	// Code review: space this out (I did it for you)
	
	public StorageKey createStorageKey()
	{
		return new StorageKey(getSimpleKind(), getSimpleObjectId(), "xml"); // CODE REVEIW: The third paramater should be StorageKeyExtension, not a string. Since it will be used so much, I suggest creating a static private final StorageKeyExtension XML = new StorageKeyExtension("xml"); in StorageKeyExtension
	}
}
