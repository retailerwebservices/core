package org.jimmutable.core.objects.common;

import java.util.HashSet;
import java.util.Set;

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
    	
    	assert(TimezoneID.getSimpleAllCommonTimeZoneIDs().size() > 3);
    	assert(TimezoneID.getSimeAllTimezoneIDs().size() > 50);
    }
    
    private void assertFailure(String value)
    {
    	try
    	{
    		new TimezoneID(value);
    		assert(false);
    	}
    	catch(ValidationException e)
    	{
    		assert(true);
    	}
    }
    
    private void assertValid(String value)
    {
    	try
    	{
    		new TimezoneID(value);
    		assert(true);
    	}
    	catch(ValidationException e)
    	{
    		assert(false);
    	}
    }
}