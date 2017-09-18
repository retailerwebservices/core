package org.jimmutable.environment;

import org.jimmutable.aws.environment.CloudName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CloudNameTest extends TestCase 
{
	 /**
    * Create the test case
    *
    * @param testName name of the test case
    */
   public CloudNameTest( String testName )
   {
       super( testName );
   }

   /**
    * @return the suite of tests being tested
    */
   public static Test suite()
   {
       return new TestSuite( CloudNameTest.class );
   }
   
   public void testHostName()
   {
	   // Test codes
	   testStringConvert("foo", new CloudName("foo"));
	   testStringConvert("FoO", new CloudName("foo"));

	   testContsructorFailure(null);
	   testContsructorFailure("");
	   testContsructorFailure("jim.kane@gmail.com");
	   testContsructorFailure("-jim");
	   testContsructorFailure("jim-");
	   testContsructorFailure("jim--kane");
   }
 

   private void testStringConvert(String str, CloudName expected_result)
   {
	   CloudName result = CloudName.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
   }

   private void testContsructorFailure(String str)
   {
	   try
	   {
		   CloudName result = new CloudName(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
   }
}
