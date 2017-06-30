package org.jimmutable.core.objects.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.joda.time.DateTimeZone;

/**
 * A simple stringable used to store timezone ID(s) (uses JodaTime)
 * @author jim.kane
 *
 */
public class TimezoneID extends Stringable
{
	static private List<TimezoneID> cached_common_time_zones = null;
	static private List<TimezoneID> cached_uncommon_time_zones = null;
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	private DateTimeZone value_time_zone;
	
	public TimezoneID(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		
		try
		{
			value_time_zone = DateTimeZone.forID(getSimpleValue());
		}
		catch(Exception e)
		{
			throw new ValidationException(String.format("Invalid timezone id %s", getSimpleValue()));
		}
	}
	
	/**
	 * Get the DateTimeZone assocaited with the ID
	 * @return
	 */
	public DateTimeZone getSimpleDateTimeZoneValue()
	{
		return value_time_zone;
	}
	
	static public class MyConverter extends Stringable.Converter<TimezoneID>
	{
		public TimezoneID fromString(String str, TimezoneID default_value)
		{
			try
			{
				return new TimezoneID(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	
	
	/**
	 * Get a sorted list of common (US) timezone(s)
	 * @return
	 */
	static public List<TimezoneID> getSimpleAllCommonTimeZoneIDs()
	{
		
		if ( cached_common_time_zones != null ) return cached_common_time_zones;
		
		List<TimezoneID> ret = new ArrayList();
		
		ret.add(new TimezoneID("US/Hawaii"));
		ret.add(new TimezoneID("US/Alaska"));
		ret.add(new TimezoneID("US/Arizona"));
		ret.add(new TimezoneID("US/Pacific"));
		ret.add(new TimezoneID("US/Mountain"));
		ret.add(new TimezoneID("US/Central"));
		ret.add(new TimezoneID("US/Indiana-Starke"));
		ret.add(new TimezoneID("US/Michigan"));
		ret.add(new TimezoneID("US/East-Indiana"));
		ret.add(new TimezoneID("US/Eastern"));
		
		Collections.sort(ret);
		
		cached_common_time_zones = ret;
		
		return cached_common_time_zones;
	
	}
	
	/**
	 * Get a list of all valid Timezone ID(s)
	 * @return
	 */
	static public List<TimezoneID> getSimeAllTimezoneIDs()
	{
		if ( cached_uncommon_time_zones != null ) return cached_uncommon_time_zones;
		
		Set<String> zones = DateTimeZone.getAvailableIDs();
		
		List<TimezoneID> ret = new ArrayList();
		
		for ( String zone : zones )
		{
			ret.add(new TimezoneID(zone));
		}
		
		Collections.sort(ret);
		
		cached_uncommon_time_zones = ret;
		return cached_uncommon_time_zones;
	}
}
