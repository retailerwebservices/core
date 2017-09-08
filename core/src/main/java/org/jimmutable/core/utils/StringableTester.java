package org.jimmutable.core.utils;

import java.util.Objects;

import org.jimmutable.core.objects.Stringable;

public class StringableTester<S extends Stringable> 
{
	private Stringable.Converter<S> converter = null;
	
	public StringableTester(Stringable.Converter<S> converter)
	{
		Validator.notNull(converter);
		this.converter = converter;
	}

	public S assertValid(String value, String normalized_value)
	{
		S ret = converter.fromString(value, null);
		
		assert(ret != null);
		
		
		assert(Objects.equals(ret.getSimpleValue(), normalized_value));
		
		return ret;
	}
	
	public S assertValid(String value)
	{
		S ret = converter.fromString(value, null);
		
		assert(ret != null);
		
		return ret;
	}
	
	public void assertInvalid(String value)
	{
		assert(null == converter.fromString(value, null));
	}
}
