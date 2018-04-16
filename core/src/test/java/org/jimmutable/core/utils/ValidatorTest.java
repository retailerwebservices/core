package org.jimmutable.core.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.common.Address;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator.ValidCharacters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ValidatorTest( String testName )
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(ValidatorTest.class);
	}

	public void testContainsOnlyValidCharacters()
	{
		testContainsOnlyValidCharacters(true, "hello world", Validator.LOWERCASE_LETTERS, Validator.SPACE);
		testContainsOnlyValidCharacters(false, "Hello world", Validator.LOWERCASE_LETTERS, Validator.SPACE);

		testContainsOnlyValidCharacters(true, "Hello 199 world", Validator.LETTERS, Validator.NUMBERS, Validator.SPACE);
		testContainsOnlyValidCharacters(false, "Hello 199 world", Validator.NUMBERS, Validator.SPACE);
		testContainsOnlyValidCharacters(false, "Hello 199 world", Validator.LETTERS, Validator.NUMBERS);
	}

	public void testReturnMessageForNotNull()
	{// correct

		try
		{
			Validator.notNull(null, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Required field is null for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForisTrue()
	{// correct

		try
		{
			Validator.isTrue(false, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Expression must evaluate to true for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageisFalse()
	{// correct

		try
		{
			Validator.isFalse(true, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Expression must evaluate to false for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageforMaxObject()
	{// correct

		try
		{
			Validator.maxObject(true, false, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (true) is above the maximum allowed value (false) for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForMinObject()
	{// correct
		try
		{
			Validator.minObject(false, true, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (false) is below minimum allowed value (true) for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForMax()
	{// correct

		try
		{
			Validator.max((byte) 5, (byte) 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (5) is above the maximum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.max(5, 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (5) is above the maximum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.max((short) 5, (short) 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (5) is above the maximum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.max(5l, 4l, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (5) is above the maximum allowed value (4) for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForMin()
	{// correct
		try
		{
			Validator.min((byte) 3, (byte) 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (3) is below minimum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.min((short) 3, (short) 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (3) is below minimum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.min(3, 4, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (3) is below minimum allowed value (4) for Bad Math", e.getMessage());
		}
		try
		{
			Validator.min(3l, 4l, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (3) is below minimum allowed value (4) for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForContainsNoNull()
	{// correct
		ArrayList<Day> days = new ArrayList<>();
		days.add(null);
		try
		{
			Validator.containsNoNulls(days, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Collection contained a null element for Collection Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForContainsOnlyIntancesOf()
	{// correct
		FieldArrayList<Day> days = new FieldArrayList<>();
		days.add(new Day(1, 2, 3000));
		try
		{
			Validator.containsOnlyInstancesOf(Address.class, days, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Collection contains an object of the wrong type for Bad Math", e.getMessage());
		}
		HashMap<ObjectId, Day> maps = new HashMap<>();
		maps.put(ObjectId.createRandomId(), new Day(1, 2, 3000));
		try
		{
			Validator.containsOnlyInstancesOf(ObjectId.class, Address.class, maps, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Collection contains an object of the wrong type for Bad Math", e.getMessage());
		}
		try
		{
			Validator.containsOnlyInstancesOf(Address.class, Day.class, maps, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Collection contains an object of the wrong type for Bad Math", e.getMessage());
		}
	}

	public void testReturnMessageForNotEqual()
	{

		try
		{
			Validator.notEqual(null, null, "Bad Math");
		}
		catch ( Exception e )
		{
			assertEquals("Value (null) is equal to value (null) for Bad Math", e.getMessage());
		}
	}

	public void testCharacterValidators()
	{
		testCharacterValidator(Validator.LETTERS, new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' });
		testCharacterValidator(Validator.UPPERCASE_LETTERS, new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' });
		testCharacterValidator(Validator.LOWERCASE_LETTERS, new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' });
		testCharacterValidator(Validator.NUMBERS, new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' });
		testCharacterValidator(Validator.COMMON_WHITESPACE, new char[] { ' ', '\n', '\r', '\t' });
		testCharacterValidator(Validator.SPACE, new char[] { ' ' });
		testCharacterValidator(Validator.DOT, new char[] { '.' });
		testCharacterValidator(Validator.DASH, new char[] { '-' });
		testCharacterValidator(Validator.UNDERSCORE, new char[] { '_' });
		testCharacterValidator(Validator.FORWARD_SLASH, new char[] { '/' });
		testCharacterValidator(Validator.BACKWARD_SLASH, new char[] { '\\' });
		testCharacterValidator(Validator.COLON, new char[] { ':' });
	}

	// CODEREVIEW: This should be updated for Label. -PM
	private void testContainsOnlyValidCharacters( boolean should_be_valid, String str, ValidCharacters... allowed_characters )
	{
		try
		{
			Validator.containsOnlyValidCharacters(str, "label", allowed_characters);

			if ( !should_be_valid )
				fail();
		}
		catch ( Exception e )
		{
			if ( should_be_valid )
			{
				fail();
			}
			else
			{
				assertEquals("Illegal character in string "+str+" for label", e.getMessage());
			}

		}
	}

	private void testCharacterValidator( Validator.ValidCharacters validator, char[] valid_chars )
	{
		for ( int i = 0; i < 10_000; i++ )
		{
			char ch = (char) i;

			boolean should_cur_character_be_valid = false;

			for ( int j = 0; j < valid_chars.length; j++ )
			{
				if ( valid_chars[j] == ch )
				{
					should_cur_character_be_valid = true;
					break;
				}
			}

			assertEquals(should_cur_character_be_valid, validator.isValid(ch));
		}
	}
}
