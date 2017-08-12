package org.jimmutable.gcloud.search;

import org.jimmutable.gcloud.AppTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SubstringUtilsTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SubstringUtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SubstringUtilsTest.class );
    }

   
    public void testAppendAllSubstringsOfLength()
    {
	    	{ 
	    		StringBuilder builder = new StringBuilder();
	    		
	    		SubstringUtils.appendAllSubstringsOfLength(builder, "abcd", 1);
	    		
	    		assertEquals("a b c d", builder.toString());
	    	}
	    	
	    	{ 
	    		StringBuilder builder = new StringBuilder();
	    		
	    		SubstringUtils.appendAllSubstringsOfLength(builder, "abcd", 2);
	    		
	    		assertEquals("ab bc cd", builder.toString());
	    	}
	    	
	    	{ 
	    		StringBuilder builder = new StringBuilder();
	    		
	    		SubstringUtils.appendAllSubstringsOfLength(builder, "abcd", 3);
	    		
	    		assertEquals("abc bcd", builder.toString());
	    	}
	    	
	    	{ 
	    		StringBuilder builder = new StringBuilder();

	    		SubstringUtils.appendAllSubstringsOfLength(builder, "abcd", 4);

	    		assertEquals("abcd", builder.toString());
	    	}
    }
    
    public void testCreateSubstringMatchingText()
    {
    		assertEquals("abcd a b c d ab bc cd abc bcd abcd", SubstringUtils.createSubstringMatchingText("abcd", 1, 50, null));
    		assertEquals("abcd a b c d ab bc cd abc bcd abcd", SubstringUtils.createSubstringMatchingText("AbCd", 1, 50, null));
    		assertEquals("abcd a b c d ab bc cd abc bcd abcd", SubstringUtils.createSubstringMatchingText("  \tAbCd\n", 1, 50, null));
    		
    		assertEquals(null, SubstringUtils.createSubstringMatchingText(null, 1, 50, null));
    		assertEquals(null, SubstringUtils.createSubstringMatchingText("abcd", 0, 50, null));
    		assertEquals(null, SubstringUtils.createSubstringMatchingText("abcd", 10, 2, null));
    		assertEquals(null, SubstringUtils.createSubstringMatchingText("abcd", 10, -2, null));
    }
}
