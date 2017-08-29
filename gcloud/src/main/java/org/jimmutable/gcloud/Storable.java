package org.jimmutable.gcloud;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;


public abstract class Storable {
	abstract public Kind getSimpleKind();
	abstract public ObjectId getSimpleObjectId();
	abstract public String serialize(Format format);
	public StorageKey createStorageKey() {
		return new StorageKey(getSimpleKind(), getSimpleObjectId(), StorageKeyExtension.XML);
	}

}