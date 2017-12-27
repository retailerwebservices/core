package org.jimmutable.cloud.storage;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;

public class StorageKeyName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();

	public StorageKeyName(String value) 
	{
		super(value);		
	}
	
	public StorageKeyName(ObjectId id)
	{
		this(id.getSimpleValue());
	}

	@Override
	public void normalize() 
	{
		normalizeTrim();
		if (getSimpleValue() != null) setValue(getSimpleValue().toLowerCase());
	}

	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LETTERS, Validator.DASH, Validator.NUMBERS, Validator.UNDERSCORE);

		if (getSimpleValue().length() < 1)
		{
			throw new ValidationException("Length must be greater than 1");
		}
		
		if (getSimpleValue().length() > 255)
		{
			throw new ValidationException("Length must be less than 255");
		}
	}
	
	static public class MyConverter extends Stringable.Converter<StorageKeyName>
	{
		public StorageKeyName fromString(String str, StorageKeyName default_value)
		{
			try
			{
				return new StorageKeyName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}	
}
