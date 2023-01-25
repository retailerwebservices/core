package org.jimmutable.core.objects.common.time;

import static org.junit.Assert.assertEquals;

import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.junit.BeforeClass;
import org.junit.Test;

public class InstantTest
{

	@BeforeClass
	public static void setup()
	{
		JimmutableTypeNameRegister.registerAllTypes();
	}

	@Test
	public void testDay()
	{
		Day day = new Day(1, 24, 1990);
		TimeOfDay time = new TimeOfDay(TimeOfDay.toMillis(13, 30, 1, 967));
		assertEquals(day, new Instant(Instant.toMillisecondsFromEpoch(day, time, new TimezoneID("US/Arizona"), 0)).toDay(new TimezoneID("US/Arizona")));
	}

	@Test
	public void testEpochSeconds()
	{
		assertEquals(1516057366l, new Instant(1516057366987l).getSimpleSecondsFromEpoch());
	}

	@Test
	public void testTimeAndDay()
	{

		Day day = new Day(1, 24, 1990);
		TimeOfDay time = new TimeOfDay(TimeOfDay.toMillis(13, 30, 1, 967));
		assertEquals(time, new Instant(Instant.toMillisecondsFromEpoch(day, time, new TimezoneID("US/Arizona"), 0)).toTimeOfDay(new TimezoneID("US/Arizona")));

		time = new TimeOfDay(TimeOfDay.toMillis(23, 20, 1, 29));
		assertEquals(time, new Instant(Instant.toMillisecondsFromEpoch(day, time, new TimezoneID("US/Arizona"), 0)).toTimeOfDay(new TimezoneID("US/Arizona")));

		// System.out.println(time.toPrettyPrint());
		// System.out.println(new TimeOfDay(4801029).toPrettyPrint());

		assertEquals(new TimeOfDay(TimeOfDay.toMillis(1, 20, 1, 29)), new Instant(Instant.toMillisecondsFromEpoch(day, time, new TimezoneID("US/Arizona"), 0)).toTimeOfDay(new TimezoneID("US/Eastern")));
		assertEquals(new Day(1, 25, 1990), new Instant(Instant.toMillisecondsFromEpoch(day, time, new TimezoneID("US/Arizona"), 0)).toDay(new TimezoneID("US/Eastern")));

		// instant obtained from System.currentTimeMillis()
		long instant = 1516057366987l;
		// System.out.println(instant);
		// System.out.println(new Instant(instant).toTimeOfDay(new
		// TimezoneID("US/Arizona")).toPrettyPrint());

		assertEquals(new TimeOfDay(TimeOfDay.toMillis(16, 2, 46, 987)), new Instant(instant).toTimeOfDay(new TimezoneID("US/Arizona")));

		assertEquals(new TimeOfDay(TimeOfDay.toMillis(18, 2, 46, 987)), new Instant(instant).toTimeOfDay(new TimezoneID("US/Eastern")));

	}

	@Test
	public void serialize()
	{

		JimmutableBuilder b = new JimmutableBuilder(Instant.TYPE_NAME);
		b.set(Instant.FIELD_MS_FROM_EPOCH, 1516057366987l);
		Instant instant = (Instant) b.create();

		// System.out.println(instant.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"instant\",", "  \"ms_from_epoch\" : 1516057366987", "}");

		Instant obj = (Instant) StandardObject.deserialize(obj_string);
		assertEquals(instant, obj);
	}

}
