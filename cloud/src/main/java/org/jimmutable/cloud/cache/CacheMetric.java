package org.jimmutable.cloud.cache;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Validator;

public enum CacheMetric implements StandardEnum
{
	HIT("hit"),//
	MISS("miss"),//
	ADD("add"),//
	REMOVE("remove");

	static public final MyConverter CONVERTER = new MyConverter();
	private String code;

	private CacheMetric( String code )
	{
		Validator.notNull("Code", code);
		this.code = code;
	}

	@Override
	public String getSimpleCode()
	{
		return code;
	}
	
	static public class MyConverter extends StandardEnum.Converter<CacheMetric>
	{
		public CacheMetric fromCode( String code, CacheMetric default_value )
		{
			if ( code == null )
			{
				return default_value;
			}
			code = code.trim();

			for ( CacheMetric a : CacheMetric.values() )
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
