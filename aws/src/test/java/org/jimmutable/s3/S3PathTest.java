package org.jimmutable.s3;

import org.jimmutable.aws.s3.S3Path;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class S3PathTest extends TestCase 
{
	 /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public S3PathTest( String testName )
  {
      super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
      return new TestSuite( S3PathTest.class );
  }
  
  public void testValidation()
  {
	   testContsructorFailure(null);
	   
	   testContsructorFailure(".foo");
	   testContsructorFailure("foo.");
	   
	   testContsructorFailure("-foo");
	   testContsructorFailure("foo-");
	   
	   testContsructorFailure("foo..bar");
	   testContsructorFailure("foo//bar");
	   testContsructorFailure("foo--bar");
	   
	   testContsructorFailure("/foo/ bar /baz.txt");
	   
	   testContsructorFailure("jim.kane@gmail.com");
	   
	   testContsructorFailure("jim_kane");
	   
	   
	   StringBuilder long_string = new StringBuilder();
	   
	   while( long_string.length() < 1500 ) 
		   long_string.append("hello");
	   
	   testContsructorFailure(long_string.toString());
  }
  
  public void testNormalization()
  {
	  testStringConvert("foo.txt", new S3Path("foo.txt"));
	  testStringConvert("/foo.txt", new S3Path("foo.txt"));
	  testStringConvert("/FOO.txt", new S3Path("foo.txt"));
	  
	  testStringConvert(" / foo.txt ", new S3Path("foo.txt"));
	  
	  testStringConvert("/foo/", new S3Path("foo"));
	  
	  testStringConvert("/foo/bar/baz.TXT/", new S3Path("foo/bar/baz.txt"));
	  
	  testStringConvert("", S3Path.PATH_BUCKET_ROOT);
	  testStringConvert("/", S3Path.PATH_BUCKET_ROOT);
	  testStringConvert(" ", S3Path.PATH_BUCKET_ROOT);
  }
  
  public void testRootPath()
  {
	  assertEquals(0,S3Path.PATH_BUCKET_ROOT.getSimpleNumberOfParts());
	  assertEquals(0,S3Path.PATH_BUCKET_ROOT.getSimpleParts().size());
	  
	  
	  assertEquals(null,S3Path.PATH_BUCKET_ROOT.getOptionalPart(0, null));
	  
	  assertEquals(false,S3Path.PATH_BUCKET_ROOT.hasExtension());
	  assertEquals(false,S3Path.PATH_BUCKET_ROOT.hasLastPart());
	  assertEquals(false,S3Path.PATH_BUCKET_ROOT.hasParent());
	  
	  assertEquals(null,S3Path.PATH_BUCKET_ROOT.getOptionalExtension(null));
	  assertEquals(null,S3Path.PATH_BUCKET_ROOT.getOptionalLastPart(null));
	  assertEquals("",S3Path.PATH_BUCKET_ROOT.getSimpleValue());
  }
  
  public void testPath()
  {
	  S3Path test_path = new S3Path("foo/bar/BAZ.txt");
	  
	  assertEquals(3,test_path.getSimpleNumberOfParts());
	  
	  assertEquals(3,test_path.getSimpleParts().size());
	  
	  assertEquals("foo", test_path.getSimpleParts().get(0));
	  assertEquals("bar", test_path.getSimpleParts().get(1));
	  assertEquals("baz.txt", test_path.getSimpleParts().get(2));
	  
	  assertEquals(null, test_path.getOptionalPart(-2, null));
	  assertEquals(null, test_path.getOptionalPart(4, null));
	  
	  assertEquals("foo", test_path.getOptionalPart(0, null));
	  assertEquals("bar", test_path.getOptionalPart(1, null));
	  assertEquals("baz.txt", test_path.getOptionalPart(2, null));
	  
	  assertEquals(true,test_path.hasExtension());
	  assertEquals(true,test_path.hasLastPart());
	  assertEquals(true,test_path.hasParent());
	  
	  assertEquals("txt",test_path.getOptionalExtension(null));
	  assertEquals("baz.txt",test_path.getOptionalLastPart(null));
	  assertEquals("foo/bar/baz.txt",test_path.getSimpleValue());
	  
	  test_path = test_path.getOptionalParent(null);
	  
	  {
		  assertEquals(test_path, new S3Path("foo/bar"));
		  
		  assertEquals(2,test_path.getSimpleNumberOfParts());
		  
		  assertEquals(2,test_path.getSimpleParts().size());
		  
		  assertEquals("foo", test_path.getSimpleParts().get(0));
		  assertEquals("bar", test_path.getSimpleParts().get(1));
		  
		  assertEquals(null, test_path.getOptionalPart(-2, null));
		  assertEquals(null, test_path.getOptionalPart(3, null));
		  
		  assertEquals("foo", test_path.getOptionalPart(0, null));
		  assertEquals("bar", test_path.getOptionalPart(1, null));
		   
		  assertEquals(false,test_path.hasExtension());
		  assertEquals(true,test_path.hasLastPart());
		  assertEquals(true,test_path.hasParent());
		  
		  assertEquals(null,test_path.getOptionalExtension(null));
		  assertEquals("bar",test_path.getOptionalLastPart(null));
		  assertEquals("foo/bar",test_path.getSimpleValue());
	  }
	  
	  test_path = test_path.getOptionalParent(null);
	  
	  {
		  assertEquals(test_path, new S3Path("foo"));
		  
		  assertEquals(1,test_path.getSimpleNumberOfParts());
		  
		  assertEquals(1,test_path.getSimpleParts().size());
		  
		  assertEquals("foo", test_path.getSimpleParts().get(0));
		  
		  assertEquals(null, test_path.getOptionalPart(-2, null));
		  assertEquals(null, test_path.getOptionalPart(1, null));
		  
		  assertEquals("foo", test_path.getOptionalPart(0, null));
		   
		  assertEquals(false,test_path.hasExtension());
		  assertEquals(true,test_path.hasLastPart());
		  assertEquals(true,test_path.hasParent());
		  
		  assertEquals(null,test_path.getOptionalExtension(null));
		  assertEquals("foo",test_path.getOptionalLastPart(null));
		  assertEquals("foo",test_path.getSimpleValue());
	  }
	  
	  test_path = test_path.getOptionalParent(null);
	  
	  {
		  assertEquals(test_path, S3Path.PATH_BUCKET_ROOT);
	  }
	  
	  assertEquals(false,test_path.hasParent());
	  
	  assertEquals(null, test_path.getOptionalParent(null));
  }

  private void testStringConvert(String str, S3Path expected_result)
  {
	  S3Path result = S3Path.CONVERTER.fromString(str, null);

	  assertEquals(result, expected_result);
  }

  private void testContsructorFailure(String str)
  {
	   try
	   {
		   S3Path result = new S3Path(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
  }
}

