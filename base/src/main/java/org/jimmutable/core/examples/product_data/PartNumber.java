package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a simple, Stringable object
 * 
 * @author jim.kane
 *
 */
public class PartNumber extends Stringable
{
	public PartNumber(String code)
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
			
			throw new ValidationException(String.format("Illegal character \'%c\' in part number %s.  Only upper case letters and numbers are allowed", ch, getSimpleValue()));
		}
	}
}
