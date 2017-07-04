package org.jimmutable.simple_object_store;

import org.jimmutable.aws.s3.S3BucketName;
import org.jimmutable.aws.simple_object_store.SimpleStoreName;
import org.jimmutable.s3.S3BucketNameTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleStoreNameTest extends TestCase 
{
	 /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public SimpleStoreNameTest( String testName )
  {
      super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
      return new TestSuite( SimpleStoreNameTest.class );
  }
  
  public void testStoreName()
  {
	   testStringConvert("some.store", new SimpleStoreName("some.store"));
	   testStringConvert("SOME.store", new SimpleStoreName("some.store"));
	   testStringConvert(" SOME.store ", new SimpleStoreName("some.store"));
	   
	   testStringConvert("dev-jim-kane.some.store", new SimpleStoreName("dev-jim-kane.some.store"));
	   
	   testContsructorFailure(null);
	   testContsructorFailure("");
	   testContsructorFailure(" ");
	   testContsructorFailure("foo_bar");
	   testContsructorFailure(".foo");
	   testContsructorFailure("foo.");
	   testContsructorFailure("foo..bar");
  }

  private void testStringConvert(String str, SimpleStoreName expected_result)
  {
	  SimpleStoreName result = SimpleStoreName.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
  }

  private void testContsructorFailure(String str)
  {
	   try
	   {
		   SimpleStoreName result = new SimpleStoreName(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
  }
}
