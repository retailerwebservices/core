package org.jimmutable.core.objects.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;


/**
 * An easy to use abstraction for working with a day (e.g. 12/25/2017)
 * 
 * As with everything date/time related, days get terribly complicated, terribly
 * quickly. For basic business purposes Day makes certain assumptions that,
 * generally, work pretty well.
 * 
 * General rule of thumb: If you are doing something complciated with time,
 * DON'T USE THIS CLASS. If, on the other hand, you are working with days in the
 * US in a business context then Day is a good place to start.
 * 
 * @author kanej
 *
 */
public class Day extends Stringable
{
	static private DateTimeZone US_EASTERN = DateTimeZone.forID("US/Eastern");

	static public final MyConverter CONVERTER = new MyConverter();
	
	private DateTime mid_day_est;

	/**
	 * Construct a Day object from a string formated as mm/dd/yyyy (e.g. 2/8/1981)
	 * 
	 * Note, impossible dates (e.g. 2/31/2017) will cause a ValidationException to
	 * be thrown
	 * 
	 * @param mm_dd_yyyy
	 *            A date, formated as mm/dd/yyyy (e.g. 2/8/1981)
	 */
	public Day(String mm_dd_yyyy)
	{
		super(mm_dd_yyyy);
	}

	/**
	 * Construct a Day object
	 * 
	 * Note, impossible dates (e.g. 2/31/2017) will cause a ValidationException to
	 * be thrown
	 * 
	 * @param month
	 *            The month of the date (e.g. 2 for February)
	 * @param day
	 *            The day of the month (e.g. 8th)
	 * @param year
	 *            The year (e.g. 2017)
	 */
	public Day(int month, int day, int year)
	{
		super(String.format("%d/%d/%d", month, day, year));
	}

	/**
	 * Construct a day from a Joda Time DateTime object
	 * 
	 * @param date_time
	 *            The day as a Joda Time DateTime object
	 */
	public Day(DateTime date_time)
	{
		this(date_time.getMonthOfYear(), date_time.getDayOfMonth(), date_time.getYear());
	}

	
	public void normalize() 
	{
		normalizeTrim();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.NUMBERS, Validator.FORWARD_SLASH);
		
		String parts[] = getSimpleValue().split("/");
		
		if ( parts.length != 3 ) 
			throw new ValidationException("Invalid day format, must be mm/dd/yyyy");
		
		if ( parts[2].length() != 4 )
			throw new ValidationException("Invalid day format, year must be 4 digits, e.g. 1972, *not* 72");
		
		try
		{
			int month = Integer.parseInt(parts[0]);
			int day = Integer.parseInt(parts[1]);
			int year = Integer.parseInt(parts[2]);
			
			mid_day_est = new DateTime(year, month, day, 12, 0, 0, US_EASTERN);
		}
		catch(Exception e)
		{
			throw new ValidationException("Invalid day format, must be mm/dd/yyyy (and be a valid date)"); 
		}
	}
	
	public int getSimpleYear() { return mid_day_est.getYear(); }
	public int getSimpleDayOfMonth() { return mid_day_est.getDayOfMonth(); }
	public int getSimpleMonthOfYear() { return mid_day_est.getMonthOfYear(); }

	/**
	 * Get the month name (e.g. February)
	 * 
	 * @return The full name of the month (e.g. January, November)
	 */
	public String getSimpleMonthName() { return mid_day_est.toString("MMMM", Locale.US); }
	
	/**
	 * Get the three letter name of the month, e.g. Feb
	 * 
	 * @return The three letter abbreviation of the month (e.g. Jan, Feb, ...)
	 */
	public String getSimpleThreeLetterMonthName() { return mid_day_est.toString("MMM", Locale.US); }
	
	/**
	 * Get the day name (e.g. Sunday)
	 * 
	 * @return The full name of the day (e.g. Monday, Tuesday)
	 */
	
	public String getSimpleDayName() {  return mid_day_est.toString("EEEE", Locale.US); }

	/**
	 * Get the three letter name of the day, e.g. Mon, Tue
	 * 
	 * @return The three letter abbreviation of the month (e.g. Mon, Tue, ...)
	 */
	
	public String getSimpleThreeLetterDayName() {  return mid_day_est.toString("EEE", Locale.US); }
	
	public java.util.Date createSimpleDate()
	{
		return mid_day_est.toDate();
	}
	
	public java.util.Date createSimpleDate(DateTimeZone time_zone)
	{
		return createSimpleJodaTimeDateTime(time_zone).toDate();
	}
	
	public Day createSimpleAddDays(int number_of_days_to_add)
	{
		return new Day(mid_day_est.withFieldAdded(DurationFieldType.days(), number_of_days_to_add));
	}
	
	public Day createSimpleAddMonths(int number_of_months_to_add)
	{
		return new Day(mid_day_est.withFieldAdded(DurationFieldType.months(), number_of_months_to_add));
	}
	
	public Day createSimpleAddYears(int number_of_years_to_add)
	{
		return new Day(mid_day_est.withFieldAdded(DurationFieldType.years(), number_of_years_to_add));
	}
	
	public int compareTo(Stringable o) 
	{
		if ( o instanceof Day )
		{
			Day other = (Day)o;
			
			return getSimpleJodaTimeDateTime().compareTo(other.getSimpleJodaTimeDateTime());
		}
		
		return super.compareTo(o);
	}

	
	public Day createSimpleTomorrow() { return createSimpleAddDays(1); }
	public Day createSimpleYesterday() { return createSimpleAddDays(-1); }
	
	public DateTime getSimpleJodaTimeDateTime() { return mid_day_est; }
	
	public DateTime createSimpleJodaTimeDateTime(DateTimeZone time_zone) 
	{ 
		Validator.notNull(time_zone);
		return new DateTime(mid_day_est.getYear(), mid_day_est.getMonthOfYear(), mid_day_est.getDayOfMonth(), 12, 0, 0, time_zone); 
	}
	
	static public class MyConverter extends Stringable.Converter<Day>
	{
		public Day fromString(String str, Day default_value)
		{
			try
			{
				return new Day(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	/**
	 * Returns the number of days that seperate two days. This is always positive.
	 * The same day will return zero. Tomorrow will return 1
	 * 
	 * @param other_day
	 *            The other day
	 * @return The number of days between two days
	 */
	public int getSimpleDaysBetween(Day other_day)
	{
		Validator.notNull(other_day);
		return Math.abs(Days.daysBetween(mid_day_est, other_day.mid_day_est).getDays());
	}
	
	public boolean isBefore(Day other_day)
	{
		Validator.notNull(other_day);
		return compareTo(other_day) < 0;
	}
	
	public boolean isAfter(Day other_day)
	{
		Validator.notNull(other_day);
		return compareTo(other_day) > 0;
	}
}

