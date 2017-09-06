package org.jimmutable.aws.servlet_utils.search;

import java.util.ArrayList;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SearchResponseOKTest extends TestCase
{
	public SearchResponseOKTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchResponseOK.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		return new TestSuite(SearchResponseOKTest.class);
	}

	public void testSearchResponseOKTest()
	{
		SearchResponseOK result = null;
		try
		{
			result = new SearchResponseOK(new StandardSearchRequest(""), new ArrayList<OneSearchResult>(), 1, true, true, 20, 1);
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == SearchResponseOK.HTTP_STATUS_CODE_OK);

		StandardSearchRequest search_request = new StandardSearchRequest("TestSearchRequest");
		result = new SearchResponseOK(
				search_request, 
				new ArrayList<OneSearchResult>(), 
				0, false, false, 0, 0);
		assert (result.getSimpleSearchRequest() == search_request);
		assert (result.getSimpleSearchRequest().getSimpleQueryString() == "TestSearchRequest");
		assert (result.getSimpleFirstResultIdx() == 0);
		assert (result.getSimpleHasMoreResults() == false);
		assert (result.getSimpleHasPreviousResults() == false);
		assert (result.getSimpleStartOfNextPageOfResults() == -1);
		assert (result.getSimpleStartOfPreviousPageOfResults() == -1);
	}
	
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.SearchResponseOK\","
			     , "  \"search_request\" : {"
			     , "    \"type_hint\" : \"jimmutable.aws.servlet_utils.search.StandardSearchRequest\","
			     , "    \"query\" : \"\","
			     , "    \"max_results\" : 100,"
			     , "    \"start_results_after\" : 0"
			     , "  },"
			     , "  \"results\" : [ ],"
			     , "  \"first_result_idx\" : 1,"
			     , "  \"has_more_results\" : true,"
			     , "  \"has_previous_results\" : true,"
			     , "  \"start_of_next_page_of_results\" : 20,"
			     , "  \"start_of_previous_page_of_results\" : 1"
			     , "}"
			);

			SearchResponseOK obj = (SearchResponseOK)StandardObject.deserialize(obj_string);
			assert(obj.getSimpleSearchRequest() != null);
			assert(obj.getSimpleSearchRequest().getSimpleQueryString().equals(""));
			assert(obj.getSimpleFirstResultIdx() == 1);
			assert(obj.getSimpleHasMoreResults() == true);
			assert(obj.getSimpleHasPreviousResults() == true);
			assert(obj.getSimpleStartOfNextPageOfResults() == 20);
			assert(obj.getSimpleStartOfPreviousPageOfResults() == 1);
	}
}
