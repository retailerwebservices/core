package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.junit.Test;

public class OneSearchResultTest extends StubTest
{

	@Test
	public void testOneSearchResultTest()
	{
		OneSearchResult result = null;
		Map<FieldName, String> input_data = new FieldHashMap<>();
		input_data.put(new FieldName("test_key"), "Test Value");
		input_data.put(new FieldName("test_key2"), "Test Value2");

		try
		{
			result = new OneSearchResult(input_data);
			// System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		}
		catch ( Exception e )
		{
			fail();
		}

		assertTrue(result.getSimpleContents().size() == 2);

		// Test with no Map passed in. Should allow it.
		try
		{
			result = new OneSearchResult();
		}
		catch ( Exception e )
		{
			fail();
		}
	}

	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.OneSearchResult\",", "  \"result\" : [ {", "    \"type_hint\" : \"MapEntry\",", "    \"key\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"test_key2\"", "    },", "    \"value\" : \"Test Value2\"", "  }, {", "    \"type_hint\" : \"MapEntry\",", "    \"key\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"test_key\"", "    },", "    \"value\" : \"Test Value\"", "  } ]", "}");

		OneSearchResult obj = (OneSearchResult) StandardObject.deserialize(obj_string);

		assertEquals(obj.getSimpleContents().size(), 2);
		assertTrue(obj.getSimpleContents().containsValue("Test Value2"));
		assertTrue(obj.getSimpleContents().containsValue("Test Value"));
		assertTrue(obj.getSimpleContents().containsKey(new FieldName("test_key")));
		assertTrue(obj.getSimpleContents().containsKey(new FieldName("test_key2")));

	}
}
