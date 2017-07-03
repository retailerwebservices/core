package org.jimmutable.core.objects.common;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.BindingType.MyConverter;
import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

public enum PhoneNumberType implements StandardEnum
{
	HOME("home"),
	WORK("work"),
	MOBILE("mobile"),
	FAX("fax"),
	OTHER("other");
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	private String code;
	
	private PhoneNumberType(String code)
	{
		Validator.notNull(code);
		this.code = Normalizer.lowerCase(code);
	}
	
	public String getSimpleCode() { return code; }
	public String toString() { return code; }
	
	static public class MyConverter extends StandardEnum.Converter<PhoneNumberType>
	{
		public PhoneNumberType fromCode(String code, PhoneNumberType default_value) 
		{
			if ( code == null ) return default_value;
			code = code.trim();
			
			for ( PhoneNumberType t : PhoneNumberType.values() )
			{
				if ( t.getSimpleCode().equalsIgnoreCase(code) ) 
					return t;
			}
			
			return default_value;
		}
	}
}
