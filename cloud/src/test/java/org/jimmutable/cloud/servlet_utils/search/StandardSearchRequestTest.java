package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class StandardSearchRequestTest extends StubTest
{
	private SortBy sort_by = new SortBy(new SearchIndexFieldDefinition(new FieldName("field"), SearchIndexFieldType.ATOM), SortDirection.DESCENDING);
	private Sort sort = new Sort(sort_by);
	
	@Test
	public void testStandardSearchRequest()
	{
		StandardSearchRequest result = null;
		
		try
		{
			result = new StandardSearchRequest("Test Query", 50, 20, sort);
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			fail();
		}

		result = new StandardSearchRequest("Test Query 2");
		assertTrue(result.getSimpleQueryString().equals("Test Query 2"));
		assertTrue(result.getSimpleMaxResults() == StandardSearchRequest.DEFAULT_MAX_RESULTS);
		assertTrue(result.getSimpleStartResultsAfter() == StandardSearchRequest.DEFAULT_START_RESULTS_AFTER);
		assertTrue(result.getSimpleSort() == Sort.DEFAULT_SORT);
		
		result = new StandardSearchRequest("Test Query 3", 0, 0, sort);
		assertTrue(result.getSimpleMaxResults() == 0);
		assertTrue(result.getSimpleStartResultsAfter() == 0);
		assertEquals(result.getSimpleSort(), sort);
	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.StandardSearchRequest\","
			     , "  \"query\" : \"Test Query\","
			     , "  \"max_results\" : 50,"
			     , "  \"start_results_after\" : 20,"
			     , "  \"sort\" : {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.Sort\","
			     , "    \"sort_order\" : [ {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "      \"field\" : {"
			     , "        \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "        \"name\" : {"
			     , "          \"type_hint\" : \"jimmutable.FieldName\","
			     , "          \"name\" : \"field\""
			     , "        },"
			     , "        \"type\" : \"keyword\""
			     , "      },"
			     , "      \"direction\" : \"descending\""
			     , "    } ]"
			     , "  }"
			     , "}"
			);

		StandardSearchRequest obj = (StandardSearchRequest)StandardObject.deserialize(obj_string);
		assertTrue( obj.getSimpleQueryString().equals("Test Query"));
		assertTrue( obj.getSimpleMaxResults() == 50 );
		assertTrue( obj.getSimpleStartResultsAfter() == 20);
		assertEquals( obj.getSimpleSort(), sort);
	}
}
