package org.jimmutable.core.objects.common;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.core.exceptions.ValidationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PhoneNumberDigitsTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PhoneNumberDigitsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PhoneNumberDigitsTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testObjectID()
    {
    	assertFailure(null);
    	assertFailure("");
    	assertFailure("hello");
    	assertFailure("268-8207");
    	
    	assertToString("555-867-5309", "1-555-867-5309", "1-555-867-5309", "5558675309", null);
    	assertToString("1-555-867-5309", "1-555-867-5309", "1-555-867-5309", "15558675309", null);
    	
    	
    	assertToString("555-867-5309 x 12", "1-555-867-5309 x 12", "1-555-867-5309", "5558675309", "12");
    	assertToString("5558675309x12", "1-555-867-5309 x 12", "1-555-867-5309", "5558675309", "12");
    }
    
    private void assertFailure(String value)
    {
    	try
    	{
    		new PhoneNumberDigits(value);
    		assert(false);
    	}
    	catch(ValidationException e)
    	{
    		assert(true);
    	}
    }
    
    private void assertToString(String parse, String expected_string_value, String expected_phone_pretty_print, String expected_phone_digits, String expected_extension)
    {
    	try
    	{ 
    		PhoneNumberDigits test = new PhoneNumberDigits(parse);
    		
    		assertEquals(expected_string_value, test.getSimpleValue());
    		
    		assertEquals(test.getSimplePhoneDigits(), expected_phone_digits);
    		assertEquals(test.getSimplePhonePrettyPrint(), expected_phone_pretty_print);
    		assertEquals(test.getOptionalExtensionDigits(null),expected_extension);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		assert(false);
    	}
    }
}
