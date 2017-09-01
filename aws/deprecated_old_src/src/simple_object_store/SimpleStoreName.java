package org.jimmutable.aws.simple_object_store;

import org.jimmutable.aws.s3.S3BucketName;
import org.jimmutable.aws.s3.S3BucketName.MyConverter;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class SimpleStoreName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public SimpleStoreName(String code)
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
		
		if ( getSimpleValue().startsWith(".") ) throw new ValidationException(String.format("Store name %s is invalid, bucket names may not start with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().endsWith(".") ) throw new ValidationException(String.format("Store name %s is invalid, bucket names may not end with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().contains("..") ) throw new ValidationException(String.format("Store name %s is invalid, bucket names may not contain two dots in a row (..) ", getSimpleValue()));
	}
	
	static public class MyConverter extends Stringable.Converter<SimpleStoreName>
	{
		public SimpleStoreName fromString(String str, SimpleStoreName default_value)
		{
			try
			{
				return new SimpleStoreName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}

