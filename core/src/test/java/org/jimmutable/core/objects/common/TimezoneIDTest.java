package org.jimmutable.core.objects.common;

import org.jimmutable.core.exceptions.ValidationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TimezoneIDTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TimezoneIDTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TimezoneIDTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testObjectID()
    {
    	assertFailure(null);
    	assertFailure("");
    	assertFailure("hello");
    	
    	assertValid("US/Hawaii");
    	assertValid("US/Alaska");
    	assertValid("US/Arizona");
    	assertValid("US/Pacific");
    	assertValid("US/Mountain");
    	assertValid("US/Central");
    	assertValid("US/Indiana-Starke");
    	assertValid("US/Michigan");
    	assertValid("US/East-Indiana");
    	assertValid("US/Eastern");
    	
    	assertTrue(TimezoneID.getSimpleAllCommonTimeZoneIDs().size() > 3);
    	assertTrue(TimezoneID.getSimeAllTimezoneIDs().size() > 50);
    }
    
    private void assertFailure(String value)
    {
    	try
    	{
    		new TimezoneID(value);
    		fail();
    	}
    	catch(ValidationException e)
    	{
    		
    	}
    }
    
    private void assertValid(String value)
    {
    	try
    	{
    		new TimezoneID(value);
    	}
    	catch(ValidationException e)
    	{
    		fail();
    	}
    }
}