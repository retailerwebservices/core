package org.jimmutable.aws.s3;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class S3BucketName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	public S3BucketName(String code)
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
			
			throw new ValidationException(String.format("Illegal character \'%c\' in bucket name %s.  Only lower case letters, numbers, dash (-) and dot (.) are allowed.", ch, getSimpleValue()));
		}
		
		if ( getSimpleValue().startsWith(".") ) throw new ValidationException(String.format("Bucket name %s is invalid, bucket names may not start with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().endsWith(".") ) throw new ValidationException(String.format("Bucket name %s is invalid, bucket names may not end with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().contains("..") ) throw new ValidationException(String.format("Bucket name %s is invalid, bucket names may not contain two dots in a row (..) ", getSimpleValue()));
	}
	
	static public class MyConverter extends Stringable.Converter<S3BucketName>
	{
		public S3BucketName fromString(String str, S3BucketName default_value)
		{
			try
			{
				return new S3BucketName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
