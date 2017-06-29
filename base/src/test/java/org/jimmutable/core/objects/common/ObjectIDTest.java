package org.jimmutable.core.objects.common;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.core.exceptions.ValidationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ObjectIDTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ObjectIDTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ObjectIDTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testObjectID()
    {
    	assertFailure(null);
    	assertFailure("");
    	assertFailure("hello");
    	
    	assertToString("0", "0000-0000-0000",0);
    	assertToString("1", "0000-0000-0001",1);
    	
    	assertToString("1234-5678-9012", "1234-5678-9012",1234_5678_9012l);
    	assertToString("123456789012", "1234-5678-9012",1234_5678_9012l);
    	assertToString("  123456789012  ", "1234-5678-9012",1234_5678_9012l);
    	assertToString("-1234-5678-9012-", "1234-5678-9012",1234_5678_9012l);
    	
    	assertToString("12345678901234", "1234-5678-9012",1234_5678_9012l);
    	
    	// Test the random ID function
    	{
	    	Set<ObjectID> ids = new HashSet();
	    	
	    	for ( int i = 0; i < 10_000; i++ )
	    	{
	    		ObjectID cur = ObjectID.randomID();
	    		assertEquals(14,cur.getSimpleValue().length());
	    		
	    		ids.add(cur);
	    	}
	    	
	    	assertEquals(ids.size(),10_000);
    	}
    }
    
    private void assertFailure(String value)
    {
    	try
    	{
    		new ObjectID(value);
    		assert(false);
    	}
    	catch(ValidationException e)
    	{
    		assert(true);
    	}
    }
    
    private void assertToString(String parse, String expected_string_value, long expected_long_value)
    {
    	try
    	{ 
    		ObjectID test = new ObjectID(parse);
    		
    		assertEquals(expected_string_value, test.getSimpleValue());
    		assertEquals(expected_long_value, test.getSimpleLongValue());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		assert(false);
    	}
    }
}