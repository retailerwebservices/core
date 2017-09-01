package org.jimmutable.storage;

import java.io.File;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;

/**
 * 
 * @author andrew.towe
 *	This class is designed to help us with how we store objects. 
 *	All storage objects will need a storage key. 
 *	Keys have three parts to them: the Kind, the Object Id, and the Extension of the storage object.
 *	
 */
public class StorageKey extends Stringable{
	Kind kind;
	ObjectId id;
	StorageKeyExtension extension;

	/** 
	 * @param 
	 * Creates a new Storage Key based on the string that is passed in.
	 * Therefore if the String passed in is "Alpha/123.txt"
	 * then
	 * 		the Kind will be "Alpha"
	 * 		the Object Id will be "123"
	 * 		the extension will be "txt"
	 */
	public StorageKey(String value) 
	{
		super(value);
	}
	
	/**
	 * @param kind
	 * 		the Kind used for the StorageKey
	 * @param objectId
	 * 		the ObjectId used for the StorageKey
	 * @param extension
	 * 		the extension of the StorageKey
	 */
	public StorageKey(Kind kind, ObjectId objectId, String extension) 
	{
		this(checkValidationFirst(kind, objectId, extension));
	}
	/**
	 * 
	 * /**
	 * @param kind
	 * 		the Kind used for the StorageKey
	 * @param objectId
	 * 		the ObjectId used for the StorageKey
	 * @param extension
	 * 		the extension of the StorageKey
	 *
	 * @return if Everything validates it will return a string that concatenates all of the parameters simple values. {alpha,123,"txt"}->"alpha/123.txt"
	 */
	private static String checkValidationFirst(Kind kind, ObjectId objectId, String extension) {
		Validator.notNull(kind,objectId,extension);
		return String.format("%s/%s.%s", kind.getSimpleValue(),objectId.getSimpleValue(),extension);
	}

	@Override
	public void normalize() 
	{
		normalizeLowerCase();
	}

	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		String[] breakonslash = getSimpleValue().split(File.separator);
		kind = new Kind(breakonslash[0]);
		String[] breakondot = breakonslash[1].split("\\.");
		id = new ObjectId(breakondot[0]);
		extension= new StorageKeyExtension(breakondot[1]);
		
	}
	/**
	 * @return The Kind associated with the storage Key
	 */
	 public Kind getSimpleKind() { return kind; }
	/**
	 * @return The ObjectId associated with the storage Key
	 */
	 public ObjectId getSimpleId() { return id; }
	/**
	 * @return The Extension associated with the storage Key
	 */
	 public StorageKeyExtension getSimpleExtension() { return extension; }

}