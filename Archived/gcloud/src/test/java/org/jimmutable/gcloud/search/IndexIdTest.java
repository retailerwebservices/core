package org.jimmutable.gcloud.search;


import org.jimmutable.core.utils.StringableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IndexIdTest extends TestCase 
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public IndexIdTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( IndexIdTest.class );
	}
	
private StringableTester<IndexId> tester = new StringableTester(new IndexId.MyConverter());
	
	public void testValid()
	{
		tester.assertValid("some-id", "some-id");
		tester.assertValid("some-id-1234", "some-id-1234");
		tester.assertValid("SOME-id", "some-id");
		tester.assertValid(" SOME-id ", "some-id");
	}

	public void testInvalid()
	{		
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid(" ");
		tester.assertInvalid("foo_bar");
		tester.assertInvalid(".foo");
		tester.assertInvalid("foo.");
		tester.assertInvalid("foo..bar");
		tester.assertInvalid("foo..bar");
		tester.assertInvalid("foo/bar");
		tester.assertInvalid("abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc"); // more than 64 characters
	
	}

	public void testIndexId()
	{
		testStringConvert("some-id", new IndexId("some-id"));
		testStringConvert("some-id-1234", new IndexId("some-id-1234"));
		testStringConvert("SOME-id", new IndexId("some-id"));
		testStringConvert(" SOME-id ", new IndexId("some-id"));

		testConstructorFailure(null);
		testConstructorFailure("");
		testConstructorFailure(" ");
		testConstructorFailure("foo_bar");
		testConstructorFailure(".foo");
		testConstructorFailure("foo.");
		testConstructorFailure("foo..bar");
		testConstructorFailure("foo..bar");
		testConstructorFailure("foo/bar");
		testConstructorFailure("abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc"); // more than 64 characters
	}

	private void testStringConvert(String str, IndexId expected_result)
	{
		IndexId result = IndexId.CONVERTER.fromString(str, null);

		assertEquals(result, expected_result);
	}

	private void testConstructorFailure(String str)
	{
		try
		{
			IndexId result = new IndexId(str);
			assert(false);
		}
		catch(Exception e)
		{
			assert(true);
		}
	}
}