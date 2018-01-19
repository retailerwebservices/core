package org.jimmutable.core.objects.common.time;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

import java.util.Objects;

/**
 * Stores milliseconds from midnight up to 863999999
 * 
 * @author trevorbox
 *
 */
public class TimeOfDay extends StandardImmutableObject<TimeOfDay>
{

	static public final Kind KIND = new Kind("time-of-day");
	static public final TypeName TYPE_NAME = new TypeName("time_of_day");

	static public final FieldDefinition.Long FIELD_MS_FROM_MIDNIGHT = new FieldDefinition.Long("ms_from_midnight", null);

	static private final long MS_IN_SECOND = 1000L;
	static private final long MS_IN_MINUTE = 60L * MS_IN_SECOND;
	static private final long MS_IN_HOUR = 60L * MS_IN_MINUTE;
	static private final long MS_IN_DAY = 24L * MS_IN_HOUR;

	private long ms_from_midnight;// (long, required, in the range 0 - 86399999, normalize higher numbers using
									// modulus)

	public TimeOfDay(int ms_from_midnight)
	{
		this.ms_from_midnight = new Long(ms_from_midnight);
		complete();
	}

	public TimeOfDay(long ms_from_midnight)
	{
		this.ms_from_midnight = ms_from_midnight;
		complete();
	}

	public TimeOfDay(ObjectParseTree t)
	{
		this.ms_from_midnight = t.getLong(FIELD_MS_FROM_MIDNIGHT);
	}

	/**
	 * 
	 * @return The number of milliseconds form midnight
	 */
	public long getSimpleMillisecondsFromMidnight()
	{
		return ms_from_midnight;
	}

	/**
	 * 
	 * @return The number of seconds from midnight
	 */
	public long getSimpleSecondsFromMidnight()
	{

		return getSimpleMillisecondsFromMidnight() / MS_IN_SECOND;
	}

	/**
	 * 
	 * @return The number of minutes from midnight
	 */
	public long getSimpleMinutesFromMidnight()
	{
		return getSimpleMillisecondsFromMidnight() / MS_IN_MINUTE;
	}

	/**
	 * 
	 * @return The 12 hour formatted hour 1-12
	 */
	public long getSimple12hrClockHours()
	{
		long hours = getSimple24hrClockHours();

		if (hours > 12)
		{
			hours -= 12;
		}
		if (hours == 0)
		{
			return 12;
		}
		return hours;
	}

	/**
	 * return values are in the range 0 - 59
	 * 
	 * @return Minutes on the hour
	 */
	public long getSimple12hrClockMinutes()
	{
		return getSimple24hrClockMinutes();
	}

	/**
	 * return values are in the range 0 -59
	 * 
	 * @return Seconds on the minute
	 */
	public long getSimple12hrClockSeconds()
	{
		return getSimple24hrClockSeconds();
	}

	/**
	 * return values are in the range 0 - 999
	 * 
	 * @return Milliseconds on the second
	 */
	public long getSimple12hrClockMilliseconds()
	{
		return getSimple24hrClockMilliseconds();
	}

	/**
	 * Returns the 24 hour clock hour
	 * 
	 * @return the hour in the range 0 - 23
	 */
	public long getSimple24hrClockHours()
	{
		return getSimpleMillisecondsFromMidnight() / MS_IN_HOUR;
	}

	/**
	 * 
	 * @return true if AM, else false
	 */
	public boolean getSimple12hrClockAm()
	{
		return getSimple24hrClockHours() < 12;
	}

	/**
	 * 
	 * @return true if PM, else false
	 */
	public boolean getSimple12hrClockPm()
	{
		return !getSimple12hrClockAm();
	}

	/**
	 * Pretty print as 1:27 AM, etc. (NOTE it's 12:27 AM not 00:27 AM)
	 */
	public String getSimple12hrClockPrettyPrint()
	{
		return String.format("%d:%02d %s", getSimple12hrClockHours(), getSimple12hrClockMinutes(), getSimple12hrClockAm() ? "AM" : "PM");
	}

