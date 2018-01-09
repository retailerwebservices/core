package org.jimmutable.core.objects.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PostalCodeTest extends TestCase {

 /*
  * Create the test case
  *
  * @param testName name of the test case
  */
 public PostalCodeTest( String testName )
 {
     super( testName );
 }

 /**
  * @return the suite of tests being tested
  */
 public static Test suite()
 {
     return new TestSuite( PostalCodeTest.class );
 }
 
 public void testPostalCode()
 {
	   testStringConvert("99999", new PostalCode("99999"));
	   testStringConvert("99999-9999", new PostalCode("99999-9999"));
	   testStringConvert("999999999", new PostalCode("99999-9999"));
	   testStringConvert(" 99999 ", new PostalCode("99999"));
	   testStringConvert("K1A 0B1", new PostalCode("K1A 0B1"));
	   
	   testConstructorFailure(null);
	   testConstructorFailure("");
	   testConstructorFailure(" ");
	   testConstructorFailure("foo_bar");
	   testConstructorFailure(".foo");
	   testConstructorFailure("foo.");
	   testConstructorFailure("foo..bar");
	   testConstructorFailure("foo..bar");
	   testConstructorFailure("foo/bar");
	   testConstructorFailure("12345678901234567890123456789012345678901234567890123456789012345");
 }

 private void testStringConvert(String str, PostalCode expected_result)
 {
	 PostalCode result = PostalCode.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
 }

 private void testConstructorFailure(String str)
 {
	   try
	   {
		   PostalCode result = new PostalCode(str);
		   fail();
	   }
	   catch(Exception e)
	   {
		   
	   }
 }	
	
	
}
