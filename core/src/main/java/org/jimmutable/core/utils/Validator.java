package org.jimmutable.core.utils;

import java.util.Collection;
import java.util.Map;

import javax.swing.SpringLayout.Constraints;

import org.jimmutable.core.exceptions.ValidationException;

/**
 * Various utility functions used for validation.
 * 
 * @author jim.kane
 *
 */
public class Validator
{
	static public final ValidCharacters LETTERS = new ValidCharactersLetters();
	static public final ValidCharacters LOWERCASE_LETTERS = new ValidCharactersLowerCaseLetters();
	static public final ValidCharacters UPPERCASE_LETTERS = new ValidCharactersUpperCaseLetters();
	static public final ValidCharacters NUMBERS = new ValidCharactersNumbers();
	static public final ValidCharacters COMMON_WHITESPACE = new ValidCharactersOthers(' ', '\n', '\r', '\t'); // limits the characters to common whitespace
	static public final ValidCharacters SPACE = new ValidCharactersOthers(' ');
	static public final ValidCharacters DOT = new ValidCharactersOthers('.');
	static public final ValidCharacters DASH = new ValidCharactersOthers('-');
	static public final ValidCharacters UNDERSCORE = new ValidCharactersOthers('_');
	static public final ValidCharacters FORWARD_SLASH = new ValidCharactersOthers('/');
	static public final ValidCharacters BACKWARD_SLASH = new ValidCharactersOthers('\\');
	static public final ValidCharacters COLON = new ValidCharactersOthers(':');
	static public final ValidCharacters MONEY_SYMBOLS = new ValidMoneyCharacters();

	/**
	 * Guarantee that obj is not null (i.e. thrown a ValidationException if obj is
	 * null)
	 * 
	 * @param obj
	 *            The object to test
	 */
	static public void notNull( Object obj )
	{
		notNull(obj, null);
	}

