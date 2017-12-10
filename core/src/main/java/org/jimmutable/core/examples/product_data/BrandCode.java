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
	static public final MyConverter CONVERTER = new MyConverter();
	
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
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UPPERCASE_LETTERS, Validator.NUMBERS, Validator.UNDERSCORE);
	}
	
	static public class MyConverter extends Stringable.Converter<BrandCode>
	{
		public BrandCode fromString(String str, BrandCode default_value)
		{
			try
			{
				return new BrandCode(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
