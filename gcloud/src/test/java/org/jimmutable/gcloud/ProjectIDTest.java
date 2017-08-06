package org.jimmutable.gcloud;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ProjectIDTest extends TestCase 
{
	 /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public ProjectIDTest( String testName )
  {
      super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
      return new TestSuite( ProjectIDTest.class );
  }
  
  public void testProjectID()
  {
	   testStringConvert("some-id", new ProjectID("some-id"));
	   testStringConvert("some-id-1234", new ProjectID("some-id-1234"));
	   testStringConvert("SOME-id", new ProjectID("some-id"));
	   testStringConvert(" SOME-id ", new ProjectID("some-id"));
	   
	   testContsructorFailure(null);
	   testContsructorFailure("");
	   testContsructorFailure(" ");
	   testContsructorFailure("foo_bar");
	   testContsructorFailure(".foo");
	   testContsructorFailure("foo.");
	   testContsructorFailure("foo..bar");
	   testContsructorFailure("foo..bar");
	   testContsructorFailure("foo/bar");
  }

  private void testStringConvert(String str, ProjectID expected_result)
  {
	  ProjectID result = ProjectID.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
  }

  private void testContsructorFailure(String str)
  {
	   try
	   {
		   ProjectID result = new ProjectID(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
  }
}
