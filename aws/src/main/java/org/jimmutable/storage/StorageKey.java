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

/**
 * CODE REVIEW: 
 * 
 * Fix your spacing, { go on newlines,
 * @author kanej
 *
 */

public class StorageKey extends Stringable{
	Kind kind;
	ObjectId id;
	StorageKeyExtension extension;

<<<<<<< HEAD
	/**
	 * @param value
	 *            a new Storage Key based on the string that is passed in. Therefore
	 *            if the String passed in is "Alpha/123.txt" then the Kind will be
	 *            "alpha" the Object Id will be "123" the extension will be "txt"
=======
	/** 
	 * @param 
	 * Creates a new Storage Key based on the string that is passed in.
	 * Therefore if the String passed in is "Alpha/123.txt"
	 * then
	 * 		the Kind will be "alph"
	 * 		the Object Id will be "123"
	 * 		the extension will be "txt"
>>>>>>> origin/dev
	 */
	public StorageKey(String value) 
	{
		super(value);
	}
	
	/**
	 * @param kind
<<<<<<< HEAD
	 *            the Kind used for the StorageKey
	 * @param object_id
	 *            the ObjectId used for the StorageKey
=======
	 * 		the Kind used for the StorageKey
	 * @param objectId
	 * 		the ObjectId used for the StorageKey
>>>>>>> origin/dev
	 * @param extension
	 * 		the extension of the StorageKey
	 */
	
	// CODE REVIEW: Our field names are lower case with words separated by _
	// The extension should be be a StorageKeyExtension object
	
	public StorageKey(Kind kind, ObjectId object_id, StorageKeyExtension extension) 
	{	
		this(createStringFromComponents(kind, object_id, extension));
	}
	/**
	 * 
	 * /**
	 * @param kind
<<<<<<< HEAD
	 *            the Kind used for the StorageKey
	 * @param object_id
	 *            the ObjectId used for the StorageKey
=======
	 * 		the Kind used for the StorageKey
	 * @param objectId
	 * 		the ObjectId used for the StorageKey
>>>>>>> origin/dev
	 * @param extension
	 * 		the extension of the StorageKey
	 *
	 * @return if Everything validates it will return a string that concatenates all of the parameters simple values. {alpha,123,"txt"}->"alpha/123.txt"
	 */
	
	// CODE REVIEW: Make your method names indicative of what the function does
	// I fixed your spacing for you
	// static goes before private
	
	static private String createStringFromComponents(Kind kind, ObjectId object_id, StorageKeyExtension extension) 
	{
		Validator.notNull(kind,object_id,extension);
		return String.format("%s/%s.%s", kind.getSimpleValue(), object_id.getSimpleValue(), extension);
	}

	@Override
	public void normalize() 
	{
		normalizeLowerCase();
	}

	@Override
	public void validate() 
	{
		// CODE REVIEW: Space your statements apart
		// CODE REVIEW: You need to check the return of split.  What if the string does not have a / in it?  Also, your unit test should test things like new StorageKey("foo") which would show this problem...
		// CODE REVIEW: You can't use File.separator ... on windows the seperator is "\" which will break everything.  Hard code "/" here
		
		Validator.notNull(getSimpleValue());
		
		String[] breakonslash = getSimpleValue().split(File.separator);
		kind = new Kind(breakonslash[0]);
		
		String[] breakondot = breakonslash[1].split("\\.");
		id = new ObjectId(breakondot[0]);
		
		extension= new StorageKeyExtension(breakondot[1]);
		
		// CODE REVIEW: at the end here you need to SET the value.  So something like setValue(createStringFromComponents(getSimpelKind(), getSimpleId(), getSimpleExtension());
		
	}
	/**
	 * @return The Kind associated with the storage Key
	 */
	 public Kind getSimpleKind() { return kind; }
	/**
	 * @return The ObjectId associated with the storage Key
	 */
	 // CODE REVIEW: The method name should be getSimpleObjectId()
	 public ObjectId getSimpleId() { return id; }
	/**
	 * @return The Extension associated with the storage Key
	 */
	 public StorageKeyExtension getSimpleExtension() { return extension; }

}