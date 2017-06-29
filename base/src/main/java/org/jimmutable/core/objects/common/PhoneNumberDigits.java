package org.jimmutable.core.objects.common;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class PhoneNumberDigits extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	private String phone_digits; // required
	private String phone_pretty_print; // required
	private String extension_digits; // optional
	
	public PhoneNumberDigits(String code)
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
		
		int idx = getSimpleValue().indexOf('x');
		
		if ( idx != -1 )
		{
			extension_digits = onlyDigits(getSimpleValue().substring(idx+1));
			if ( extension_digits.length() == 0 ) extension_digits = null;
			
			phone_digits = onlyDigits(getSimpleValue().substring(0,idx));
		}
		else
		{
			phone_digits = onlyDigits(getSimpleValue());
		}
		
		if ( phone_digits.length() < 10 ) throw new ValidationException("Insufficient digits in phone number");
		
		if ( phone_digits.length() == 10 )
		{
			phone_pretty_print = String.format("1-%s-%s-%s", 
					phone_digits.substring(0, 3),
					phone_digits.substring(3, 6),
					phone_digits.substring(6,10));
		}
		
		else if ( phone_digits.length() == 11 )
		{
			phone_pretty_print = String.format("%s-%s-%s-%s", 
					phone_digits.substring(0, 1),
					phone_digits.substring(1, 4),
					phone_digits.substring(4, 7),
					phone_digits.substring(7,11));
		}
		else
		{
			phone_pretty_print = phone_digits;
		}
		
		if ( extension_digits != null ) 
			setValue(phone_pretty_print+" x "+extension_digits);
		else
			setValue(phone_pretty_print);
	}
	
	private String onlyDigits(String src)
	{
		char chars[] = src.toCharArray();
		
		StringBuilder clean_value = new StringBuilder();
		
		for ( char ch : chars )
		{
			if ( ch >= '0' && ch <= '9' )
			{
				clean_value.append(ch);
				continue;
			}
		}
		
		return clean_value.toString();
	}
	
	public String getSimplePhoneDigits() { return phone_digits; }
	public String getSimplePhonePrettyPrint() { return phone_pretty_print; }
	
	public boolean hasExtension() { return extension_digits != null; }
	public String getOptionalExtensionDigits(String default_value) { return Optional.getOptional(extension_digits, null, default_value); }
	
	static public class MyConverter extends Stringable.Converter<PhoneNumberDigits>
	{
		public PhoneNumberDigits fromString(String str, PhoneNumberDigits default_value)
		{
			try
			{
				return new PhoneNumberDigits(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}