package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.cloud.elasticsearch.IndexVersion;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;
/**
 * This Class is used to bind our AdvancedSearch Combo box choices. 
 * <br> the Label is the field that we put our selection in. 
 * <br> the value of the choice selected
 * @author andrew.towe
 *
 */
public class AdvancedSearchComboBoxChoice extends StandardImmutableObject<AdvancedSearchComboBoxChoice>
{
	static public final FieldDefinition.String FIELD_LABEL = new FieldDefinition.String("label", null);
	static public final FieldDefinition.String FIELD_VALUE = new FieldDefinition.String("value", null);

	static public final TypeName TYPE_NAME = new TypeName("advancedsearchcomboboxchoice");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("advancedsearchcomboboxchoice"), new IndexVersion("v1"));

	static public final TopicDefinition TOPIC_DEF = new TopicDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new TopicId("advancedsearchcomboboxchoice"));

	static private final SearchIndexFieldDefinition SEARCH_FIELD_LABEL = new SearchIndexFieldDefinition(FIELD_LABEL.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TEXT);

	static public final SearchIndexDefinition INDEX_MAPPING;

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_LABEL);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}

	private String label;
	private String value;

	public AdvancedSearchComboBoxChoice( String label, String value )
	{
		this.label = label;
		this.value = value;
		complete();
	}

	public AdvancedSearchComboBoxChoice( ObjectParseTree t )
	{
		this.label = t.getString(FIELD_LABEL);
		this.value = t.getString(FIELD_VALUE);
	}

	@Override
	public int compareTo( AdvancedSearchComboBoxChoice other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleLabel(), other.getSimpleLabel());
		ret = Comparison.continueCompare(ret, getSimpleValue(), other.getSimpleValue());

		return ret == 0 ? 0 : ret > 0 ? 1 : -1;// Comparison.continueCompare for strings returns other numbers besides 1 -1 and
		// 0. We are only concerned with Greater then and less then or equal.

	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeString(FIELD_LABEL, getSimpleLabel());
		writer.writeString(FIELD_VALUE, getSimpleValue());
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
		Validator.notNull(getSimpleLabel(), getSimpleValue());

	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleLabel(), getSimpleValue());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof AdvancedSearchComboBoxChoice) )
			return false;

		AdvancedSearchComboBoxChoice other = (AdvancedSearchComboBoxChoice) obj;

		if ( !Objects.equals(getSimpleLabel(), (other.getSimpleLabel())) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleValue(), other.getSimpleValue()) )
		{
			return false;
		}

		return true;
	}

	public String getSimpleLabel()
	{
		return label;
	}

	public String getSimpleValue()
	{
		return value;
	}
}
