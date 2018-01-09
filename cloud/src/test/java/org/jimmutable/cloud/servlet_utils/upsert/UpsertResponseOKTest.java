package org.jimmutable.cloud.servlet_utils.upsert;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
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
			assertTrue(false);
		}

		assertTrue(result.getSimpleHTTPResponseCode() == 200);
		assertTrue(result.getOptionalMessage(null) == null);
		
		UpsertResponseOK data_object = new UpsertResponseOK();
		result = new UpsertResponseOK("Test Message", data_object );
		assertTrue(result.getOptionalMessage(null).equals("Test Message"));
		assertTrue(result.getOptionalMessage("default").equals("Test Message"));
		assertTrue(result.getOptionalObject(null) == data_object);
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
		
		assertTrue( obj.getOptionalMessage(null).equals("Test Message") );
		UpsertResponseOK optionalObject = (UpsertResponseOK) obj.getOptionalObject(null);
		assertTrue( optionalObject.getOptionalMessage(null).equals("Nested test message") );
	}
}
