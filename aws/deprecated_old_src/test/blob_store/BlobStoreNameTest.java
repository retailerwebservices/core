package org.jimmutable.blob_store;

import org.jimmutable.aws.blob_store.BlobStoreName;
import org.jimmutable.simple_object_store.SimpleStoreNameTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BlobStoreNameTest extends TestCase 
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public BlobStoreNameTest( String testName )
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
		testStringConvert("some.store", new BlobStoreName("some.store"));
		testStringConvert("SOME.store", new BlobStoreName("some.store"));
		testStringConvert(" SOME.store ", new BlobStoreName("some.store"));

		testStringConvert("dev-jim-kane.some.store", new BlobStoreName("dev-jim-kane.some.store"));

		testContsructorFailure(null);
		testContsructorFailure("");
		testContsructorFailure(" ");
		testContsructorFailure("foo_bar");
		testContsructorFailure(".foo");
		testContsructorFailure("foo.");
		testContsructorFailure("foo..bar");
	}

	private void testStringConvert(String str, BlobStoreName expected_result)
	{
		BlobStoreName result = BlobStoreName.CONVERTER.fromString(str, null);

		assertEquals(result, expected_result);
	}

	private void testContsructorFailure(String str)
	{
		try
		{
			BlobStoreName result = new BlobStoreName(str);
			assert(false);
		}
		catch(Exception e)
		{
			assert(true);
		}
	}
}

