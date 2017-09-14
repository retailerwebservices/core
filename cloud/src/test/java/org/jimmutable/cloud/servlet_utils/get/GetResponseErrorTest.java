package org.jimmutable.cloud.servlet_utils.get;

import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GetResponseErrorTest extends TestCase
{
	public GetResponseErrorTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(GetResponseError.class);
		return new TestSuite(GetResponseErrorTest.class);
	}

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
