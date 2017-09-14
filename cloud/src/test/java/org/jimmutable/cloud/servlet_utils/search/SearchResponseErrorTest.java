package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SearchResponseErrorTest extends TestCase
{

	public SearchResponseErrorTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();
		return new TestSuite(SearchResponseErrorTest.class);
	}

	public void testSearchResponseErrorTest()
	{
		SearchResponseError result = null;
		StandardSearchRequest search_request = new StandardSearchRequest("TestSearchRequest");

		try
		{
			result = new SearchResponseError(search_request, "Test Message");
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == SearchResponseError.HTTP_STATUS_CODE_ERROR);
		assert (Objects.equals(result.getSimpleSearchRequest(), search_request));
		assert (result.getOptionalMessage(null).equals("Test Message"));

		try
		{
			result = new SearchResponseError(search_request);
		} catch ( Exception e )
		{
			assert (false);
		}
		
		assert (result.getOptionalMessage("default").equals("default"));
	}
	
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.SearchResponseError\","
			     , "  \"search_request\" : {"
			     , "    \"type_hint\" : \"jimmutable.aws.servlet_utils.search.StandardSearchRequest\","
			     , "    \"query\" : \"TestSearchRequest\","
			     , "    \"max_results\" : 100,"
			     , "    \"start_results_after\" : 0"
			     , "  },"
			     , "  \"message\" : \"Test Message\""
			     , "}"
		);

		SearchResponseError obj = (SearchResponseError)StandardObject.deserialize(obj_string);
		assert (obj.getSimpleSearchRequest().getSimpleQueryString().equals("TestSearchRequest"));
		assert (obj.getOptionalMessage(null).equals("Test Message"));
		assert (obj.getSimpleHTTPResponseCode() == SearchResponseError.HTTP_STATUS_CODE_ERROR);
	}
}
