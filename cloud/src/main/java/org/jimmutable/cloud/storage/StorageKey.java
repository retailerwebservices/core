package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;

/**
 * This interface represents the abstraction of a storage key. In practice, this let's us store objects in a file system, without requiring the SKName to be a
 * valid ObjectId, as was the case before. However, it's still strict enough to require us to specify all parts to uniquely determine a file in a file system.
 * @author salvador.salazar
 *
 */
public interface StorageKey
{
	public Kind getSimpleKind();
	public StorageKeyName getSimpleName();
	public StorageKeyExtension getSimpleExtension();
	
	/**
	 * Default toString implementation that returns [kind]/[name].[extension]
	 * @return
	 */
	public String toString();
}
