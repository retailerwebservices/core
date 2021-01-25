package org.jimmutable.cloud.cache;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.junit.Test;

public class CacheEventTest extends StubTest
{

	public static final CacheEvent event;

	static
	{
		Builder b = new Builder(CacheEvent.TYPE_NAME);
		b.set(CacheEvent.FIELD_KEY, new CacheKey("h://ello"));
		b.set(CacheEvent.FIELD_TIMESTAMP, 1000000000000l);
		b.set(CacheEvent.FIELD_ACTIVITY, CacheActivity.GET);
		b.set(CacheEvent.FIELD_METRIC, CacheMetric.ADD);
		event = b.create();
	}

	@Test
	public void testValid()
	{
		// Uncomment when you have this on your box. Time zones are a pain -AWT
		// assertEquals("2001-09-251 18:09:00",
		// event.getSimpleTimestampHumanReadable());
	}

	@Test
	public void required()
	{

		// System.out.println(event.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"cache_event\",", "  \"activity\" : \"get\",", "  \"metric\" : \"add\",", "  \"key\" : \"h://ello\",", "  \"timestamp\" : 1000000000000", "}");

		CacheEvent obj = (CacheEvent) StandardObject.deserialize(obj_string);

		assertEquals(obj, event);

	}
}
