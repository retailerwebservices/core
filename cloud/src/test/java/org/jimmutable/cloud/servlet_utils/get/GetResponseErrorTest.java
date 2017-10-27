package org.jimmutable.cloud.servlet_utils.get;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.core.objects.StandardObject;
import org.junit.Test;

public class GetResponseErrorTest extends StubTest
{
	
	@Test
	public void testGetResponseError()
	{
		GetResponseError result = null;
		try
		{
			result = new GetResponseError();
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == GetResponseError.HTTP_STATUS_CODE_ERROR);
		assert (result.getOptionalMessage(null) == null);

		result = new GetResponseError("Test Message");
		assert (result.getOptionalMessage(null).equals("Test Message"));
		assert (result.getOptionalMessage("default").equals("Test Message"));
	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s"
				, "{"
				, "  \"type_hint\" : \"jimmutable.aws.servlet_utils.get.GetResponseError\","
				, "  \"message\" : \"Test Deserialization\""
				, "}");

		GetResponseError obj = (GetResponseError) StandardObject.deserialize(obj_string);
		assert (obj.getOptionalMessage(null).equals("Test Deserialization"));
	}
}
