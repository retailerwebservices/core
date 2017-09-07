package org.jimmutable.aws.elasticsearch;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * A search Index and its fields
 * 
 * @author trevorbox
 *
 */
public class SearchIndexDefinition extends StandardImmutableObject<SearchIndexDefinition>
{

	public static final FieldDefinition.Collection FIELD_FIELDS = new FieldDefinition.Collection("fields",
			new FieldArrayList<SearchIndexFieldDefinition>());
	public static final FieldDefinition.Stringable<IndexDefinition> FIELD_INDEX_DEFINITION = new FieldDefinition.Stringable<IndexDefinition>(
			"index", null, IndexDefinition.CONVERTER);
	public static final TypeName TYPE_NAME = new TypeName(SearchIndexDefinition.class.getName());

	private IndexDefinition index;
	private FieldList<SearchIndexFieldDefinition> fields;

	public SearchIndexDefinition(ObjectParseTree t)
	{
		index = t.getStringable(FIELD_INDEX_DEFINITION);
		fields = t.getCollection(FIELD_FIELDS, new FieldArrayList<SearchIndexFieldDefinition>(), ReadAs.OBJECT,
				ObjectParseTree.OnError.SKIP);
	}

	public SearchIndexDefinition(IndexDefinition index, Collection<SearchIndexFieldDefinition> fields)
	{
		super();
		this.index = index;
		this.fields = new FieldArrayList<SearchIndexFieldDefinition>(fields);
		complete();
	}

	public List<SearchIndexFieldDefinition> getSimpleFields()
	{
		return fields;
	}

	public IndexDefinition getSimpleIndex()
	{
		return index;
	}

	@Override
	public int compareTo(SearchIndexDefinition other)
	{
		int ret = Comparison.startCompare();

		Comparison.continueCompare(ret, this.getSimpleIndex(), other.getSimpleIndex());
		Comparison.continueCompare(ret, this.getSimpleFields().size(), other.getSimpleFields().size());

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
		writer.writeStringable(FIELD_INDEX_DEFINITION, this.getSimpleIndex());
		writer.writeCollection(FIELD_FIELDS, this.getSimpleFields(), WriteAs.OBJECT);
	}

	@Override
	public void freeze()
	{
		fields.freeze();
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.notNull(this.getSimpleIndex());
		Validator.notNull(this.getSimpleFields());
		Validator.containsNoNulls(this.getSimpleFields());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(index, fields);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SearchIndexDefinition))
			return false;

		SearchIndexDefinition other = (SearchIndexDefinition) obj;

		if (!this.getSimpleIndex().equals(other.getSimpleIndex()))
			return false;
		if (!this.getSimpleFields().equals(other.getSimpleFields()))
			return false;

		return true;
	}

}
