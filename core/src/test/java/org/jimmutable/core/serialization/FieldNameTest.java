package org.jimmutable.core.serialization;

import org.jimmutable.core.serialization.FieldName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FieldNameTest extends TestCase
{

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public FieldNameTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( FieldNameTest.class );
	}
	
	
	public void assertValid(String field_name_str)
	{
		try
		{
			FieldName field_name = new FieldName(field_name_str);
			assertEquals(field_name.getSimpleName(),field_name_str);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			assert(false);
		}
	}
	
	public void assertInvalid(String field_name_str)
	{
		try
		{
			FieldName field_name = new FieldName(field_name_str);
			assert(false);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			assert(true);
		}
	}

	public void testFieldNames()
	{
		assertValid("foo");
		assertValid("foo_bar");
		assertValid("foo_bar22");
		assertValid("f");
		
		assertInvalid(null);
		assertInvalid("");
		assertInvalid("2bar");
		assertInvalid("_");
		assertInvalid("jim bob");
		assertInvalid("jim-bob");
		assertInvalid("f&p");
		assertInvalid("!f");
		assertInvalid("FOO");
		assertInvalid("fOO");
	}
}
