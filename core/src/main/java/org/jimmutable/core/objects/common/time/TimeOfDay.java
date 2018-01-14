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
 * Stores milliseconds from midnight
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
	}

	public TimeOfDay(long ms_from_midnight)
	{
		this.ms_from_midnight = ms_from_midnight;
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
	 * @return
	 */
	public long getSimple12hrClockMinutes()
	{
		return getSimple24hrClockMinutes();
	}

	/**
	 * return values are in the range 0 -59
	 * 
	 * @return
	 */
	public long getSimple12hrClockSeconds()
	{
		return getSimple24hrClockSeconds();
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
	 * If AM/PM is missing, assume AM
	 * 
	 * Interpret 00:23 am as 12:23
	 * 
	 * Error on out of range values
	 * 
	 * Support hh:mm [am/pm] hh:mm:ss [am/pm] h:mm [am/pm] h:mm:ss [am/pm]
	 * 
	 * @param str
	 *            time string
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

		if (str.indexOf(":") == 1)
		{
			str = "0" + str;
		}

		if (str.indexOf(" ") < 0)
		{
			str = str + " am";
		}

		if (str.matches("[01][0-12]:[0-5][0-9] am"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			hours = (hours == 12) ? 0 : hours;
			long minutes = Long.parseLong(str.substring(3, 5));
			return new TimeOfDay(toMillis(hours, minutes, 0, 0));
		} else if (str.matches("[01][0-12]:[0-5][0-9] pm"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			hours = (hours == 12) ? 0 : hours;
			hours += 12;
			long minutes = Long.parseLong(str.substring(3, 5));
			return new TimeOfDay(toMillis(hours, minutes, 0, 0));
		} else if (str.matches("[01][0-12]:[0-5][0-9]:[0-5][0-9] am"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			hours = (hours == 12) ? 0 : hours;
			long minutes = Long.parseLong(str.substring(3, 5));
			long seconds = Long.parseLong(str.substring(6, 8));
			return new TimeOfDay(toMillis(hours, minutes, seconds, 0));
		} else if (str.matches("[01][0-12]:[0-5][0-9]:[0-5][0-9] pm"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			hours = (hours == 12) ? 0 : hours;
			hours += 12;
			long minutes = Long.parseLong(str.substring(3, 5));
			long seconds = Long.parseLong(str.substring(6, 8));
			return new TimeOfDay(toMillis(hours, minutes, seconds, 0));
		} else
		{
			return default_value;
		}
	}

	/**
	 * Support hh:mm and hh:mm:ss formats
	 * 
	 * Midnight is 00:23, whereas 12:23 is the afternoon
	 * 
	 * @param str
	 *            time string
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

		str = str.trim();

		if (str.matches("[0-2][0-3]:[0-5][0-9]"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			long minutes = Long.parseLong(str.substring(3, 5));
			return new TimeOfDay(toMillis(hours, minutes, 0, 0));
		} else if (str.matches("[0-2][0-3]:[0-5][0-9]:[0-5][0-9]"))
		{
			long hours = Long.parseLong(str.substring(0, 2));
			long minutes = Long.parseLong(str.substring(3, 5));
			long seconds = Long.parseLong(str.substring(6, 8));
			return new TimeOfDay(toMillis(hours, minutes, seconds, 0));
		} else
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
		Validator.max(ms_from_midnight, 86399999);
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
		long hours_ms = getSimple24hrClockHours() * MS_IN_HOUR;
		long minutes_ms = getSimple24hrClockMinutes() * MS_IN_MINUTE;
		long seconds_ms = getSimple24hrClockSeconds() * MS_IN_SECOND;
		long ms = getSimpleMillisecondsFromMidnight() - (hours_ms + minutes_ms + seconds_ms);
		return String.format("%02d:%02d:%02d.%03d", getSimple24hrClockHours(), getSimple24hrClockMinutes(), getSimple24hrClockSeconds(), ms);
	}

}
