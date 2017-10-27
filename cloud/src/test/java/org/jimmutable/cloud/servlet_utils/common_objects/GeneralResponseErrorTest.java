package org.jimmutable.cloud.servlet_utils.common_objects;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.core.objects.StandardObject;
import org.junit.Test;

public class GeneralResponseErrorTest extends StubTest
{

	@Test
	public void testGeneralResponseError()
	{
		GeneralResponseError result = null;
		try
		{
			result = new GeneralResponseError();
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == GeneralResponseError.HTTP_STATUS_CODE_ERROR);
		assert (result.getOptionalMessage(null) == null);

		result = new GeneralResponseError("Test Message");
		assert (result.getOptionalMessage(null).equals("Test Message"));
		assert (result.getOptionalMessage("default").equals("Test Message"));
	}
	
	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s"
				, "{"
				, "  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.GeneralResponseError\","
				, "  \"message\" : \"Test Deserialization\""
				, "}"
				);

		GeneralResponseError obj = (GeneralResponseError) StandardObject.deserialize(obj_string);
		assert (obj.getOptionalMessage(null).equals("Test Deserialization"));
	}
}
