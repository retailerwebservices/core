package org.jimmutable.environment;

import org.jimmutable.aws.environment.HostName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HostNameTest extends TestCase 
{
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HostNameTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( HostNameTest.class );
    }
    
    public void testHostName()
    {
    	// Test codes
    	testStringConvert("foo", new HostName("foo"));
    	testStringConvert("FoO", new HostName("foo"));
    	
    	testContsructorFailure(null);
    	testContsructorFailure("");
    	testContsructorFailure("jim.kane@gmail.com");
    }
    
    private void testStringConvert(String str, HostName expected_result)
    {
    	HostName result = HostName.CONVERTER.fromString(str, null);
    	
    	assertEquals(result, expected_result);
    }
    
    private void testContsructorFailure(String str)
    {
    	try
    	{
    		HostName result = new HostName(str);
    		assert(false);
    	}
    	catch(Exception e)
    	{
    		assert(true);
    	}
    }
}
