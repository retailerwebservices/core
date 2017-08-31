package org.jimmutable.gcloud;

import java.io.ByteArrayInputStream;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.core.objects.common.Kind;

import com.google.appengine.repackaged.com.google.common.primitives.Bytes;
public abstract class Storage {
	Storage instance = null;
	private boolean is_readOnly=false;
	public Storage(boolean is_readOnly) {
		this.is_readOnly=is_readOnly;
	}
	public Storage getSimpleInstance() {
		if(instance ==null) {
			if(ApplicationId.hasOptionalDevApplicationId()) {
				instance = new StorageDevLocalFileSystem(isReadOnly());
			}else {
				instance = new StorageGoogleCloudStorage(isReadOnly());
			}
		}
		return instance;
	}

	public abstract boolean exists(StorageKey key, boolean default_value);
	public abstract boolean upsert(StorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);
	public abstract byte[] getCurrentVersion(StorageKey key, byte[] default_value);
	public abstract boolean delete(StorageKey key);
	public abstract Iterable<StorageKey> list(Kind kind);

	public boolean upsert(Storable obj) {
		Validator.notNull(obj);
		return upsert(obj.createStorageKey(),null,true);
	}
	public boolean exists(Storable obj, boolean default_value) {
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
	
	protected boolean isReadOnly()
	{
		return is_readOnly;
	}

}
