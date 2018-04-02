package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;



public class OneSearchResultWithTypingTest extends StubTest
{
	
	
	@Test
	public void testOneSearchResultWithTyping()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"Test Value"};
		String[] value2 = new String[] {"Test Value 2", "Test Value 3"};
		input_data.put(key1, value1);
		input_data.put(key2, value2);

		try
		{
			result = new OneSearchResultWithTyping(input_data);
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			fail();
		}

		assertEquals("Test Value", result.readAsText(key1, null));

		// Test with no Map passed in. Should allow it.
		try
		{
			result = new OneSearchResultWithTyping();
		} catch ( Exception e )
		{
			fail();
		}
	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.OneSearchResultWithTyping\","
			     , "  \"result\" : [ {"
			     , "    \"type_hint\" : \"MapEntry\","
			     , "    \"key\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"test_key2\""
			     , "    },"
			     , "    \"value\" : \"Test Value 2\","
			     , "    \"value\" : \"Test Value 3\""
			     , "  }, {"
			     , "    \"type_hint\" : \"MapEntry\","
			     , "    \"key\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"test_key\""
			     , "    },"
			     , "    \"value\" : \"Test Value\""
			     , "  } ]"
			     , "}"
			);
		 
		OneSearchResultWithTyping obj = (OneSearchResultWithTyping) StandardObject.deserialize(obj_string);

		assertEquals("Test Value 2", obj.readAsText(new FieldName("test_key2"), null));
	}
}
