package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;

public class StubStorage implements IStorage
{

	@Override
	public boolean exists(ObjectIdStorageKey key, boolean default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsert(ObjectIdStorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public byte[] getCurrentVersion(ObjectIdStorageKey key, byte[] default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean delete(ObjectIdStorageKey key)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public Iterable<ObjectIdStorageKey> listComplex(Kind kind, Iterable<ObjectIdStorageKey> default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsert(Storable obj, Format format)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean exists(Storable obj, boolean default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean delete(Storable obj)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean isReadOnly()
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public StorageMetadata getObjectMetadata(Storable obj, StorageMetadata default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}
}
