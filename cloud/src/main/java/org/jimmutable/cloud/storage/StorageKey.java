package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;

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
