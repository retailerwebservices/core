package org.jimmutable.aws.servlet_utils.common_objects;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeneralResponseOKTest extends TestCase
{
	public GeneralResponseOKTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(GeneralResponseOK.class);
		return new TestSuite(GeneralResponseOKTest.class);
	}

	public void testGeneralResponseOK()
	{
		GeneralResponseOK result = null;
		try
		{
			result = new GeneralResponseOK();
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == 200);
		assert (result.getOptionalMessage(null) == null);

		result = new GeneralResponseOK("Test Message");
		assert (result.getOptionalMessage(null).equals("Test Message"));
		assert (result.getOptionalMessage("default").equals("Test Message"));
	}

	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s", "{",
				"  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.GeneralResponseOK\",",
				"  \"message\" : \"Test Deserialization\"", "}");

		GeneralResponseOK obj = (GeneralResponseOK) StandardObject.deserialize(obj_string);
		assert (obj.getOptionalMessage(null).equals("Test Deserialization"));
	}

}
