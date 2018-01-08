package org.jimmutable.core.serialization;

import org.jimmutable.core.examples.product_data.BrandCode;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;

import junit.framework.TestCase;

/**
 * This class cannot be used across projects as it lives in the test folder of
 * Jimmutable core. In order to test a Stringable using utilities from this project
 * you should instead use StringableTestingUtils.
 */
abstract public class StringableTest extends TestCase
{
	abstract public Stringable fromString(String src);
	
	public StringableTest( String testName )
	{
		super( testName );
	}
	
	
	public void assertNotValid(String src_code)
	{
		try 
		{
			Stringable test = fromString(src_code);
			assert(false); // failure, should have not been valid
		}
		catch(ValidationException e)
		{
			// This is what we expect
		}
	}

	public void assertValid(String src_code, String expected_value)
	{
		try 
		{
			Stringable test = fromString(src_code);
			assertEquals(test.getSimpleValue(),expected_value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			assert(false);
		}
	}
}
