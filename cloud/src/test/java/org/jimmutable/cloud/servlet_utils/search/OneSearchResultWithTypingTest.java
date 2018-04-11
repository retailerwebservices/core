package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;



public class OneSearchResultWithTypingTest extends StubTest
{
	@Test
	public void testEmpty()
	{
		// Test with no Map passed in. Should allow it.
		try
		{
			OneSearchResultWithTyping result = new OneSearchResultWithTyping();
		} catch ( Exception e )
		{
			fail();
		}
	}
	
	@Test
	public void testText()
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
		assertTrue(result.readAsTextArray(key2, null).length == 2);
		assertTrue(result.readAsTextArray(key2, null)[1].equals("Test Value 3"));
	}
	
	@Test
	public void testBoolean()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"false"};
		String[] value2 = new String[] {"true", "false"};
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

		assertEquals(false, result.readAsBoolean(key1, true));
		assertTrue(result.readAsBooleanArray(key2, null).length == 2);
		assertTrue(result.readAsBooleanArray(key2, null)[1] == false);
	}
	
	@Test
	public void testDay()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"2016-09-29"};
		String[] value2 = new String[] {"2016-09-29", "2016-10-21"};
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

		assertEquals(new Day(9, 29, 2016), result.readAsDay(key1, null));
		assertTrue(result.readAsDayArray(key2, null).length == 2);
		assertTrue(result.readAsDayArray(key2, null)[1].equals(new Day(10, 21, 2016)));
	}
	
	@Test
	public void testFloat()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"0.0020021796"};
		String[] value2 = new String[] {"0.0020021796", "0.00864923"};
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

		assertTrue(0.0020021796F == result.readAsFloat(key1, -1F));
		assertTrue(result.readAsFloatArray(key2, null).length == 2);
		assertTrue(result.readAsFloatArray(key2, null)[1] == 0.00864923F);
	}
	
	@Test
	public void testInstant()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"{ \"type_hint\" : \"instant\",  \"ms_from_epoch\" : 1414097896861}"};
		String[] value2 = new String[] {"{ \"type_hint\" : \"instant\",  \"ms_from_epoch\" : 1414097896861}", "{ \"type_hint\" : \"instant\",  \"ms_from_epoch\" : 1306529896861}"};
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

		assertEquals(1414097896861L, result.readAsInstant(key1, null).getSimpleMillisecondsFromEpoch());
		assertTrue(result.readAsInstantArray(key2, null).length == 2);
		assertTrue(result.readAsInstantArray(key2, null)[1].getSimpleMillisecondsFromEpoch() == 1306529896861L);
	}

	@Test
	public void testLong()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"-9201657656501495214"};
		String[] value2 = new String[] {"-9201657656501495214", "-9104549800578348988"};
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

		assertEquals(-9201657656501495214L, result.readAsLong(key1, -1L));
		assertTrue(result.readAsLongArray(key2, null).length == 2);
		assertTrue(result.readAsLongArray(key2, null)[1] == -9104549800578348988L);
	}
	
	@Test
	public void testTimeOfDay()
	{
		OneSearchResultWithTyping result = null;
		Map<FieldName, String[]> input_data = new FieldHashMap<>();
		
		FieldName key1 = new FieldName("test_key");
		FieldName key2 = new FieldName("test_key2");
		String[] value1 = new String[] {"{\"type_hint\" : \"time_of_day\", \"ms_from_midnight\" : 86266785}"};
		String[] value2 = new String[] {"{\"type_hint\" : \"time_of_day\", \"ms_from_midnight\" : 86266785}", "{\"type_hint\" : \"time_of_day\", \"ms_from_midnight\" : 86120615}"};
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

		assertEquals(86266785L, result.readAsTimeOfDay(key1, null).getSimpleMillisecondsFromMidnight());
		assertTrue(result.readAsTimeOfDayArray(key2, null).length == 2);
		assertTrue(result.readAsTimeOfDayArray(key2, null)[1].getSimpleMillisecondsFromMidnight() == 86120615L);
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
