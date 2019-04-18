package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Validator;

public enum CacheActivity implements StandardEnum
{
	GET("get"), PUT("put"), REMOVE("remove");

	static public final MyConverter CONVERTER = new MyConverter();
	
	private String code;

	private CacheActivity( String code )
	{
		Validator.notNull("Code", code);
		this.code = code;
	}

	@Override
	public String getSimpleCode()
	{
		return code;
	}
	static public class MyConverter extends StandardEnum.Converter<CacheActivity>
	{
		public CacheActivity fromCode( String code, CacheActivity default_value )
		{
			if ( code == null )
			{
				return default_value;
			}
			code = code.trim();

			for ( CacheActivity a : CacheActivity.values() )
			{
				if ( a.getSimpleCode().equalsIgnoreCase(code) )
				{
					return a;
				}
			}

			return default_value;
		}
	}

}
