package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.Validator;

/**
 * TODO get more info from Jim on this class. I believe it's simply genericizing the notion of an ObjectIdStorageKey, where a GenericStorageKey can contain a StorageKeyName with
 * an arbitrary amount of characters and such, but I'm not sure about this one.
 * @author salvador.salazar
 *
 */
public class GenericStorageKey extends Stringable implements StorageKey
{
	private Kind kind; // required
	private StorageKeyName name; // required
	private StorageKeyExtension extension; // required

	public GenericStorageKey(String value)
	{
		super(value);
	}
	
	@Override
	public void normalize()
	{
		super.normalizeTrim();
		super.normalizeLowerCase();

		int kind_delim_index = super.getSimpleValue().indexOf("/");
		int extension_delim_index = super.getSimpleValue().lastIndexOf(".");

		kind = new Kind(super.getSimpleValue().substring(0, kind_delim_index));
		name = new StorageKeyName(super.getSimpleValue().substring(kind_delim_index + 1, extension_delim_index));
		extension = new StorageKeyExtension(super.getSimpleValue().substring(extension_delim_index));
	}

	@Override
	public void validate()
	{
		Validator.notNull(kind);
		Validator.notNull(name);
		Validator.notNull(extension);
	}

	@Override
	public Kind getSimpleKind()
	{
		return kind;
	}

	@Override
	public StorageKeyName getSimpleName()
	{
		return name;
	}
	
	@Override
	public StorageKeyExtension getSimpleExtension()
	{
		return extension;
	}

	public boolean isNameAnObjectId()
	{
		// TODO dunno what to do here
		return false;
	}

	static public class MyConverter extends Stringable.Converter<GenericStorageKey>
	{
		public GenericStorageKey fromString(String str, GenericStorageKey default_value)
		{
			try
			{
				return new GenericStorageKey(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}	
}
