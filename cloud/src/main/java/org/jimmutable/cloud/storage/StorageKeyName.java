package org.jimmutable.cloud.storage;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.core.utils.Validator.ValidCharacters;

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
			throw new ValidationException("Length must be greater than 0");
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
	
	// TODO the following should be a regex check of the form XXXX-XXXX-XXXX-XXXX, where X is a letter or number
	/**
	 * The following is a convenience method to determine (quickly) if this is an ObjectId. It doesn't attempt
	 * to actually parse out every section and ensure it's actually hex and all that. It simply checks the contents and length.
	 * It doesn't throw any exceptions, and it doesn't attempt to instantiate any objects in a try catch block, so it's meant to be fast.
	 * 
	 * @return
	 */
	public boolean isObjectId()
	{
		List<ValidCharacters> allowed_characters = new ArrayList<>();
		
		allowed_characters.add(Validator.DASH);
		allowed_characters.add(Validator.LETTERS);
		allowed_characters.add(Validator.NUMBERS);
		
		char chars[] = getSimpleValue().toCharArray();
		
		for ( char ch : chars )
		{
			boolean is_valid = false;

			for ( ValidCharacters filter : allowed_characters )
			{
				if ( !filter.isValid(ch) ) 
				{
					is_valid = true;
				}
			}
			
			if (!is_valid)
			{
				return false;
			}
		}
		
		// if (value.charAt(4) != '-' || value.charAt(8) != '-' || value.charAt(12) != '-') return false; // need to make sure a long generic item key doesn't resolve to an ObjectId by mistake
		
		return getSimpleValue().length() == 19; // all objectId's are the same length, so length of 133a-abbf-3dda-ad77
	}
}
