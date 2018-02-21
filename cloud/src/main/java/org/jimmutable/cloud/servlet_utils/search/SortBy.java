package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;


/**
 * An object passed in to dictate how to sort. Contains a field and a direction
 * 
 * @author jon.toy
 *
 */
public class SortBy extends StandardImmutableObject<SortBy>
{
	static public final Converter<SortBy> CONVERTER = new Converter<>();

	public static final FieldDefinition.StandardObject FIELD_FIELD = new FieldDefinition.StandardObject("field", null);
	public static final FieldDefinition.Enum<SortDirection> FIELD_DIRECTION = new FieldDefinition.Enum<SortDirection>("direction", null, SortDirection.CONVERTER);

	public static final TypeName TYPE_NAME = new TypeName(SortBy.class.getName());

	private SearchIndexFieldDefinition field;
	private SortDirection direction;

	public SortBy(ObjectParseTree t)
	{
		field = (SearchIndexFieldDefinition) t.getObject(FIELD_FIELD);
		direction = t.getEnum(FIELD_DIRECTION);
	}

	public SortBy(SearchIndexFieldDefinition field, SortDirection direction)
	{
		super();
		this.field = field;
		this.direction = direction;
		complete();
	}

	/**
	 * The sort's field definition
	 * 
	 * @return SearchIndexFieldDefinition
	 */
	public SearchIndexFieldDefinition getSimpleField() { return field; }

	/**
	 * The sort's direction
	 * 
	 * @return SortDirection
	 */
	public SortDirection getSimpleDirection() { return direction; }

	@Override
	public int compareTo(SortBy other)
	{
		int ret = Comparison.startCompare();

		Comparison.continueCompare(ret, this.getSimpleField(), other.getSimpleField());
		Comparison.continueCompare(ret, this.getSimpleDirection(), other.getSimpleDirection());

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
		writer.writeObject(FIELD_FIELD, this.getSimpleField());
		writer.writeEnum(FIELD_DIRECTION, this.getSimpleDirection());
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
		Validator.notNull(this.getSimpleField());
		Validator.notNull(this.getSimpleDirection());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(field, direction);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SortBy))
			return false;

		SortBy other = (SortBy) obj;

		if (!this.getSimpleField().equals(other.getSimpleField()))
			return false;
		if (!this.getSimpleDirection().equals(other.getSimpleDirection()))
			return false;

		return true;
	}

	static private class Converter<S extends SortBy> extends ReadAs
	{
		public SortBy from(SearchIndexFieldDefinition field, SortDirection direction, SortBy default_value)
		{
			try
			{
				return new SortBy(field, direction);
			} catch (Exception e)
			{
				return default_value;
			}
		}

		@Override
		public Object readAs(ObjectParseTree t)
		{
			SearchIndexFieldDefinition name = (SearchIndexFieldDefinition) t.getObject(FIELD_FIELD);
			SortDirection type = t.getEnum(FIELD_DIRECTION);
			return from(name, type, null);
		}
	}

}
