package org.jimmutable.gcloud.pubsub;

import org.jimmutable.gcloud.pubsub.TopicId;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TopicIdTest  extends TestCase 
{
	 /**
  * Create the test case
  *
  * @param testName name of the test case
  */
 public TopicIdTest( String testName )
 {
     super( testName );
 }

 /**
  * @return the suite of tests being tested
  */
 public static Test suite()
 {
     return new TestSuite( TopicIdTest.class );
 }
 
 public void testTopicID()
 {
	   testStringConvert("some-id", new TopicId("some-id"));
	   testStringConvert("some-id-1234", new TopicId("some-id-1234"));
	   testStringConvert("SOME-id", new TopicId("some-id"));
	   testStringConvert(" SOME-id ", new TopicId("some-id"));
	   
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

 private void testStringConvert(String str, TopicId expected_result)
 {
	 TopicId result = TopicId.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
 }

 private void testContsructorFailure(String str)
 {
	   try
	   {
		   TopicId result = new TopicId(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
 }
}
