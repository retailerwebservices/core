package org.jimmutable.core.objects.common.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.junit.BeforeClass;
import org.junit.Test;

public class TimeOfDayTest
{

	@BeforeClass
	public static void setup()
	{
		JimmutableTypeNameRegister.registerAllTypes();
	}

	@Test
	public void toWrappedMillis()
	{
		assertEquals(TimeOfDay.toMillis(14, 0, 0, 0), TimeOfDay.toWrappedDayMillis(-1 * TimeOfDay.toMillis(10, 0, 0, 0)));
		assertEquals(1, TimeOfDay.toWrappedDayMillis(86400001));
		assertEquals(0, TimeOfDay.toWrappedDayMillis(86400000 * 4));
		assertEquals(86399999, TimeOfDay.toWrappedDayMillis(-1));
	}

	@Test
	public void test24Hour()
	{
		assertEquals(14, new TimeOfDay(TimeOfDay.toMillis(14, 30, 0, 986)).getSimple24hrClockHours());
		assertEquals(23, new TimeOfDay(TimeOfDay.toMillis(23, 59, 59, 999)).getSimple24hrClockHours());
	}

	@Test
	public void test12Hour()
	{

		assertEquals(12, new TimeOfDay(TimeOfDay.toMillis(0, 0, 0, 0)).getSimple12hrClockHours());
		assertEquals(12, new TimeOfDay(TimeOfDay.toMillis(0, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(12, new TimeOfDay(TimeOfDay.toMillis(24, 00, 0, 0)).getSimple12hrClockHours());

		assertEquals(1, new TimeOfDay(TimeOfDay.toMillis(1, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(2, new TimeOfDay(TimeOfDay.toMillis(2, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(3, new TimeOfDay(TimeOfDay.toMillis(3, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(4, new TimeOfDay(TimeOfDay.toMillis(4, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(5, new TimeOfDay(TimeOfDay.toMillis(5, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(6, new TimeOfDay(TimeOfDay.toMillis(6, 25, 0, 0)).getSimple12hrClockHours());

		assertEquals(12, new TimeOfDay(TimeOfDay.toMillis(12, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(1, new TimeOfDay(TimeOfDay.toMillis(13, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(2, new TimeOfDay(TimeOfDay.toMillis(14, 25, 0, 0)).getSimple12hrClockHours());
		assertEquals(3, new TimeOfDay(TimeOfDay.toMillis(15, 25, 0, 0)).getSimple12hrClockHours());

	}

	@Test
	public void test12HourMinutes()
	{
		assertEquals(25, new TimeOfDay(TimeOfDay.toMillis(12, 25, 0, 0)).getSimple12hrClockMinutes());
		assertEquals(59, new TimeOfDay(TimeOfDay.toMillis(13, 59, 0, 0)).getSimple12hrClockMinutes());
		assertEquals(0, new TimeOfDay(TimeOfDay.toMillis(24, 0, 0, 0)).getSimple12hrClockMinutes());
		assertEquals(0, new TimeOfDay(TimeOfDay.toMillis(24, 60, 0, 0)).getSimple12hrClockMinutes());
	}

	@Test
	public void test12HourSeconds()
	{
		assertEquals(21, new TimeOfDay(TimeOfDay.toMillis(12, 00, 21, 0)).getSimple12hrClockSeconds());
		assertEquals(21, new TimeOfDay(TimeOfDay.toMillis(12, 00, 21, 999)).getSimple12hrClockSeconds());
		assertEquals(22, new TimeOfDay(TimeOfDay.toMillis(12, 00, 21, 1000)).getSimple12hrClockSeconds());
		assertEquals(59, new TimeOfDay(TimeOfDay.toMillis(12, 00, 59, 999)).getSimple12hrClockSeconds());
		assertEquals(0, new TimeOfDay(TimeOfDay.toMillis(12, 00, 59, 1000)).getSimple12hrClockSeconds());
		assertEquals(0, new TimeOfDay(TimeOfDay.toMillis(12, 00, 60, 0)).getSimple12hrClockSeconds());
		assertEquals(1, new TimeOfDay(TimeOfDay.toMillis(12, 00, 1, 0)).getSimple12hrClockSeconds());
		assertEquals(59, new TimeOfDay(TimeOfDay.toMillis(23, 59, 59, 999)).getSimple12hrClockSeconds());
	}

	@Test
	public void testPrettyPrint()
	{
		assertEquals("23:59:59.999", new TimeOfDay(TimeOfDay.toMillis(23, 59, 59, 999)).toPrettyPrint());
		assertEquals("01:59:00.017", new TimeOfDay(TimeOfDay.toMillis(1, 59, 0, 17)).toPrettyPrint());
		assertEquals("00:00:00.001", new TimeOfDay(TimeOfDay.toMillis(0, 0, 0, 1)).toPrettyPrint());
	}

	@Test
	public void createFrom12hrClockStringTest()
	{
		assertEquals("12:10 AM", TimeOfDay.createFrom12hrClockString("0:10 am", null).getSimple12hrClockPrettyPrint());
		assertEquals("12:10 AM", TimeOfDay.createFrom12hrClockString("0:10", null).getSimple12hrClockPrettyPrint());
		assertEquals("1:10 AM", TimeOfDay.createFrom12hrClockString("1:10", null).getSimple12hrClockPrettyPrint());
		assertEquals("12:10 AM", TimeOfDay.createFrom12hrClockString("00:10 am", null).getSimple12hrClockPrettyPrint());

		assertEquals("1:10 AM", TimeOfDay.createFrom12hrClockString("1:10 am", null).getSimple12hrClockPrettyPrint());
		assertEquals("12:00 AM", TimeOfDay.createFrom12hrClockString("12:00 am", null).getSimple12hrClockPrettyPrint());
		assertEquals("12:01 AM", TimeOfDay.createFrom12hrClockString("12:01 am", null).getSimple12hrClockPrettyPrint());
		assertEquals("1:01 PM", TimeOfDay.createFrom12hrClockString("01:01 pm", null).getSimple12hrClockPrettyPrint());
		assertEquals("1:01 PM", TimeOfDay.createFrom12hrClockString("1:01 pm", null).getSimple12hrClockPrettyPrint());
		assertEquals("12:02 PM", TimeOfDay.createFrom12hrClockString("12:02 pm", null).getSimple12hrClockPrettyPrint());
		assertEquals("2:00 PM", TimeOfDay.createFrom12hrClockString("02:00 pm", null).getSimple12hrClockPrettyPrint());

		assertEquals("14:00:30.000", TimeOfDay.createFrom12hrClockString("02:00:30 pm", null).toPrettyPrint());
		assertEquals("02:01:30.000", TimeOfDay.createFrom12hrClockString("02:01:30 am", null).toPrettyPrint());

		assertNull(TimeOfDay.createFrom12hrClockString("02:00 p", null));
		assertNull(TimeOfDay.createFrom12hrClockString("13:00 am", null));
		assertNull(TimeOfDay.createFrom12hrClockString("12:60 am", null));
	}

	@Test
	public void createFrom24hrClockStringTest()
	{
		assertEquals("00:10", TimeOfDay.createFrom24hrClockString("00:10", null).getSimple24hrClockPrettyPrint());

		assertEquals("23:01", TimeOfDay.createFrom24hrClockString("23:01", null).getSimple24hrClockPrettyPrint());

		assertEquals("00:10:01.000", TimeOfDay.createFrom24hrClockString("00:10:01", null).toPrettyPrint());

		assertEquals("23:01:59.000", TimeOfDay.createFrom24hrClockString("23:01:59", null).toPrettyPrint());

		assertNull(TimeOfDay.createFrom24hrClockString("02:60", null));
		assertNull(TimeOfDay.createFrom24hrClockString("2:00", null));
	}

	@Test
	public void serialize()
	{
		Builder b = new Builder(TimeOfDay.TYPE_NAME);
		b.set(TimeOfDay.FIELD_MS_FROM_MIDNIGHT, 82860000);
		TimeOfDay tod = (TimeOfDay) b.create(null);

		//System.out.println(tod.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"time_of_day\","
			     , "  \"ms_from_midnight\" : 82860000"
			     , "}"
			);

		TimeOfDay obj = (TimeOfDay) StandardObject.deserialize(obj_string);
		assertEquals(tod, obj);
	}
	
}
