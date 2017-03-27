package org.jimmutable.core.utils;

import java.util.Collection;
import java.util.Map;

import org.jimmutable.core.exceptions.ValidationException;

/**
 * Various utility functions used for validation.  
 * 
 * @author jim.kane
 *
 */
public class Validator 
{
	/**
	 * Guarantee that obj is not null (i.e. thrown a ValidationException if obj 
	 * is null)
	 * 
	 * @param obj The object to test
	 */
	static public void notNull(Object obj) 
	{
		if ( obj == null ) throw new ValidationException("Required field is null");
	}
	
	/**
	 * Guarantee that all objects in objs are not null (i.e. thrown a
	 * ValidationException if any object is null)
	 * 
	 * @param objs
	 *            The objects to test
	 */
	static public void notNull(Object... objs) 
	{
		for ( Object obj : objs )
		{
			if ( obj == null ) throw new ValidationException("Required field is null");
		}
	}
	
	/**
	 * Guarantee that a given expression evaluates to true
	 * 
	 * @param value The result of the expression (must be true, or a ValidationException is thrown)
	 */
	static public void isTrue(boolean value)
	{
		if ( value == false )
		{
			throw new ValidationException("Expression must evaluate to true");
		}
	}
	
	/**
	 * Guarantee that a given expression evaluates to false
	 * 
	 * @param value The result of the expression (must be false, or a ValidationException is thrown)
	 */
	static public void isFalse(boolean value)
	{
		if ( value == false )
		{
			throw new ValidationException("Expression must evaluate to false");
		}
	}
	
	/**
	 * Guarantee that an object is equal to or greater than minimum_valid_value
	 * 
	 * @param value
	 *            The value to test
	 * @param minimum_valid_value
	 *            The minimum valid value
	 */
	static public void minObject(Comparable value, Comparable minimum_valid_value)
	{
		if ( value.compareTo(minimum_valid_value) < 0 ) 
			throw new ValidationException("Value ("+value+") is below minimum allowed value ("+minimum_valid_value+")");
	}

	static public void min(byte value, byte minimum_valid_value)
	{
		if ( value < minimum_valid_value ) 
			throw new ValidationException("Value ("+value+") is below minimum allowed value ("+minimum_valid_value+")");
	}
	
	static public void min(short value, short minimum_valid_value)
	{
		if ( value < minimum_valid_value ) 
			throw new ValidationException("Value ("+value+") is below minimum allowed value ("+minimum_valid_value+")");
	}
	
	static public void min(int value, int minimum_valid_value)
	{
		if ( value < minimum_valid_value ) 
			throw new ValidationException("Value ("+value+") is below minimum allowed value ("+minimum_valid_value+")");
	}
	
	static public void min(long value, long minimum_valid_value)
	{
		if ( value < minimum_valid_value ) 
			throw new ValidationException("Value ("+value+") is below minimum allowed value ("+minimum_valid_value+")");
	}
	
	/**
	 * Guarantee that an object is equal to or less than maximum_value
	 * 
	 * @param value
	 *            The value to test
	 * @param maximum_valid_value
	 *            The maximum valid value
	 */
	static public void maxObject(Comparable value, Comparable maximum_valid_value)
	{
		if ( value.compareTo(maximum_valid_value) > 0 ) 
			throw new ValidationException("Value ("+value+") is above the maximum allowed value ("+maximum_valid_value+")");
	}
	
	static public void max(byte value, byte maximum_valid_value)
	{
		if ( value > maximum_valid_value ) 
			throw new ValidationException("Value ("+value+") is above the maximum allowed value ("+maximum_valid_value+")");
	}
	
	static public void max(short value, short maximum_valid_value)
	{
		if ( value > maximum_valid_value ) 
			throw new ValidationException("Value ("+value+") is above the maximum allowed value ("+maximum_valid_value+")");
	}
	
	static public void max(int value, int maximum_valid_value)
	{
		if ( value > maximum_valid_value ) 
			throw new ValidationException("Value ("+value+") is above the maximum allowed value ("+maximum_valid_value+")");
	}
	
	static public void max(long value, long maximum_valid_value)
	{
		if ( value > maximum_valid_value ) 
			throw new ValidationException("Value ("+value+") is above the maximum allowed value ("+maximum_valid_value+")");
	}
	
	/**
	 * Guarantee that a collection contains no nulls (i.e. throw a
	 * ValidationException if the collection contains one or more null values)
	 * 
	 * @param c
	 *            The collection to test
	 */
	static public void containsNoNulls(Collection<?> collection)
	{
		for ( Object obj : collection )
		{
			if ( obj == null ) throw new ValidationException("Collection contained a null element");
		}
	}
	
	static public void containsOnlyInstancesOf(Class<?> c, Collection<?> collection)
	{
		for ( Object obj : collection )
		{
			if ( !c.isInstance(obj) )
				throw new ValidationException("Collection contains an object of the wrong type");
		}
	}
	
	static public void containsOnlyInstancesOf(Class<?> key, Class<?> value, Map<?, ?> map)
	{
		containsOnlyInstancesOf(key,map.keySet());
		containsOnlyInstancesOf(value,map.values());
	}

	static public <E extends Enum<E>> void notEqual(Enum<E> one, Enum<E> two)
	{
	    if (one == two) throw new ValidationException();
	}
}
