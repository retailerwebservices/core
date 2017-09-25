package org.jimmutable.gcloud.pubsub;

import org.jimmutable.gcloud.pubsub.SubscriptionId;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SubscriptionIdTest extends TestCase 
{
	 /**
  * Create the test case
  *
  * @param testName name of the test case
  */
 public SubscriptionIdTest( String testName )
 {
     super( testName );
 }

 /**
  * @return the suite of tests being tested
  */
 public static Test suite()
 {
     return new TestSuite( SubscriptionIdTest.class );
 }
 
 public void testSubscriptionID()
 {
	   testStringConvert("some-id", new SubscriptionId("some-id"));
	   testStringConvert("some-id-1234", new SubscriptionId("some-id-1234"));
	   testStringConvert("SOME-id", new SubscriptionId("some-id"));
	   testStringConvert(" SOME-id ", new SubscriptionId("some-id"));
	   
	   testConstructorFailure(null);
	   testConstructorFailure("");
	   testConstructorFailure(" ");
	   testConstructorFailure("foo_bar");
	   testConstructorFailure(".foo");
	   testConstructorFailure("foo.");
	   testConstructorFailure("foo..bar");
	   testConstructorFailure("foo..bar");
	   testConstructorFailure("foo/bar");
 }

 private void testStringConvert(String str, SubscriptionId expected_result)
 {
	 SubscriptionId result = SubscriptionId.CONVERTER.fromString(str, null);

	   assertEquals(result, expected_result);
 }

 private void testConstructorFailure(String str)
 {
	   try
	   {
		   SubscriptionId result = new SubscriptionId(str);
		   assert(false);
	   }
	   catch(Exception e)
	   {
		   assert(true);
	   }
 }
}
