package pubsub;

import org.jimmutable.gcloud.pubsub.SubscriptionID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SubscriptionIDTest extends TestCase 
{
	 /**
  * Create the test case
  *
  * @param testName name of the test case
  */
 public SubscriptionIDTest( String testName )
 {
     super( testName );
 }

 /**
  * @return the suite of tests being tested
  */
 public static Test suite()
 {
     return new TestSuite( SubscriptionIDTest.class );
 }
 
 public void testSubscriptionID()
 {
	   testStringConvert("some-id", new SubscriptionID("some-id"));
	   testStringConvert("some-id-1234", new SubscriptionID("some-id-1234"));
	   testStringConvert("SOME-id", new SubscriptionID("some-id"));
	   testStringConvert(" SOME-id ", new SubscriptionID("some-id"));
	   
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

 private void testStringConvert(String str, SubscriptionID expected_result)
 {
	 SubscriptionID result = SubscriptionID.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
 }

 private void testContsructorFailure(String str)
 {
	   try
	   {
		   SubscriptionID result = new SubscriptionID(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
 }
}
