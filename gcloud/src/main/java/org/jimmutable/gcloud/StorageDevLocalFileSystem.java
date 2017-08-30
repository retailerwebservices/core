package org.jimmutable.gcloud;

import org.jimmutable.core.objects.common.Kind;

import com.google.appengine.repackaged.com.google.common.primitives.Bytes;

public class StorageDevLocalFileSystem extends Storage{

	public StorageDevLocalFileSystem(boolean is_readOnly) {
		super(is_readOnly);
	}

	@Override
	public boolean exists(StorageKey key, boolean default_value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean upsert(StorageKey key, Bytes[] bytes, boolean hint_content_likely_to_be_compressible) {
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
