package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class StandardSearchRequestTest extends StubTest
{
	
	@Test
	public void testStandardSearchRequest()
	{
		StandardSearchRequest result = null;
		try
		{
			result = new StandardSearchRequest("Test Query", 50, 20);
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			fail();
		}

		result = new StandardSearchRequest("Test Query 2");
		assertTrue(result.getSimpleQueryString().equals("Test Query 2"));
		assertTrue(result.getSimpleMaxResults() == StandardSearchRequest.DEFAULT_MAX_RESULTS);
		assertTrue(result.getSimpleStartResultsAfter() == StandardSearchRequest.DEFAULT_START_RESULTS_AFTER);
		
		result = new StandardSearchRequest("Test Query 3", 0, 0);
		assertTrue(result.getSimpleMaxResults() == 0);
		assertTrue(result.getSimpleStartResultsAfter() == 0);
	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.StandardSearchRequest\","
			     , "  \"query\" : \"Test Query\","
			     , "  \"max_results\" : 50,"
			     , "  \"start_results_after\" : 20"
			     , "}"
			);

		StandardSearchRequest obj = (StandardSearchRequest)StandardObject.deserialize(obj_string);
		assertTrue( obj.getSimpleQueryString().equals("Test Query"));
		assertTrue( obj.getSimpleMaxResults() == 50 );
		assertTrue( obj.getSimpleStartResultsAfter() == 20);
	}
}
