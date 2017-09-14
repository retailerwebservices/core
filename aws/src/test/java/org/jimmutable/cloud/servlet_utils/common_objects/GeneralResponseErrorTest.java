package org.jimmutable.cloud.servlet_utils.common_objects;

import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeneralResponseErrorTest extends TestCase
{
	public GeneralResponseErrorTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();
//		ObjectParseTree.registerTypeName(GeneralResponseError.class);
		return new TestSuite(GeneralResponseErrorTest.class);
	}

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
