package org.jimmutable.gcloud;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;

public class StorageKey extends Stringable{
	Kind kind;
	ObjectId id;
	StorageKeyExtension extension;

	public StorageKey(String value) {
		super(value);
	}
	public StorageKey(Kind kind, ObjectId objectId, String extension) {
		this(String.format("%s/%s.%s", kind.getSimpleValue(),objectId.getSimpleValue(),extension));
	}

	@Override
	public void normalize() {
		normalizeLowerCase();
	}

	@Override
	public void validate() {
		String simpleValue = getSimpleValue();
		int placeOfDot = simpleValue.indexOf(".");
		int placeOfSlash = simpleValue.lastIndexOf("/");
		kind = new Kind(simpleValue.substring(0, placeOfSlash));
		id = new ObjectId(simpleValue.substring(placeOfSlash+1,placeOfDot));
		extension = new StorageKeyExtension(simpleValue.substring(placeOfDot+1));		
	}
	final public String getSimpleKind() { return kind.getSimpleValue(); }
	final public String getSimpleId() { return id.getSimpleValue(); }
	final public String getSimpleExtension() { return extension.getSimpleMimeType(); }

}
