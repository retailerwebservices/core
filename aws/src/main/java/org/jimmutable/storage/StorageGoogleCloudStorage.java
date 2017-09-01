package org.jimmutable.storage;

import org.jimmutable.core.objects.common.Kind;

public class StorageGoogleCloudStorage extends Storage {

	public StorageGoogleCloudStorage(boolean is_readOnly) {
		super(is_readOnly);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean exists(StorageKey key, boolean default_value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean upsert(StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] getCurrentVersion(StorageKey key, byte[] default_value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(StorageKey key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<StorageKey> list(Kind kind) {
		// TODO Auto-generated method stub
		return null;
	}

}
