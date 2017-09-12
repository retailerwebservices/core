package org.jimmutable.aws.servlet_utils.search;

import java.util.Map;

import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OneSearchResultTest extends TestCase
{

	public OneSearchResultTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		return new TestSuite(OneSearchResultTest.class);
	}

	public void testOneSearchResultTest()
	{
		OneSearchResult result = null;
		Map<FieldName, String> input_data = new FieldHashMap<>();
		input_data.put(new FieldName("test_key"), "Test Value");
		input_data.put(new FieldName("test_key2"), "Test Value2");

		try
		{
			result = new OneSearchResult(input_data);
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleContents().size() == 2);

		// Test with no Map passed in. Should allow it.
		try
		{
			result = new OneSearchResult();
		} catch ( Exception e )
		{
			assert (false);
		}
	}

	public void testSerialization()
	{
		 String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.search.OneSearchResult\","
			     , "  \"result\" : [ {"
			     , "    \"type_hint\" : \"MapEntry\","
			     , "    \"key\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"test_key2\""
			     , "    },"
			     , "    \"value\" : \"Test Value2\""
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
		 
		OneSearchResult obj = (OneSearchResult) StandardObject.deserialize(obj_string);

		assertEquals(obj.getSimpleContents().size(), 2);
		assert (obj.getSimpleContents().containsValue("Test Value2"));
		assert (obj.getSimpleContents().containsValue("Test Value"));
		assert (obj.getSimpleContents().containsKey(new FieldName("test_key")));
		assert (obj.getSimpleContents().containsKey(new FieldName("test_key2")));

	}
}
