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
		
		char chars[] = getSimpleValue().toCharArray();
		for ( char ch : chars )
		{
			if ( ch >= 'a' && ch <= 'z' ) continue;
			if ( ch >= '0' && ch <= '9' ) continue;
			if ( ch == '-' ) continue;
			if ( ch == '.' ) continue;
			
			throw new ValidationException(String.format("Illegal character \'%c\' in store name %s.  Only lower case letters, numbers, dash (-) and dot (.) are allowed.", ch, getSimpleValue()));
		}
		
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

