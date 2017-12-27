package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;

public interface IStorage
{

	public boolean exists(ObjectIdStorageKey key, boolean default_value);

	public abstract boolean upsert(ObjectIdStorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);

	public abstract byte[] getCurrentVersion(ObjectIdStorageKey key, byte[] default_value);

	public abstract boolean delete(ObjectIdStorageKey key);

	public abstract Iterable<ObjectIdStorageKey> listComplex(Kind kind, Iterable<ObjectIdStorageKey> default_value);

	public abstract Iterable<StorageKey> listComplex(StorageKey prefix, Iterable<StorageKey> default_value);
	
	public boolean upsert(Storable obj, Format format);

	public boolean exists(Storable obj, boolean default_value);

	public boolean delete(Storable obj);

	public boolean isReadOnly();
}
