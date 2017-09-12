package org.jimmutable.cloud.servlet_utils.upsert;

import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UpsertResponseValidationErrorTest extends TestCase
{
	public UpsertResponseValidationErrorTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(UpsertResponseValidationError.class);
		return new TestSuite(UpsertResponseValidationErrorTest.class);
	}

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

	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s", "{",
				"  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.UpsertResponseValidationError\",",
				"  \"error_message\" : \"Test Deserialization\"", "}");

		UpsertResponseValidationError obj = (UpsertResponseValidationError) StandardObject.deserialize(obj_string);
		assert (obj.getOptionalErrorMessage(null).equals("Test Deserialization"));
	}
}
