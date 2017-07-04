package org.jimmutable.s3;

import org.jimmutable.aws.s3.S3AbsolutePath;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class S3AbsolutePathTest extends TestCase 
{
	 /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public S3AbsolutePathTest( String testName )
  {
      super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
      return new TestSuite( S3AbsolutePathTest.class );
  }
  
  public void testAbsolutePath()
  {
	   testStringConvert("BUCKET:/FOO/BAR/BAZ.TXT", new S3AbsolutePath("bucket:foo/bar/baz.txt"));
	   testStringConvert(" BUCKET : /FOO/BAR/BAZ.TXT ", new S3AbsolutePath("bucket:foo/bar/baz.txt"));
	   
	   S3AbsolutePath path = new S3AbsolutePath(" BUCKET : /FOO/BAR/BAZ.TXT ");
	   assertEquals("bucket:foo/bar/baz.txt", path.getSimpleValue());
	   
	   
	   testContsructorFailure(null);
	   testContsructorFailure("");
	   testContsructorFailure(" ");
	   testContsructorFailure("foo_bar");
	   testContsructorFailure(".foo");
	   testContsructorFailure("foo.");
	   testContsructorFailure("foo..bar");
	   testContsructorFailure(":bar");
  }

  private void testStringConvert(String str, S3AbsolutePath expected_result)
  {
	  S3AbsolutePath result = S3AbsolutePath.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
  }

  private void testContsructorFailure(String str)
  {
	   try
	   {
		   S3AbsolutePath result = new S3AbsolutePath(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
  }
}
