package org.jimmutable.core.objects.common.time;

import java.util.Objects;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class InstantDetails extends StandardObject<InstantDetails>
{
	static public final Kind KIND = new Kind("instant-details");
	static public final TypeName TYPE_NAME = new TypeName("instant_details");
	static public final FieldDefinition.Long FIELD_MS_FROM_EPOCH = new FieldDefinition.Long("ms_from_epoch", null);
	static public final FieldDefinition.Boolean FIELD_SUCCESS = new FieldDefinition.Boolean("success", null);
	static public final FieldDefinition.Stringable<TimezoneID> FIELD_TIMEZONE = new FieldDefinition.Stringable<TimezoneID>("timezone_id", null, TimezoneID.CONVERTER);
	static public final FieldDefinition.Stringable<Day> FIELD_DAY = new FieldDefinition.Stringable<Day>("day", null, Day.CONVERTER);
	static public final FieldDefinition.Integer FIELD_DAY_YEAR = new FieldDefinition.Integer("day_year", null);
	static public final FieldDefinition.Integer FIELD_DAY_MONTH = new FieldDefinition.Integer("day_month", null);
	static public final FieldDefinition.Integer FIELD_DAY_DAY = new FieldDefinition.Integer("day_day", null);

	static public final FieldDefinition.Long FIELD_HOURS_ON_TWELVE_HOUR_CLOCK = new FieldDefinition.Long("hours_on_twelve_hour_clock", null);
	static public final FieldDefinition.Long FIELD_MINUTES_ON_TWELVE_HOUR_CLOCK = new FieldDefinition.Long("minutes_on_twelve_hour_clock", null);
	static public final FieldDefinition.Long FIELD_SECONDS_ON_TWELVE_HOUR_CLOCK = new FieldDefinition.Long("seconds_on_twelve_hour_clock", null);

	static public final FieldDefinition.Boolean FIELD_IS_AM_ON_TWELVE_HOUR_CLOCK = new FieldDefinition.Boolean("is_am_on_twelve_hour_clock", null);

	static public final FieldDefinition.Long FIELD_HOURS_ON_TWENTY_FOUR_HOUR_CLOCK = new FieldDefinition.Long("hours_on_twenty_four_hour_clock", null);
	static public final FieldDefinition.Long FIELD_MINUTES_ON_TWENTY_FOUR_HOUR_CLOCK = new FieldDefinition.Long("minutes_on_twenty_four_hour_clock", null);
	static public final FieldDefinition.Long FIELD_SECONDS_ON_TWENTY_FOUR_HOUR_CLOCK = new FieldDefinition.Long("seconds_on_twenty_four_hour_clock", null);

	private boolean success;

	private long ms_from_epoch;

	private TimezoneID timezone_id;

	private Day day;

	private int day_year;

	private int day_month;

	private int day_day;

	private long hours_on_twelve_hour_clock;

	private long minutes_on_twelve_hour_clock;

	private long seconds_on_twelve_hour_clock;

	private boolean is_am_on_twelve_hour_clock;

	private long hours_on_twenty_four_hour_clock;

	private long minutes_on_twenty_four_hour_clock;

	private long seconds_on_twenty_four_hour_clock;

	public InstantDetails(ObjectParseTree t)
	{
		this.success = t.getBoolean(FIELD_SUCCESS);
		this.ms_from_epoch = t.getLong(FIELD_MS_FROM_EPOCH);
		this.timezone_id = t.getStringable(FIELD_TIMEZONE);
		this.day = t.getStringable(FIELD_DAY);
		this.day_year = t.getInt(FIELD_DAY_YEAR);
		this.day_month = t.getInt(FIELD_DAY_MONTH);
		this.day_day = t.getInt(FIELD_DAY_DAY);
		this.hours_on_twelve_hour_clock = t.getLong(FIELD_HOURS_ON_TWELVE_HOUR_CLOCK);
		this.minutes_on_twelve_hour_clock = t.getLong(FIELD_MINUTES_ON_TWELVE_HOUR_CLOCK);
		this.seconds_on_twelve_hour_clock = t.getLong(FIELD_SECONDS_ON_TWELVE_HOUR_CLOCK);
		this.is_am_on_twelve_hour_clock = t.getBoolean(FIELD_IS_AM_ON_TWELVE_HOUR_CLOCK);
		this.hours_on_twenty_four_hour_clock = t.getLong(FIELD_HOURS_ON_TWENTY_FOUR_HOUR_CLOCK);
		this.minutes_on_twenty_four_hour_clock = t.getLong(FIELD_MINUTES_ON_TWENTY_FOUR_HOUR_CLOCK);
		this.seconds_on_twenty_four_hour_clock = t.getLong(FIELD_SECONDS_ON_TWENTY_FOUR_HOUR_CLOCK);
	}

	public long getSimpleMillisecondsFromEpoch()
	{
		return ms_from_epoch;
	}

	@Override
	public int compareTo(InstantDetails other)
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleMillisecondsFromEpoch(), other.getSimpleMillisecondsFromEpoch());
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
		writer.writeBoolean(FIELD_SUCCESS, success);
		writer.writeLong(FIELD_MS_FROM_EPOCH, ms_from_epoch);
		writer.writeStringable(FIELD_TIMEZONE, timezone_id);
		writer.writeStringable(FIELD_DAY, day);
		writer.writeInt(FIELD_DAY_YEAR, day_year);
		writer.writeInt(FIELD_DAY_MONTH, day_month);
		writer.writeInt(FIELD_DAY_DAY, day_day);
		writer.writeLong(FIELD_HOURS_ON_TWELVE_HOUR_CLOCK, hours_on_twelve_hour_clock);
		writer.writeLong(FIELD_MINUTES_ON_TWELVE_HOUR_CLOCK, minutes_on_twelve_hour_clock);
		writer.writeLong(FIELD_SECONDS_ON_TWELVE_HOUR_CLOCK, seconds_on_twelve_hour_clock);
		writer.writeBoolean(FIELD_IS_AM_ON_TWELVE_HOUR_CLOCK, is_am_on_twelve_hour_clock);
		writer.writeLong(FIELD_HOURS_ON_TWENTY_FOUR_HOUR_CLOCK, hours_on_twenty_four_hour_clock);
		writer.writeLong(FIELD_MINUTES_ON_TWENTY_FOUR_HOUR_CLOCK, minutes_on_twenty_four_hour_clock);
		writer.writeLong(FIELD_SECONDS_ON_TWENTY_FOUR_HOUR_CLOCK, seconds_on_twenty_four_hour_clock);
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{

		Validator.notNull(success, ms_from_epoch, timezone_id, day, day_year, day_month, day_day, hours_on_twelve_hour_clock, minutes_on_twelve_hour_clock, seconds_on_twelve_hour_clock, is_am_on_twelve_hour_clock, hours_on_twenty_four_hour_clock, minutes_on_twenty_four_hour_clock, seconds_on_twenty_four_hour_clock);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(ms_from_epoch);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof InstantDetails))
		{
			return false;
		}

		InstantDetails other = (InstantDetails) obj;

		return getSimpleMillisecondsFromEpoch() == other.getSimpleMillisecondsFromEpoch();
	}

}
