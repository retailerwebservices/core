package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;

public interface IStorage
{

	public boolean exists(StorageKey key, boolean default_value);

	public abstract boolean upsert(StorageKey key, byte bytes[], boolean hint_content_likely_to_be_compressible);

	public abstract byte[] getCurrentVersion(StorageKey key, byte[] default_value);

	public abstract boolean delete(StorageKey key);

	public abstract Iterable<StorageKey> listComplex(Kind kind, Iterable<StorageKey> default_value);

	public boolean upsert(Storable obj);

	public boolean exists(Storable obj, boolean default_value);

	public boolean delete(Storable obj);

	public boolean isReadOnly();
}
