package org.jimmutable.cloud.cache;

import org.jimmutable.core.utils.Validator;

import org.jimmutable.core.objects.StandardEnum;

public enum CacheActivity implements StandardEnum
{
	GET("get"), PUT("put"), REMOVE("remove");

	String code;

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

}
