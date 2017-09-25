package org.jimmutable.cloud.storage;

import java.io.File;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.Day.MyConverter;
import org.jimmutable.core.utils.Validator;

/**
 *
 * @author andrew.towe This class is designed to help us with how we store
 *         objects. All storage objects will need a storage key. Keys have three
 *         parts to them: the Kind, the Object Id, and the Extension of the
 *         storage object.
 *
 */

public class StorageKey extends Stringable
{

	static public final MyConverter CONVERTER = new MyConverter();

	Kind kind;
	ObjectId id;
	StorageKeyExtension extension;

	/**
	 * @param value
	 *            a new Storage Key based on the string that is passed in. Therefore
	 *            if the String passed in is "Alpha/123.txt" then the Kind will be
	 *            "alpha" the Object Id will be "123" the extension will be "txt"
	 */
	public StorageKey(String value)
	{
		super(value);
	}

	/**
	 * @param kind
	 *            the Kind used for the StorageKey
	 * @param object_id
	 *            the ObjectId used for the StorageKey
	 * @param extension
	 *            the extension of the StorageKey
	 */

	public StorageKey(Kind kind, ObjectId object_id, StorageKeyExtension extension)
	{
		this(createStringFromComponents(kind, object_id, extension));
	}

	/**
	 *
	 * @param kind
	 *            the Kind used for the StorageKey
	 * @param object_id
	 *            the ObjectId used for the StorageKey
	 * @param extension
	 *            the extension of the StorageKey
	 *
	 * @return if Everything validates it will return a string that concatenates all
	 *         of the parameters simple values. {alpha,123,"txt"}->"alpha/123.txt"
	 */

	static private String createStringFromComponents(Kind kind, ObjectId object_id, StorageKeyExtension extension)
	{
		Validator.notNull(kind, object_id, extension);
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

		Validator.notNull(getSimpleValue());

		String[] breakonslash = getSimpleValue().split("/");
		Validator.min(breakonslash.length, 2);
		kind = new Kind(breakonslash[0]);

		String[] breakondot = breakonslash[1].split("\\.");
		Validator.min(breakonslash.length, 2);
		id = new ObjectId(breakondot[0]);

		extension = new StorageKeyExtension(breakondot[1]);

		setValue(createStringFromComponents(getSimpleKind(), getSimpleObjectId(), getSimpleExtension()));

	}

	/**
	 * @return The Kind associated with the storage Key
	 */
	public Kind getSimpleKind()
	{
		return kind;
	}

	/**
	 * @return The ObjectId associated with the storage Key
	 */
	public ObjectId getSimpleObjectId()
	{
		return id;
	}

	/**
	 * @return The Extension associated with the storage Key
	 */
	public StorageKeyExtension getSimpleExtension()
	{
		return extension;
	}

	static public class MyConverter extends Stringable.Converter<StorageKey>
	{
		public StorageKey fromString(String str, StorageKey default_value)
		{
			try {
				return new StorageKey(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}
}
