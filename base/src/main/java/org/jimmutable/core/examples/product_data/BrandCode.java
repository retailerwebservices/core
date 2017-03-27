package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a simple, Stringable object
 * 
 * @author jim.kane
 *
 */
public class BrandCode extends Stringable
{
	public BrandCode(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeUpperCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		char chars[] = getSimpleValue().toCharArray();
		for ( char ch : chars )
		{
			if ( ch >= 'A' && ch <= 'Z' ) continue;
			if ( ch >= '0' && ch <= '9' ) continue;
			if ( ch == '_' ) continue;
			
			throw new ValidationException(String.format("Illegal character \'%c\' in brand code %s.  Only upper case letters, numbers and underscore (_) are allowed", ch, getSimpleValue()));
		}
	}
}
