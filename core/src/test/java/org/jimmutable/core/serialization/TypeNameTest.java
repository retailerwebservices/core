package org.jimmutable.core.serialization;

import org.jimmutable.core.serialization.TypeName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TypeNameTest extends TestCase
{

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TypeNameTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( TypeNameTest.class );
	}
	
	
	public void assertValid(String type_name_str)
	{
		try
		{
			TypeName type_name = new TypeName(type_name_str);
			assertEquals(type_name.getSimpleName(),type_name_str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	public void assertInvalid(String type_name_str)
	{
		try
		{
			TypeName type_name = new TypeName(type_name_str);
			fail();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}

	public void testTypeNames()
	{
		assertValid("foo");
		assertValid("foo_bar");
		assertValid("foo_bar22");
		assertValid("f");
		assertValid("org.kane.base.TypeNameTest");
		assertValid("FOO");
		assertValid("com.Foo$Bar");
		
		assertInvalid(null);
		assertInvalid("");
		assertInvalid("2bar");
		assertInvalid("_");
		assertInvalid("jim bob");
		assertInvalid("jim-bob");
		assertInvalid("f&p");
		assertInvalid("!f");
	}
}
