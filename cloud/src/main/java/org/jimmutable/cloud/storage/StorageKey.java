package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
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

	private Kind kind;
	private String name;
	private ObjectId id;
	private StorageKeyExtension extension;

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
	 * @param kind
	 *            the Kind used for the StorageKey
	 * @param name
	 *            A nice name for the file (usually useful for attachments so the
	 *            user can set a recognizable filename themselves)
	 * @param object_id
	 *            the ObjectId used for the StorageKey
	 * @param extension
	 *            the extension of the StorageKey
	 */

	public StorageKey(Kind kind, String name, ObjectId object_id, StorageKeyExtension extension)
	{
		this(createStringFromComponents(kind, name, object_id, extension));
	}

	/**
	 *
	 * @param kind
	 *            the Kind used for the StorageKey
	 * 
	 * @param name
	 *            the nice name
	 * @param object_id
	 *            the ObjectId used for the StorageKey
	 * @param extension
	 *            the extension of the StorageKey
	 *
	 * @return if Everything validates it will return a string that concatenates all
	 *         of the parameters simple values. {alpha,123,"txt"}->"alpha/123.txt"
	 */

	static private String createStringFromComponents(Kind kind, String name, ObjectId object_id, StorageKeyExtension extension)
	{
		Validator.notNull(kind, object_id, extension);

		if (name == null || name.isEmpty())
		{
			return createStringFromComponents(kind, object_id, extension);
		}

		return String.format("%s/%s~%s.%s", kind.getSimpleValue(), name, object_id.getSimpleValue(), extension.getSimpleValue());
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
		return String.format("%s/%s.%s", kind.getSimpleValue(), object_id.getSimpleValue(), extension.getSimpleValue());
	}

	@Override
	public void normalize()
	{
		super.normalizeTrim();
		super.normalizeLowerCase();

		int kind_delim_index = super.getSimpleValue().indexOf("/");
		int name_delim_index = super.getSimpleValue().lastIndexOf("~");
		int extension_delim_index = super.getSimpleValue().lastIndexOf(".");

		kind = new Kind(super.getSimpleValue().substring(0, kind_delim_index));

		try
		{
			name = super.getSimpleValue().substring(kind_delim_index + 1, name_delim_index).replaceAll(" ", "_");
		} catch (Exception e)
		{
			name = "";
		}

		if (name.isEmpty())
		{
			id = new ObjectId(super.getSimpleValue().substring(kind_delim_index + 1, extension_delim_index));
		} else
		{
			id = new ObjectId(super.getSimpleValue().substring(name_delim_index + 1, extension_delim_index));
		}

		extension = new StorageKeyExtension(super.getSimpleValue().substring(extension_delim_index));

		super.setValue(createStringFromComponents(getSimpleKind(), getOptionalName(null), getSimpleObjectId(), getSimpleExtension()));

	}

	@Override
	public void validate()
	{

		Validator.notNull(super.getSimpleValue(), getSimpleKind(), getSimpleObjectId(), getSimpleExtension());
		Validator.max(super.getSimpleValue().length(), 255);

	}

	/**
	 * The nice name of the file (usually useful for attachments)
	 * 
	 * @return
	 */
	public String getOptionalName(String default_value)
	{
		if (name == null)
		{
			return default_value;
		}
		return name;
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
			try
			{
				return new StorageKey(str);
			} catch (Exception e)
			{
				return default_value;
			}
		}
	}
}