	/**
	 * Guarantee that obj is not null (i.e. thrown a ValidationException if obj is
	 * null)
	 * 
	 * @param obj
	 *            The object to test
	 */
	static public void notNull( Object obj, String label )
	{
		if ( obj == null )
		{
			String error_message = "Required field is null";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	/**
	 * Guarantee that all objects in objs are not null (i.e. thrown a
	 * ValidationException if any object is null)
	 * 
	 * @param objs
	 *            The objects to test
	 */
	@Deprecated
	static public void notNull( Object... objs )
	{
		noneAreNull(objs);
	}
	
	static public void noneAreNull( Object... objs )
	{
		for ( Object obj : objs )
		{
			if ( obj == null )
				throw new ValidationException("Required field is null");
		}
	}

	/**
	 * Guarantee that a given expression evaluates to true
	 * 
	 * @param value
	 *            The result of the expression (must be true, or a
	 *            ValidationException is thrown)
	 */
	static public void isTrue( boolean value )
	{
		isTrue(value, null);
	}

	/**
	 * Guarantee that a given expression evaluates to true
	 * 
	 * @param value
	 *            The result of the expression (must be true, or a
	 *            ValidationException is thrown)
	 */
	static public void isTrue( boolean value, String label )
	{
		if ( value == false )
		{
			String error_message = "Expression must evaluate to true";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	/**
	 * Guarantee that a given expression evaluates to false
	 * 
	 * @param value
	 *            The result of the expression (must be false, or a
	 *            ValidationException is thrown)
	 */
	static public void isFalse( boolean value )
	{
		isFalse(value, null);
	}

	/**
	 * Guarantee that a given expression evaluates to false
	 * 
	 * @param value
	 *            The result of the expression (must be false, or a
	 *            ValidationException is thrown)
	 */
	static public void isFalse( boolean value, String label )
	{
		if ( value == true )
		{
			String error_message = "Expression must evaluate to false";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
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
	static public void minObject( Comparable value, Comparable minimum_valid_value )
	{
		minObject(value, minimum_valid_value, null);
	}

	/**
	 * Guarantee that an object is equal to or greater than minimum_valid_value
	 * 
	 * @param value
	 *            The value to test
	 * @param minimum_valid_value
	 *            The minimum valid value
	 */
	static public void minObject( Comparable value, Comparable minimum_valid_value, String label )
	{
		if ( value.compareTo(minimum_valid_value) < 0 )
		{
			String error_message = "Value (" + value + ") is below minimum allowed value (" + minimum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void min( byte value, byte minimum_valid_value )
	{
		min(value, minimum_valid_value, null);
	}

	static public void min( byte value, byte minimum_valid_value, String label )
	{
		if ( value < minimum_valid_value )
		{
			String error_message = "Value (" + value + ") is below minimum allowed value (" + minimum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void min( short value, short minimum_valid_value )
	{
		min(value, minimum_valid_value, null);
	}

	static public void min( short value, short minimum_valid_value, String label )
	{
		if ( value < minimum_valid_value )
		{
			String error_message = "Value (" + value + ") is below minimum allowed value (" + minimum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void min( int value, int minimum_valid_value )
	{
		min(value, minimum_valid_value, null);
	}

	static public void min( int value, int minimum_valid_value, String label )
	{
		if ( value < minimum_valid_value )
		{
			String error_message = "Value (" + value + ") is below minimum allowed value (" + minimum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void min( long value, long minimum_valid_value )
	{
		min(value, minimum_valid_value, null);
	}

	static public void min( long value, long minimum_valid_value, String label )
	{
		if ( value < minimum_valid_value )
		{
			String error_message = "Value (" + value + ") is below minimum allowed value (" + minimum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	/**
	 * Guarantee that an object is equal to or less than maximum_value
	 * 
	 * @param value
	 *            The value to test
	 * @param maximum_valid_value
	 *            The maximum valid value
	 */
	static public void maxObject( Comparable value, Comparable maximum_valid_value )
	{
		maxObject(value, maximum_valid_value, null);
	}

	/**
	 * Guarantee that an object is equal to or less than maximum_value
	 * 
	 * @param value
	 *            The value to test
	 * @param maximum_valid_value
	 *            The maximum valid value
	 */
	static public void maxObject( Comparable value, Comparable maximum_valid_value, String label )
	{
		if ( value.compareTo(maximum_valid_value) > 0 )
		{
			String error_message = "Value (" + value + ") is above the maximum allowed value (" + maximum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void max( byte value, byte maximum_valid_value )
	{
		max(value, maximum_valid_value, null);
	}

	static public void max( byte value, byte maximum_valid_value, String label )
	{
		if ( value > maximum_valid_value )
		{
			String error_message = "Value (" + value + ") is above the maximum allowed value (" + maximum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}

	}

	static public void max( short value, short maximum_valid_value )
	{
		max(value, maximum_valid_value, null);
	}

	static public void max( short value, short maximum_valid_value, String label )
	{
		if ( value > maximum_valid_value )
		{
			String error_message = "Value (" + value + ") is above the maximum allowed value (" + maximum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void max( int value, int maximum_valid_value )
	{
		max(value, maximum_valid_value, null);
	}

	static public void max( int value, int maximum_valid_value, String label )
	{
		if ( value > maximum_valid_value )
		{
			String error_message = "Value (" + value + ") is above the maximum allowed value (" + maximum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public void max( long value, long maximum_valid_value )
	{
		max(value, maximum_valid_value, null);
	}

	static public void max( long value, long maximum_valid_value, String label )
	{
		if ( value > maximum_valid_value )
		{
			String error_message = "Value (" + value + ") is above the maximum allowed value (" + maximum_valid_value + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	/**
	 * Guarantee that a collection contains no nulls (i.e. throw a
	 * ValidationException if the collection contains one or more null values)
	 * 
	 * @param collection
	 *            The collection to test
	 */
	static public void containsNoNulls( Collection<?> collection )
	{
		containsNoNulls(collection, null);
	}

	/**
	 * Guarantee that a collection contains no nulls (i.e. throw a
	 * ValidationException if the collection contains one or more null values)
	 * 
	 * @param collection
	 *            The collection to test
	 */
	static public void containsNoNulls( Collection<?> collection, String label )
	{
		for ( Object obj : collection )
		{
			if ( obj == null )
			{
				String error_message = "Collection contained a null element";
				if ( label != null )
				{
					error_message += " for Collection " + label;
				}
				throw new ValidationException(error_message);
			}
		}

	}

	static public void containsOnlyInstancesOf( Class<?> c, Collection<?> collection )
	{
		containsOnlyInstancesOf(c, collection, null);
	}

	static public void containsOnlyInstancesOf( Class<?> c, Collection<?> collection, String label )
	{
		for ( Object obj : collection )
		{
			if ( !c.isInstance(obj) )
			{
				String error_message = "Collection contains an object of the wrong type";
				if ( label != null )
				{
					error_message += " for " + label;
				}
				throw new ValidationException(error_message);
			}
		}

	}

	static public void containsOnlyInstancesOf( Class<?> key, Class<?> value, Map<?, ?> map )
	{
		containsOnlyInstancesOf(key, value, map, null);

	}

	static public void containsOnlyInstancesOf( Class<?> key, Class<?> value, Map<?, ?> map, String label )
	{
		containsOnlyInstancesOf(key, map.keySet(), label);
		containsOnlyInstancesOf(value, map.values(), label);
	}

	static public <E extends Enum<E>> void notEqual( Enum<E> one, Enum<E> two )
	{
		notEqual(one, two, null);
	}

	static public <E extends Enum<E>> void notEqual( Enum<E> one, Enum<E> two, String label )
	{
		if ( one == two )
		{
			String error_message = "Value (" + one + ") is equal to value (" + two + ")";
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);
		}
	}

	static public interface ValidCharacters
	{
		public boolean isValid( char ch );
	}

	static private class ValidCharactersLetters implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			if ( ch >= 'a' && ch <= 'z' )
				return true;
			if ( ch >= 'A' && ch <= 'Z' )
				return true;

			return false;
		}
	}

	static private class ValidCharactersLowerCaseLetters implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			if ( ch >= 'a' && ch <= 'z' )
				return true;

			return false;
		}
	}

	static private class ValidCharactersUpperCaseLetters implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			if ( ch >= 'A' && ch <= 'Z' )
				return true;

			return false;
		}
	}

	static private class ValidCharactersNumbers implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			if ( ch >= '0' && ch <= '9' )
				return true;

			return false;
		}
	}

	static private class ValidCharactersWhitespace implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			return Character.isWhitespace(ch);
		}
	}

	static public class ValidCharactersOthers implements ValidCharacters
	{
		private char chars[];

		private ValidCharactersOthers( char... valid_chars )
		{
			this.chars = valid_chars;
		}

		public boolean isValid( char ch )
		{
			for ( int i = 0; i < chars.length; i++ )
			{
				if ( ch == chars[i] )
					return true;
			}
			return false;
		}
	}

	static private class ValidMoneyCharacters implements ValidCharacters
	{
		public boolean isValid( char ch )
		{
			if ( ch >= '0' && ch <= '9' || ch == ('$') || ch == ('.') || ch == (',') )
				return true;

			return false;
		}
	}

	/**
	 * This method is functionally the same as the original 'void
	 * containsOnlyValidCharacters', but it doesn't throw an exception. This is
	 * meant more as a general utility method, rather than something to be used
	 * during object construction.
	 * 
	 * @param str
	 * @param allowed_characters
	 * @return
	 */
	static public boolean containsOnlyValidCharactersQuiet( String str, ValidCharacters... allowed_characters )
	{
		if ( str == null )
			return false;

		char chars[] = str.toCharArray();

		for ( char ch : chars )
		{
			boolean is_allowed = false;

			for ( ValidCharacters filter : allowed_characters )
			{
				if ( filter.isValid(ch) )
				{
					is_allowed = true;
					continue;
				}
			}

			if ( !is_allowed )
				return false;
		}

		return true;
	}

	static public void containsOnlyValidCharacters( String str, ValidCharacters... allowed_characters )
	{
		containsOnlyValidCharacters(str, null, allowed_characters);
	}

	static public void containsOnlyValidCharacters( String str, String label, ValidCharacters... allowed_characters )
	{
		if ( str == null )
			return;

		if ( !containsOnlyValidCharactersQuiet(str, allowed_characters) )
		{
			String error_message = String.format("Illegal character in string %s", str);
			if ( label != null )
			{
				error_message += " for " + label;
			}
			throw new ValidationException(error_message);

		}
	}
}
