package org.jimmutable.cloud.cache;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * This is used by our Cache Metrics 
 * to keep track of what is happening. 
 * @author andrew.towe
 *
 */
public class CacheEvent extends StandardImmutableObject<CacheEvent>
{
	static public final TypeName TYPE_NAME = new TypeName("cache_event");

	static public final FieldDefinition.Enum<CacheActivity> FIELD_ACTIVITY = new FieldDefinition.Enum<CacheActivity>("activity", null, CacheActivity.CONVERTER);
	static public final FieldDefinition.Enum<CacheMetric> FIELD_METRIC = new FieldDefinition.Enum<CacheMetric>("metric", null, CacheMetric.CONVERTER);
	static public final FieldDefinition.Stringable<CacheKey> FIELD_KEY = new FieldDefinition.Stringable<CacheKey>("key", null, CacheKey.CONVERTER);
	static public final FieldDefinition.Long FIELD_TIMESTAMP = new FieldDefinition.Long("timestamp", null);

	CacheActivity activity;// required
	CacheMetric metric; // required
	CacheKey key; // required
	long timestamp;// required
	// Add method getSimpleTimestampHumanReadable() to convert timestamp to a
	// friendly format like YYYY-MM-DD HH:MM:SS

	public CacheEvent( ObjectParseTree t )
	{
		this.activity = t.getEnum(FIELD_ACTIVITY);
		this.metric = t.getEnum(FIELD_METRIC);
		this.key = (CacheKey) t.getStringable(FIELD_KEY);
		this.timestamp = t.getLong(FIELD_TIMESTAMP);
	}

	@Override
	public int compareTo( CacheEvent o )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleActivity(), o.getSimpleActivity());
		ret = Comparison.continueCompare(ret, getSimpleMetric(), o.getSimpleMetric());
		ret = Comparison.continueCompare(ret, getSimpleKey(), o.getSimpleKey());
		ret = Comparison.continueCompare(ret, getSimpleTimeStamp(), o.getSimpleTimeStamp());

		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeEnum(FIELD_ACTIVITY, activity);
		writer.writeEnum(FIELD_METRIC, metric);
		writer.writeStringable(FIELD_KEY, key);
		writer.writeLong(FIELD_TIMESTAMP, timestamp);
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
		Validator.notNull(activity, "Activity");
		Validator.notNull(metric, "Metric");
		Validator.notNull(key, "Key");
		Validator.notNull(timestamp, "Timestamp");

	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((metric == null) ? 0 : metric.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		CacheEvent other = (CacheEvent) obj;
		if ( activity != other.activity )
			return false;
		if ( key == null )
		{
			if ( other.key != null )
				return false;
		}
		else if ( !key.equals(other.key) )
			return false;
		if ( metric != other.metric )
			return false;
		if ( timestamp != other.timestamp )
			return false;
		return true;
	}

	public CacheActivity getSimpleActivity()
	{
		return activity;
	}

	public CacheMetric getSimpleMetric()
	{
		return metric;
	}

	public CacheKey getSimpleKey()
	{
		return key;
	}

	public long getSimpleTimeStamp()
	{
		return timestamp;
	}

	public String getSimpleTimestampHumanReadable()
	{
		Date date = new Date(timestamp);
		SimpleDateFormat df2 = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
		return df2.format(date);
	}

}
