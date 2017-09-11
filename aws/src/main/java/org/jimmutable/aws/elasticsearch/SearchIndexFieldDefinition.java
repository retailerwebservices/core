package org.jimmutable.aws.elasticsearch;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * A search index field name and data type (mapping)
 * 
 * @author trevorbox
 *
 */
public class SearchIndexFieldDefinition extends StandardImmutableObject<SearchIndexFieldDefinition>
{

	public static final FieldDefinition.StandardObject FIELD_FIELD_NAME = new FieldDefinition.StandardObject("name", null);
	public static final FieldDefinition.Enum<SearchIndexFieldType> FIELD_SEARCH_INDEX_FIELD_TYPE = new FieldDefinition.Enum<SearchIndexFieldType>("type", null, SearchIndexFieldType.CONVERTER);

	public static final TypeName TYPE_NAME = new TypeName(SearchIndexFieldDefinition.class.getName());

	private FieldName name;
	private SearchIndexFieldType type;

	public SearchIndexFieldDefinition(ObjectParseTree t)
	{
		name = (FieldName) t.getObject(FIELD_FIELD_NAME);
		type = t.getEnum(FIELD_SEARCH_INDEX_FIELD_TYPE);
	}

	public SearchIndexFieldDefinition(FieldName name, SearchIndexFieldType type)
	{
		super();
		this.name = name;
		this.type = type;
		complete();
	}

	public FieldName getSimpleFieldName()
	{
		return name;
	}

	public SearchIndexFieldType getSimpleType()
	{
		return type;
	}

	@Override
	public int compareTo(SearchIndexFieldDefinition other)
	{
		int ret = Comparison.startCompare();

		Comparison.continueCompare(ret, this.getSimpleFieldName(), other.getSimpleFieldName());
		Comparison.continueCompare(ret, this.getSimpleType(), other.getSimpleType());

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
		writer.writeObject(FIELD_FIELD_NAME, this.getSimpleFieldName());
		writer.writeEnum(FIELD_SEARCH_INDEX_FIELD_TYPE, this.getSimpleType());
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
		Validator.notNull(this.getSimpleFieldName());
		Validator.notNull(this.getSimpleType());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, type);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SearchIndexFieldDefinition))
			return false;

		SearchIndexFieldDefinition other = (SearchIndexFieldDefinition) obj;

		if (!this.getSimpleFieldName().equals(other.getSimpleFieldName()))
			return false;
		if (!this.getSimpleType().equals(other.getSimpleType()))
			return false;

		return true;
	}

}