	/**
	 * 
	 * @return long in the range 0 - 59
	 */
	public long getSimple24hrClockMinutes()
	{
		return (getSimpleMillisecondsFromMidnight() - (getSimple24hrClockHours() * MS_IN_HOUR)) / MS_IN_MINUTE;
	}

	/**
	 * 
	 * @return long in the range 0 - 59
	 */
	public long getSimple24hrClockSeconds()
	{
		long hour_millis = getSimple24hrClockHours() * MS_IN_HOUR;
		long minute_millis = getSimple24hrClockMinutes() * MS_IN_MINUTE;

		return (getSimpleMillisecondsFromMidnight() - hour_millis - minute_millis) / MS_IN_SECOND;
	}

	/**
	 * return values are in the range 0 - 999
	 * 
	 * @return Milliseconds on the second
	 */
	public long getSimple24hrClockMilliseconds()
	{
		long hour_millis = getSimple24hrClockHours() * MS_IN_HOUR;
		long minute_millis = getSimple24hrClockMinutes() * MS_IN_MINUTE;
		long second_millis = getSimple24hrClockSeconds() * MS_IN_SECOND;

		return getSimpleMillisecondsFromMidnight() - hour_millis - minute_millis - second_millis;
	}

	/**
	 * 
	 * @return Pretty print as 00:23, 01:47, 23:32
	 */
	public String getSimple24hrClockPrettyPrint()
	{
		return String.format("%02d:%02d", getSimple24hrClockHours(), getSimple24hrClockMinutes());
	}

	/**
	 * Combines time units total milliseconds
	 * 
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param millis
	 * @return long
	 */
	static public long toMillis(long hours, long minutes, long seconds, long millis)
	{
		long hours_millis = hours * MS_IN_HOUR;
		long minutes_millis = minutes * MS_IN_MINUTE;
		long seconds_millis = seconds * MS_IN_SECOND;
		return hours_millis + minutes_millis + seconds_millis + millis;
	}

	/**
	 * If AM/PM is missing, assume AM</br>
	 * 
	 * Interpret 00:23 am as 12:23</br>
	 * 
	 * Error on out of range values</br>
	 * 
	 * Supports hh:mm [am/pm] hh:mm:ss [am/pm]</br>
	 * 
	 * @param str
	 *            time string as hh:mm [am/pm] or hh:mm:ss [am/pm]
	 * @param default_value
	 *            default TimeOfDay
	 * @return TimeOfDay
	 */
	static public TimeOfDay createFrom12hrClockString(String str, TimeOfDay default_value)
	{

		if (str == null)
		{
			return default_value;
		}

		str = str.trim().toLowerCase();

		try
		{
			long hours = Long.parseLong(str.substring(0, 2));

			Validator.max(hours, 12);
			Validator.min(hours, 0);

			if (hours == 12)
			{
				hours = 0;
			}

			if (str.endsWith(" pm"))
			{
				hours += 12;
				str = str.substring(0, str.indexOf(" pm"));

			}
			if (str.endsWith(" am"))
			{
				str = str.substring(0, str.indexOf(" am"));
			}

			long minutes = Long.parseLong(str.substring(3, 5));

			Validator.max(minutes, 59);
			Validator.min(minutes, 0);

			long seconds = 0;
			if (str.length() > 5)
			{
				seconds = Long.parseLong(str.substring(6, 8));
				Validator.max(seconds, 59);
				Validator.min(seconds, 0);
			}

			return new TimeOfDay(toMillis(hours, minutes, seconds, 0));
		} catch (Exception e)
		{
			return default_value;
		}
	}

