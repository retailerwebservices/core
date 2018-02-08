package org.jimmutable.core.objects.common.time;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

public class InstantDetailsTest
{
	@BeforeClass
	public static void register()
	{
		JimmutableTypeNameRegister.registerAllTypes();
	}

	@Test
	public void builder()
	{

		Instant i = new Instant(0);

		Builder b = new Builder(InstantDetails.TYPE_NAME);
		b.set(InstantDetails.FIELD_SUCCESS, true);
		b.set(InstantDetails.FIELD_MS_FROM_EPOCH, i.getSimpleMillisecondsFromEpoch());

		TimezoneID timezone_id = new TimezoneID("US/Arizona");

		Set<String> us_timezones = new HashSet<String>();
		DateTimeZone.getAvailableIDs().forEach(timezone ->
		{
			if (timezone.startsWith("US/"))
			{
				us_timezones.add(timezone);
			}
		});

		System.out.println(us_timezones);

		b.set(InstantDetails.FIELD_TIMEZONE, timezone_id);
		b.set(InstantDetails.FIELD_DAY, i.toDay(timezone_id));
		b.set(InstantDetails.FIELD_DAY_YEAR, i.toDay(timezone_id).getSimpleYear());
		b.set(InstantDetails.FIELD_DAY_MONTH, i.toDay(timezone_id).getSimpleMonthOfYear());
		b.set(InstantDetails.FIELD_DAY_DAY, i.toDay(timezone_id).getSimpleDayOfMonth());

		System.out.println(i.toTimeOfDay(timezone_id).toPrettyPrint());

		b.set(InstantDetails.FIELD_HOURS_ON_TWELVE_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple12hrClockHours());
		b.set(InstantDetails.FIELD_MINUTES_ON_TWELVE_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple12hrClockMinutes());
		b.set(InstantDetails.FIELD_SECONDS_ON_TWELVE_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple12hrClockSeconds());

		b.set(InstantDetails.FIELD_IS_AM_ON_TWELVE_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple12hrClockAm());

		b.set(InstantDetails.FIELD_HOURS_ON_TWENTY_FOUR_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple24hrClockHours());
		b.set(InstantDetails.FIELD_MINUTES_ON_TWENTY_FOUR_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple24hrClockMinutes());
		b.set(InstantDetails.FIELD_SECONDS_ON_TWENTY_FOUR_HOUR_CLOCK, i.toTimeOfDay(timezone_id).getSimple24hrClockSeconds());
		b.set(InstantDetails.FIELD_TIMESTAMP, i.createTimestampString(timezone_id, true, null));

		String json = "{\n" + 
				"  \"type_hint\" : \"instant_details\",\n" + 
				"  \"success\" : true,\n" + 
				"  \"ms_from_epoch\" : 0,\n" + 
				"  \"timezone_id\" : \"US/Arizona\",\n" + 
				"  \"day\" : \"12/31/1969\",\n" + 
				"  \"day_year\" : 1969,\n" + 
				"  \"day_month\" : 12,\n" + 
				"  \"day_day\" : 31,\n" + 
				"  \"hours_on_twelve_hour_clock\" : 5,\n" + 
				"  \"minutes_on_twelve_hour_clock\" : 0,\n" + 
				"  \"seconds_on_twelve_hour_clock\" : 0,\n" + 
				"  \"is_am_on_twelve_hour_clock\" : false,\n" + 
				"  \"hours_on_twenty_four_hour_clock\" : 17,\n" + 
				"  \"minutes_on_twenty_four_hour_clock\" : 0,\n" + 
				"  \"seconds_on_twenty_four_hour_clock\" : 0,\n" + 
				"  \"timestamp\" : \"12/31/1969 5:00 PM US/Arizona\"\n" + 
				"}";
		
		assertEquals(json, ObjectWriter.serialize(Format.JSON_PRETTY_PRINT, (InstantDetails) b.create(null)));
	}

}
