package org.jimmutable.cloud.servlet_utils.upsert;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
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
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == UpsertResponseValidationError.HTTP_STATUS_CODE_ERROR);
		assert (result.getOptionalErrorMessage(null) == null);

		result = new UpsertResponseValidationError("Test Message", "TestFieldName");
		assert (result.getOptionalErrorMessage(null).equals("Test Message"));
		assert (result.getOptionalErrorMessage("default").equals("Test Message"));
		assert (result.getOptionalFieldName(null).equals("TestFieldName"));
		assert (result.getOptionalFieldName("default").equals("TestFieldName"));

	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s", "{",
				"  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.UpsertResponseValidationError\",",
				"  \"error_message\" : \"Test Deserialization\"", "}");

		UpsertResponseValidationError obj = (UpsertResponseValidationError) StandardObject.deserialize(obj_string);
		assert (obj.getOptionalErrorMessage(null).equals("Test Deserialization"));
	}
}
