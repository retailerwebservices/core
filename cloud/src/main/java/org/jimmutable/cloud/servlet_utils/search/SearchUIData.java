package org.jimmutable.cloud.servlet_utils.search;

import java.util.List;
import java.util.Objects;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.cloud.elasticsearch.IndexVersion;
import org.jimmutable.cloud.messaging.TopicDefinition;
import org.jimmutable.cloud.messaging.TopicId;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * This object is used to manage the user interface for searching. It contains 2
 * fields
 * <li>The Fields that we may want to search over (AdvancedSearchField)
 * <li>The default presentation for each search field. (FieldsInView)
 * 
 * @author andrew.towe
 *
 */
public class SearchUIData extends StandardImmutableObject<SearchUIData>
{

	static public final FieldDefinition.Collection FIELD_ADVANCED_SEARCH_FIELDS = new FieldDefinition.Collection("advancedsearchfields", null);
	static public final FieldDefinition.Collection FIELD_FIELDS_IN_VIEW = new FieldDefinition.Collection("fieldsinview", null);

	static public final TypeName TYPE_NAME = new TypeName("searchuidata");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("searchuidata"), new IndexVersion("v1"));

	static public final TopicDefinition TOPIC_DEF = new TopicDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new TopicId("searchuidata"));

	private FieldArrayList<AdvancedSearchField> advanced_search_fields;
	private FieldArrayList<IncludeFieldInView> fields_in_view;

	public SearchUIData( List<AdvancedSearchField> advanced_search_fields, List<IncludeFieldInView> fields_in_view )
	{
		this.advanced_search_fields = new FieldArrayList<>(advanced_search_fields);
		this.fields_in_view = new FieldArrayList<>(fields_in_view);
		complete();
	}

	public SearchUIData( ObjectParseTree o )
	{
		this.advanced_search_fields = o.getCollection(FIELD_ADVANCED_SEARCH_FIELDS, new FieldArrayList<AdvancedSearchField>(), ReadAs.OBJECT, OnError.THROW_EXCEPTION);
		this.fields_in_view = o.getCollection(FIELD_FIELDS_IN_VIEW, new FieldArrayList<IncludeFieldInView>(), ReadAs.OBJECT, OnError.THROW_EXCEPTION);
	}

	@Override
	public int compareTo( SearchUIData other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleAdvancedSearchFields().size(), other.getSimpleAdvancedSearchFields().size());
		ret = Comparison.continueCompare(ret, getSimpleFieldsInView().size(), other.getSimpleFieldsInView().size());

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
		writer.writeCollection(FIELD_ADVANCED_SEARCH_FIELDS, getSimpleAdvancedSearchFields(), WriteAs.OBJECT);
		writer.writeCollection(FIELD_FIELDS_IN_VIEW, getSimpleFieldsInView(), WriteAs.OBJECT);

	}

	@Override
	public void freeze()
	{
		advanced_search_fields.freeze();
		fields_in_view.freeze();
	}

	@Override
	public void normalize()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleAdvancedSearchFields(), getSimpleFieldsInView());
		
		Validator.containsNoNulls(getSimpleAdvancedSearchFields());
		Validator.containsNoNulls(getSimpleFieldsInView());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleAdvancedSearchFields(), getSimpleFieldsInView());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof SearchUIData) )
			return false;

		SearchUIData other = (SearchUIData) obj;

		if ( !Objects.equals(getSimpleAdvancedSearchFields(), (other.getSimpleAdvancedSearchFields())) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleFieldsInView(), other.getSimpleFieldsInView()) )
		{
			return false;
		}

		return true;
	}

	public List<AdvancedSearchField> getSimpleAdvancedSearchFields()
	{
		return advanced_search_fields;
	}

	public List<IncludeFieldInView> getSimpleFieldsInView()
	{
		return fields_in_view;
	}

}
