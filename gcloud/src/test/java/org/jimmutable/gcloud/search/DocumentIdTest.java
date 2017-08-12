package org.jimmutable.gcloud.search;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DocumentIdTest extends TestCase 
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public DocumentIdTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( DocumentIdTest.class );
	}

	public void testDocumentId()
	{
		testStringConvert("some-id", new DocumentId("some-id"));
		testStringConvert("some-id-1234", new DocumentId("some-id-1234"));
		testStringConvert("SOME-id", new DocumentId("some-id"));
		testStringConvert(" SOME-id ", new DocumentId("some-id"));

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

	private void testStringConvert(String str, DocumentId expected_result)
	{
		DocumentId result = DocumentId.CONVERTER.fromString(str, null);

		assertEquals(result, expected_result);
	}

	private void testConstructorFailure(String str)
	{
		try
		{
			DocumentId result = new DocumentId(str);
			assert(false);
		}
		catch(Exception e)
		{
			assert(true);
		}
	}
}
