package org.jimmutable.s3;

import org.jimmutable.aws.s3.S3BucketName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class S3BucketNameTest extends TestCase 
{
	 /**
    * Create the test case
    *
    * @param testName name of the test case
    */
   public S3BucketNameTest( String testName )
   {
       super( testName );
   }

   /**
    * @return the suite of tests being tested
    */
   public static Test suite()
   {
       return new TestSuite( S3BucketNameTest.class );
   }
   
   public void testBucketName()
   {
	   testStringConvert("some.bucket", new S3BucketName("some.bucket"));
	   testStringConvert("SOME.bucket", new S3BucketName("some.bucket"));
	   testStringConvert(" SOME.bucket ", new S3BucketName("some.bucket"));
	   
	   testStringConvert("dev-jim-kane.some.bucket", new S3BucketName("dev-jim-kane.some.bucket"));
	   
	   testContsructorFailure(null);
	   testContsructorFailure("");
	   testContsructorFailure(" ");
	   testContsructorFailure("foo_bar");
	   testContsructorFailure(".foo");
	   testContsructorFailure("foo.");
	   testContsructorFailure("foo..bar");
   }

   private void testStringConvert(String str, S3BucketName expected_result)
   {
	   S3BucketName result = S3BucketName.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
   }

   private void testContsructorFailure(String str)
   {
	   try
	   {
		   S3BucketName result = new S3BucketName(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
   }
}
