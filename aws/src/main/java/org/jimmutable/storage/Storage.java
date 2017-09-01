package org.jimmutable.storage;

import org.jimmutable.core.utils.Validator;
import org.jimmutable.core.objects.common.Kind;

/**
 *
 * @author andrew.towe
 * This class is the parent of our file storage handlers.
 * It is designed to handle all updates, insertions, checking of existence, listing of objects, and deletions.
 */

// CODE REVIEW:  Fix your spacing.  { on newline, and it if ( instance == null ) etc., newlines between methods etc.

public abstract class Storage {

	Storage instance = null;
	private boolean is_readonly=false;
	public Storage(boolean is_readOnly) {
		this.is_readonly=is_readOnly;
	}
	
	// CODE REVIEW: this method definitely needs javadoc
	public Storage getSimpleInstance()
	{
		if(instance ==null)
		{
			if(ApplicationId.hasOptionalDevApplicationId())
			{
				instance = new StorageDevLocalFileSystem(isReadOnly());
			}
			else
			{
				instance = new StorageGoogleCloudStorage(isReadOnly());
			}
		}
		return instance;
	}

	// CODE REVEIW: All of these methods need javadoc
	public abstract boolean exists(StorageKey key, boolean default_value);
	public abstract boolean upsert(StorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);
	public abstract byte[] getCurrentVersion(StorageKey key, byte[] default_value);
	public abstract boolean delete(StorageKey key);
	
	// CODE REVEIW: Change the signature to listComplex and take a default return value (my mistake)
	public abstract Iterable<StorageKey> list(Kind kind);

	public boolean upsert(Storable obj)
	{
		Validator.notNull(obj); 
		return upsert(obj.createStorageKey(),null,true);
	}
	public boolean exists(Storable obj, boolean default_value)
	{
		if ( obj == null ) return default_value;

		try
		{
			return exists(obj.createStorageKey(),false);
		}
		catch(Exception e)
		{
//			LogManager.getRootLogger().error("Failure to list object "+obj,e);
			return default_value;
		}
	}

	public boolean delete(Storable obj)
	{
		if ( obj == null ) return false;
		if ( isReadOnly() ) return false;

		return delete(obj.createStorageKey());
	}

	public boolean isReadOnly()
	{
		return is_readonly;
	}

}
