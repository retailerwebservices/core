package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.examples.product_data.BrandCode;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.StringableTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BrandCodeTest extends StringableTest
{
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BrandCodeTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BrandCodeTest.class );
    }

 
	public Stringable fromString(String src) 
	{
		return new BrandCode(src);
	}

	public void testBrandCode()
    {
    	assertNotValid(null);
    	assertNotValid("foo-bar");
    	assertNotValid("foo:bar");
    	assertNotValid("");
    	
    	assertValid("AMN","AMN");
    	assertValid("amn","AMN");
    	assertValid("gE","GE");
    	
    	assertValid("ca_sny","CA_SNY");
    }
}