	/**
	 * Supports HH:mm and HH:mm:ss formats</br>
	 * 
	 * Midnight is 00:23, whereas 12:23 is the afternoon</br>
	 * 
	 * @param str
	 *            time string in the format HH:mm or HH:mm:ss
	 * @param default_value
	 *            default TimeOfDay
	 * @return TimeOfDay
	 */
	static public TimeOfDay createFrom24hrClockString(String str, TimeOfDay default_value)
	{

		if (str == null)
		{
			return default_value;
		}

		str = str.trim().toLowerCase();

		try
		{
			long hours = Long.parseLong(str.substring(0, 2));

			Validator.max(hours, 23);
			Validator.min(hours, 0);

			long minutes = Long.parseLong(str.substring(3, 5));

			Validator.max(minutes, 59);
			Validator.min(minutes, 0);

			long seconds = 0;
			if (str.length() > 5)
			{
				seconds = Long.parseLong(str.substring(6, 8));
				Validator.max(seconds, 59);
				Validator.min(seconds, 0);
			}

			return new TimeOfDay(toMillis(hours, minutes, seconds, 0));
		} catch (Exception e)
		{
			return default_value;
		}

	}

	/**
	 * Supports HH:mm:ss.SSS
	 * 
	 * @param str
	 *            time string in the format HH:mm:ss.SSS
	 * @param default_value
	 *            default TimeOfDay
	 * @return TimeOfDay
	 */
	static public TimeOfDay createFrom24hrTimestampString(String str, TimeOfDay default_value)
	{

		if (str == null)
		{
			return default_value;
		}

		str = str.trim().toLowerCase();

		try
		{
			long hours = Long.parseLong(str.substring(0, 2));

			Validator.max(hours, 23);
			Validator.min(hours, 0);

			long minutes = Long.parseLong(str.substring(3, 5));

			Validator.max(minutes, 59);
			Validator.min(minutes, 0);

			long seconds = Long.parseLong(str.substring(6, 8));
			Validator.max(seconds, 59);
			Validator.min(seconds, 0);

			long milliseconds = Long.parseLong(str.substring(9, 12));

			Validator.max(milliseconds, 999);
			Validator.min(milliseconds, 0);

			return new TimeOfDay(toMillis(hours, minutes, seconds, milliseconds));
		} catch (Exception e)
		{
			return default_value;
		}

	}

	/**
	 * This will wrap to the appropriate long from midnight within a single day.
	 * Useful if you want to instantiate a TimeOfDay from a long outside of the
	 * number of milliseconds in a single day (0-86400000). </br>
	 * 
	 * Examples:</br>
	 * assertEquals(86399999, TimeOfDay.toDayOverlapMillis(-1));</br>
	 * assertEquals(1, TimeOfDay.toDayOverlapMillis(86400001));</br>
	 * assertEquals(0, TimeOfDay.toDayOverlapMillis(86400000 * 2));</br>
	 * 
	 * @param long_outside_of_single_day
	 *            Can be any value (negative, zero or positive)
	 * 
	 * @return The wrapped day milliseconds
	 */
	public static long toWrappedDayMillis(long long_outside_of_single_day)
	{
		long remainder = long_outside_of_single_day % MS_IN_DAY;

		if (remainder < 0)
		{
			return MS_IN_DAY - Math.abs(remainder);
		}

		return remainder;
	}

	@Override
	public int compareTo(TimeOfDay other)
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleMillisecondsFromMidnight(), other.getSimpleMillisecondsFromMidnight());
		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeLong(FIELD_MS_FROM_MIDNIGHT, getSimpleMillisecondsFromMidnight());
	}

	@Override
	public void freeze()
	{
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.notNull(ms_from_midnight);
		Validator.min(ms_from_midnight, 0);
		Validator.max(ms_from_midnight, MS_IN_DAY - 1);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(ms_from_midnight);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof TimeOfDay))
		{
			return false;
		}

		TimeOfDay other = (TimeOfDay) obj;

		return getSimpleMillisecondsFromMidnight() == other.getSimpleMillisecondsFromMidnight();
	}

	/**
	 * prints the timestamp value in HH:mm:ss.SSS
	 * 
	 * @return String
	 */
	public String toPrettyPrint()
	{
		return String.format("%02d:%02d:%02d.%03d", getSimple24hrClockHours(), getSimple24hrClockMinutes(), getSimple24hrClockSeconds(), getSimple24hrClockMilliseconds());
	}

}
