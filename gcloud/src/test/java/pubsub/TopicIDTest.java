package pubsub;

import org.jimmutable.gcloud.pubsub.TopicID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TopicIDTest  extends TestCase 
{
	 /**
  * Create the test case
  *
  * @param testName name of the test case
  */
 public TopicIDTest( String testName )
 {
     super( testName );
 }

 /**
  * @return the suite of tests being tested
  */
 public static Test suite()
 {
     return new TestSuite( TopicIDTest.class );
 }
 
 public void testTopicID()
 {
	   testStringConvert("some-id", new TopicID("some-id"));
	   testStringConvert("some-id-1234", new TopicID("some-id-1234"));
	   testStringConvert("SOME-id", new TopicID("some-id"));
	   testStringConvert(" SOME-id ", new TopicID("some-id"));
	   
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

 private void testStringConvert(String str, TopicID expected_result)
 {
	 TopicID result = TopicID.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
 }

 private void testContsructorFailure(String str)
 {
	   try
	   {
		   TopicID result = new TopicID(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
 }
}
