package org.jimmutable.cloud.servlet_utils.upsert;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.core.objects.StandardObject;
import org.junit.Test;

public class UpsertResponseOKTest extends StubTest
{
	
    @Test
	public void testUpsertResponseOKTest()
	{
		UpsertResponseOK result = null;
		try
		{
			result = new UpsertResponseOK();
		} catch( Exception e ) 
		{
			assert(false);
		}

		assert(result.getSimpleHTTPResponseCode() == 200);
		assert(result.getOptionalMessage(null) == null);
		
		UpsertResponseOK data_object = new UpsertResponseOK();
		result = new UpsertResponseOK("Test Message", data_object );
		assert(result.getOptionalMessage(null).equals("Test Message"));
		assert(result.getOptionalMessage("default").equals("Test Message"));
		assert(result.getOptionalObject(null) == data_object);
	}
	 
    @Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.upsert.UpsertResponseOK\","
			     , "  \"message\" : \"Test Message\" ,"
			     , "  \"object\" : "
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.upsert.UpsertResponseOK\", "
			     , "   \"message\" : \"Nested test message\" "
			     , "}"
			     , "}"
			);

		UpsertResponseOK obj = (UpsertResponseOK)StandardObject.deserialize(obj_string);
		
		assert( obj.getOptionalMessage(null).equals("Test Message") );
		UpsertResponseOK optionalObject = (UpsertResponseOK) obj.getOptionalObject(null);
		assert( optionalObject.getOptionalMessage(null).equals("Nested test message") );
	}
}
