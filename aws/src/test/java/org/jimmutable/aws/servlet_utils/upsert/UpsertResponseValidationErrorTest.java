package org.jimmutable.aws.servlet_utils.upsert;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UpsertResponseValidationErrorTest extends TestCase
{

	public UpsertResponseValidationErrorTest(String testName)
	{
		super(testName);
	}
	
    public static Test suite()
    {
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(UpsertResponseValidationError.class);
        return new TestSuite( UpsertResponseValidationError.class );
    }
    
	public void testUpsertResponseValidationError()
	{
		UpsertResponseValidationError result = null;
		try
		{
			result = new UpsertResponseValidationError();

		} catch(Exception e) {
			assert(false);
		}

		assert(result.getSimpleHTTPResponseCode() == 500);
		assert(result.getOptionalErrorMessage(null) == null);
		
		result = new UpsertResponseValidationError("Test Message", "TestFieldName");
		assert(result.getOptionalErrorMessage(null).equals("Test Message"));
	}

	public void testBuilder()
	{
		Builder builder = new Builder(UpsertResponseValidationError.TYPE_NAME);
		
		try
		{
			builder.create(null);
		}
		catch(Exception e)
		{
			assert(false);
		}
		
		builder.set(UpsertResponseValidationError.FIELD_ERROR_MESSAGE, "Test Error Message");
		builder.set(UpsertResponseValidationError.FIELD_NAME, "TestFieldName");
		builder.create(null);
		
	}
	 
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.common_objects.UpsertResponseValidationError\","
			     , "  \"error_message\" : \"Test Deserialization\""
			     , "}"
			);

		UpsertResponseValidationError obj = (UpsertResponseValidationError)StandardObject.deserialize(obj_string);
		
		assert(obj.getOptionalErrorMessage(null).equals("Test Deserialization"));
	}
}
