package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class SearchResponseOKTest extends StubTest
{
	
	@Test
	public void testSearchResponseOKTest()
	{
		SearchResponseOK result = null;
		
		// Test with a non-empty search result list
		Map<FieldName, String> search_result_map = new FieldHashMap<>();
		search_result_map.put(new FieldName("test_key"), "Test Value");
		search_result_map.put(new FieldName("test_key2"), "Test Value2");

		OneSearchResult one_search_result = new OneSearchResult(search_result_map);
		List<OneSearchResult> search_result_list = new ArrayList<>();
		search_result_list.add(one_search_result);

		StandardSearchRequest search_request = new StandardSearchRequest("TestSearchRequest");
		
		try
		{
			result = new SearchResponseOK(search_request, search_result_list, 1, true,
					true, 20, 1);

			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			assert (false);
		}
		
		assert (Objects.equals(result.getSimpleSearchRequest(), search_request));
		assert (result.getSimpleFirstResultIdx() == 1);
		assert (result.getSimpleHasMoreResults() == true);
		assert (result.getSimpleHasPreviousResults() == true);
		assert (result.getSimpleStartOfNextPageOfResults() == 20);
		assert (result.getSimpleStartOfPreviousPageOfResults() == 1);
		assert (result.getSimpleHTTPResponseCode() == SearchResponseOK.HTTP_STATUS_CODE_OK);
		assert (result.getSimpleResults().size() == 1);
		assert (Objects.equals(search_result_list, result.getSimpleResults()));
		
		
		// Test with empty object parameters passed to constructor
		try
		{
			result = new SearchResponseOK(new StandardSearchRequest(""), new ArrayList<OneSearchResult>(), 0, false, false, 0, 0);
		} catch (Exception e)
		{
			assert(false);
		}
		
		assert (result.getSimpleSearchRequest() != null);
		assert (result.getSimpleSearchRequest().getSimpleQueryString() == "");
		assert (result.getSimpleResults().size() == 0);
		assert (result.getSimpleFirstResultIdx() == 0);
		assert (result.getSimpleHasMoreResults() == false);
		assert (result.getSimpleHasPreviousResults() == false);
		assert (result.getSimpleStartOfNextPageOfResults() == -1);
		assert (result.getSimpleStartOfPreviousPageOfResults() == -1);

	}
	
	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.SearchResponseOK\","
			     , "  \"search_request\" : {"
			     , "    \"type_hint\" : \"jimmutable.aws.servlet_utils.search.StandardSearchRequest\","
			     , "    \"query\" : \"TestSearchRequest\","
			     , "    \"max_results\" : 100,"
			     , "    \"start_results_after\" : 0"
			     , "  },"
			     , "  \"results\" : [ {"
			     , "    \"type_hint\" : \"jimmutable.aws.servlet_utils.search.OneSearchResult\","
			     , "    \"result\" : [ {"
			     , "      \"type_hint\" : \"MapEntry\","
			     , "      \"key\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"test_key2\""
			     , "      },"
			     , "      \"value\" : \"Test Value2\""
			     , "    }, {"
			     , "      \"type_hint\" : \"MapEntry\","
			     , "      \"key\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"test_key\""
			     , "      },"
			     , "      \"value\" : \"Test Value\""
			     , "    } ]"
			     , "  } ],"
			     , "  \"first_result_idx\" : 1,"
			     , "  \"has_more_results\" : true,"
			     , "  \"has_previous_results\" : true,"
			     , "  \"start_of_next_page_of_results\" : 20,"
			     , "  \"start_of_previous_page_of_results\" : 1"
			     , "}"
			);
		
		SearchResponseOK obj = (SearchResponseOK) StandardObject.deserialize(obj_string);
		assert (obj.getSimpleSearchRequest().getSimpleQueryString().equals("TestSearchRequest"));
		assert (obj.getSimpleFirstResultIdx() == 1);
		assert (obj.getSimpleHasMoreResults() == true);
		assert (obj.getSimpleHasPreviousResults() == true);
		assert (obj.getSimpleStartOfNextPageOfResults() == 20);
		assert (obj.getSimpleStartOfPreviousPageOfResults() == 1);
		assert (obj.getSimpleHTTPResponseCode() == SearchResponseOK.HTTP_STATUS_CODE_OK);
		assert (obj.getSimpleResults().size() == 1);
	}
}
