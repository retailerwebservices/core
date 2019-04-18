package org.jimmutable.cloud.cache;

import org.jimmutable.core.utils.Validator;

import org.jimmutable.core.objects.StandardEnum;

public enum CacheMetric implements StandardEnum
{
	HIT("hit"),//
	MISS("miss"),//
	ADD("add"),//
	REMOVE("remove");

	String code;

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

}
