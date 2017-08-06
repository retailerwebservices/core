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

    	assertToString("0", "0000-0000-0000-0000",0);
    	assertToString("1", "0000-0000-0000-0001",1);
    	
    	
    	assertToString("42fb-e16d-95ac-8274", "42fb-e16d-95ac-8274",0x42fb_e16d_95ac_8274l);
    	assertToString("42fb.e16d.95ac.8274", "42fb-e16d-95ac-8274",0x42fb_e16d_95ac_8274l);
    	assertToString("42fbe16d95ac8274", "42fb-e16d-95ac-8274",0x42fb_e16d_95ac_8274l);
    	assertToString("42fbe16d95ac8274", "42fb-e16d-95ac-8274",0x42fb_e16d_95ac_8274l);
    	assertToString("0x42fbe16d95ac8274", "42fb-e16d-95ac-8274",0x42fb_e16d_95ac_8274l);
    	
    	assertToString("c1-4296-742d-5e8d", "00c1-4296-742d-5e8d",0x00c1_4296_742d_5e8dl);
    	assertToString("C1-4296-742D-5E8D", "00c1-4296-742d-5e8d",0x00c1_4296_742d_5e8dl);
  
    	
    	assertToString("2232-f768-2d2f-86d6", "2232-f768-2d2f-86d6",0x2232_f768_2d2f_86d6l);
    	assertToString(" 2232-f768-2d2f-86d6 ", "2232-f768-2d2f-86d6",0x2232_f768_2d2f_86d6l);
    	
   
    	assertToString(" 2232f768- 2D2F  86d6 ", "2232-f768-2d2f-86d6",0x2232_f768_2d2f_86d6l);
    	

    	// Test the random ID function
    	{
    		Set<ObjectId> ids = new HashSet();

    		for ( int i = 0; i < 10_000; i++ )
    		{
    			ObjectId cur = ObjectId.randomID();
    			assertEquals(19,cur.getSimpleValue().length());

    			ids.add(cur);
    		}

    		assertEquals(ids.size(),10_000);
    	}
    }
    
    private void assertFailure(String value)
    {
    	try
    	{
    		new ObjectId(value);
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
    		ObjectId test = new ObjectId(parse);
    		
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