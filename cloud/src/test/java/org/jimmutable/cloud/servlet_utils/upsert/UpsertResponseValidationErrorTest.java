package org.jimmutable.cloud.servlet_utils.upsert;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.StandardObject;
import org.junit.Test;


public class UpsertResponseValidationErrorTest extends StubTest
{
	
	
	@Test
	public void testUpsertResponseValidationError()
	{
		UpsertResponseValidationError result = null;
		try
		{
			result = new UpsertResponseValidationError();
		} catch ( Exception e )
		{
			assertTrue (false);
		}

		assertTrue (result.getSimpleHTTPResponseCode() == UpsertResponseValidationError.HTTP_STATUS_CODE_ERROR);
		assertTrue (result.getOptionalErrorMessage(null) == null);

		result = new UpsertResponseValidationError("Test Message", "TestFieldName");
		assertTrue (result.getOptionalErrorMessage(null).equals("Test Message"));
		assertTrue (result.getOptionalErrorMessage("default").equals("Test Message"));
		assertTrue (result.getOptionalFieldName(null).equals("TestFieldName"));
		assertTrue (result.getOptionalFieldName("default").equals("TestFieldName"));

	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s", "{",
				"  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.UpsertResponseValidationError\",",
				"  \"error_message\" : \"Test Deserialization\"", "}");

		UpsertResponseValidationError obj = (UpsertResponseValidationError) StandardObject.deserialize(obj_string);
		assertTrue (obj.getOptionalErrorMessage(null).equals("Test Deserialization"));
	}
}
