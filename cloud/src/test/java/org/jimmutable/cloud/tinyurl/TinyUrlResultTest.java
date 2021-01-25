package org.jimmutable.cloud.tinyurl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class TinyUrlResultTest extends StubTest
{
	@Test
	public void testUserSerialization()
	{
		TinyUrlResult tiny_url_result = new TinyUrlResult(new ObjectId(123), "url", "tiny_url");
		String serialized_value = tiny_url_result.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"com.digitalpanda.objects.tinyurl.TinyUrlResult\",\n" + "  \"id\" : \"0000-0000-0000-007b\",\n" + "  \"url\" : \"url\",\n" + "  \"tiny_url\" : \"tiny_url\"\n" + "}", serialized_value);
		// System.out.println(tiny_url_result.toJavaCode(Format.JSON_PRETTY_PRINT,
		// "obj"));
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"com.digitalpanda.objects.tinyurl.TinyUrlResult\",", "  \"id\" : \"0000-0000-0000-007b\",", "  \"url\" : \"url\",", "  \"tiny_url\" : \"tiny_url\"", "}");

		TinyUrlResult obj = (TinyUrlResult) StandardObject.deserialize(obj_string);

		assertEquals(obj, tiny_url_result);
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		TinyUrlResult tiny_url_result = new TinyUrlResult(new ObjectId(123), "url", "tiny_url");
		TinyUrlResult tiny_url_result2 = new TinyUrlResult(new ObjectId(123), "url", "tiny_url");
		assertTrue(tiny_url_result.equals(tiny_url_result2));
		assertEquals(0, tiny_url_result.compareTo(tiny_url_result2));

		tiny_url_result2 = new TinyUrlResult(new ObjectId(122), "url", "tiny_url");
		assertFalse(tiny_url_result.equals(tiny_url_result2));
		assertEquals(1, tiny_url_result.compareTo(tiny_url_result2));

		tiny_url_result2 = new TinyUrlResult(new ObjectId(124), "url", "tiny_url");
		assertFalse(tiny_url_result.equals(tiny_url_result2));
		assertEquals(-1, tiny_url_result.compareTo(tiny_url_result2));
	}

	@Test(expected = ValidationException.class)
	public void validationException1()
	{
		TinyUrlResult tiny_url_result = new TinyUrlResult(null, "url", "tiny_url");
	}

	@Test(expected = ValidationException.class)
	public void validationException2()
	{
		TinyUrlResult tiny_url_result = new TinyUrlResult(new ObjectId(123), null, "tiny_url");
	}

	@Test(expected = ValidationException.class)
	public void validationException3()
	{
		TinyUrlResult tiny_url_result = new TinyUrlResult(new ObjectId(123), "url", null);
	}
}
