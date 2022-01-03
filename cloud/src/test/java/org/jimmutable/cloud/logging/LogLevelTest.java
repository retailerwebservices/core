package org.jimmutable.cloud.logging;

//import org.apache.logging.log4j.Level;
import org.jimmutable.core.exceptions.ValidationException;
import org.slf4j.event.Level;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LogLevelTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LogLevelTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LogLevelTest.class );
    }

    public void testLogLevel()
    {
    	//failure cases
    	assertFailure(null);
    	assertFailure("");
    	assertFailure("hello");
    	assertFailure("123-456-78903"); 
    	assertFailure("123-456-783"); 

    	//From string for all level values
    	for(Level entry : Level.values())
    	{
        	assertToString(entry.name(), entry.name(), Level.valueOf(entry.name()));
    	}
    }
    
    private static void assertFailure(String value)
    {
    	try
    	{
    		new LogLevel(value);
    		fail();
    	}
    	catch(ValidationException e)
    	{
    		
    	}
    }
    
    private static void assertToString(String parse, String expected_string_value, Level expected_log_level)
    {
    	try
    	{ 
    		LogLevel test = new LogLevel(parse);
    		
    		assertEquals(expected_string_value, test.getSimpleValue());
    		assertEquals(expected_log_level, test.getSimpleLevel());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		fail();
    	}
    }
}