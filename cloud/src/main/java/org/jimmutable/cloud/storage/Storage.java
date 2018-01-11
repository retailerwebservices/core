package org.jimmutable.cloud.storage;

import org.jimmutable.core.utils.Validator;
import org.apache.logging.log4j.LogManager;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;

/**
 *
 * @author andrew.towe This class is the parent of our file storage handlers. It
 *         is designed to handle all updates, insertions, checking of existence,
 *         listing of objects, and deletions.
 */

public abstract class Storage implements IStorage
{
	// Storage instance = null;
	private boolean is_readonly = false;

	protected Storage(boolean is_readOnly)
	{
		this.is_readonly = is_readOnly;
	}

	/**
	 * Retrieves the StorageMetadata associated to this Storable object,
	 * returning the default_value if lookup fails. (Object doesn't exist, or internal error)
	 */
	public StorageMetadata getObjectMetadata(Storable obj, StorageMetadata default_value)
	{
		if (obj == null) return default_value;
		
		return getObjectMetadata(obj.createStorageKey(), default_value);
	}
	
	public boolean upsert(Storable obj, Format format)
	{
		Validator.notNull(obj);
		return upsert(obj.createStorageKey(), ObjectWriter.serialize(format, obj).getBytes(), true);
	}

	public boolean exists(Storable obj, boolean default_value)
	{
		if (obj == null)
			return default_value;

		try
		{
			return exists(obj.createStorageKey(), false);
		} catch (Exception e)
		{
			LogManager.getRootLogger().error("Failure to list object " + obj, e);
			return default_value;
		}
	}

	public boolean delete(Storable obj)
	{
		if (obj == null)
			return false;
		if (isReadOnly())
			return false;

		return delete(obj.createStorageKey());
	}

	public boolean isReadOnly()
	{
		return is_readonly;
	}

}
