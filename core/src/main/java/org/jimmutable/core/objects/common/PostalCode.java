package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/* 
* @author preston.mccumber
*/

public class PostalCode extends Stringable {

	public PostalCode(String code) 
	{
		super(code);
	}

	static public final MyConverter CONVERTER = new MyConverter();

	@Override
	public void normalize() 
	{
		normalizeTrim();
		normalizeUpperCase();
		normalizeDash();
	}

	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.NUMBERS, Validator.DASH,
				Validator.UPPERCASE_LETTERS, Validator.SPACE);
		Validator.min(getSimpleValue().length(), 1);
		Validator.max(getSimpleValue().length(), 64);
	}

	final private void normalizeDash() 
	{
		CharSequence dash = "-";
		if (getSimpleValue().contains(dash)) return;
		if (getSimpleValue().length() == 9)
			setValue(getSimpleValue().substring(0, 5) + dash + getSimpleValue().substring(5));
	}

	static public class MyConverter extends Stringable.Converter<PostalCode> 
	{
		public PostalCode fromString(String str, PostalCode default_value) 
		{
			try 
			{
				return new PostalCode(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}
}
