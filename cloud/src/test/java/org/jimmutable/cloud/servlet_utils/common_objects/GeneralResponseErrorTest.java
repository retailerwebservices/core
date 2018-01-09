package org.jimmutable.cloud.servlet_utils.common_objects;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
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
			assertTrue (false);
		}

		assertTrue (result.getSimpleHTTPResponseCode() == GeneralResponseError.HTTP_STATUS_CODE_ERROR);
		assertTrue (result.getOptionalMessage(null) == null);

		result = new GeneralResponseError("Test Message");
		assertTrue (result.getOptionalMessage(null).equals("Test Message"));
		assertTrue (result.getOptionalMessage("default").equals("Test Message"));
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
		assertTrue (obj.getOptionalMessage(null).equals("Test Deserialization"));
	}
}
