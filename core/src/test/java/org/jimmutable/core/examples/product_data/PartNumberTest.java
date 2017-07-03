package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.examples.product_data.PartNumber;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.StringableTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PartNumberTest extends StringableTest
{
	 /**
    * Create the test case
    *
    * @param testName name of the test case
    */
   public PartNumberTest( String testName )
   {
       super( testName );
   }

   /**
    * @return the suite of tests being tested
    */
   public static Test suite()
   {
       return new TestSuite( PartNumberTest.class );
   }

 
   public Stringable fromString(String src) 
	{
		return new PartNumber(src);
	}
   
   public void testBrandCode()
   {
   	assertNotValid(null);
   	assertNotValid("foo-bar");
   	assertNotValid("foo:bar");
   	assertNotValid("");
   	assertNotValid("foo!");
   	
   	assertValid("ABB1924","ABB1924");
   	assertValid("abb1924","ABB1924");
   	assertValid("aBb1924","ABB1924");
   }
}

