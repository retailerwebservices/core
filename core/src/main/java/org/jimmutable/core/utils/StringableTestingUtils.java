package org.jimmutable.core.utils;

import java.util.Objects;

import org.jimmutable.core.objects.Stringable;

/**
 * Various static Stringable utility functions that are useful for making
 * Stringable tests. You can create assertions against the boolean values passed
 * into the methods here. Generally you should use JUnit assertTrue on the values
 * returned.
 */
public class StringableTestingUtils<S extends Stringable> 
{
	private Stringable.Converter<S> converter = null;
	
	public StringableTestingUtils(Stringable.Converter<S> converter)
	{
		Validator.notNull(converter);
		this.converter = converter;
	}

	/**
	 * Test that a Stringable value is equal to the normalized_value you expect.
	 * 
	 * @param value
	 *            The value that you expect to be equal to normalized_value
	 *            after normalization
	 * @param normalized_value
	 *            The value that value should equal once normalized.
	 * 
	 * @return the boolean value of whether or not the value is equal to the
	 *         normalized_value after conversion
	 */
	public boolean isValid(String value, String normalized_value)
	{
		S ret = converter.fromString(value, null);
		
		if(ret == null)
		{
			return false;
		}
		
		if(!Objects.equals(ret.getSimpleValue(), normalized_value))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Test that a Stringable value is able to be normalized.
	 * 
	 * @param value
	 *            The value that you expect to be able to normalize without
	 *            error.
	 * 
	 * @return the boolean value of whether or not the value is normalizable.
	 */
	public boolean isValid(String value)
	{
		S ret = converter.fromString(value, null);
		
		return ret != null;
	}
	
	/**
	 * Test that a Stringable value is not able to be normalized.
	 * 
	 * @param value
	 *            The value that you expect to not be able to normalize.
	 * 
	 * @return the boolean value of whether or not the value is NOT
	 *         normalizable.
	 */
	public boolean isInvalid(String value)
	{
		return null == converter.fromString(value, null);
	}
}
