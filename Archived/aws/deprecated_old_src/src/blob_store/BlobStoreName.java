package org.jimmutable.cloud.blob_store;

import org.jimmutable.cloud.simple_object_store.SimpleStoreName;
import org.jimmutable.cloud.simple_object_store.SimpleStoreName.MyConverter;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class BlobStoreName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public BlobStoreName(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH, Validator.DOT);
		
		if ( getSimpleValue().startsWith(".") ) throw new ValidationException(String.format("Blob store name %s is invalid, bucket names may not start with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().endsWith(".") ) throw new ValidationException(String.format("Blob store name %s is invalid, bucket names may not end with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().contains("..") ) throw new ValidationException(String.format("Blob store name %s is invalid, bucket names may not contain two dots in a row (..) ", getSimpleValue()));
	}
	
	static public class MyConverter extends Stringable.Converter<BlobStoreName>
	{
		public BlobStoreName fromString(String str, BlobStoreName default_value)
		{
			try
			{
				return new BlobStoreName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}


