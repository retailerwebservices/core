package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Normalizer;
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

	static private final SearchIndexFieldDefinition SEARCH_FIELD_LABEL = new SearchIndexFieldDefinition(FIELD_ADVANCED_SEARCH_FIELDS.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_FIELDS_IN_VIEW.getSimpleFieldName(), SearchIndexFieldType.TEXT);

	
	// CODE REVIEW: This object is not indexable, therefore not INDEX_MAPPING is needed
	static public final SearchIndexDefinition INDEX_MAPPING;

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_LABEL);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}

	// CODE REVIEW: You should use FieldList / FieldArrayList so that your object can be immutable (as it stands right now, the freeze function does not make these lists unchangeable)
	private List<AdvancedSearchField> advanced_search_fields;
	private List<IncludeFieldInView> fields_in_view;

	public SearchUIData( List<AdvancedSearchField> advanced_search_fields, List<IncludeFieldInView> fields_in_view )
	{
		// CODE REVEIW: This code does not probide for immutability... if advanced search fields or fields_in_view are modified by the calling code the contents of this object will change
		// You need to copy them over
		this.advanced_search_fields = advanced_search_fields;
		this.fields_in_view = fields_in_view;
		complete();
	}

	public SearchUIData( ObjectParseTree o )
	{
		this.advanced_search_fields =  o.getCollection(FIELD_ADVANCED_SEARCH_FIELDS, new ArrayList<AdvancedSearchField>(), ReadAs.OBJECT, OnError.THROW_EXCEPTION);
		this.fields_in_view =  o.getCollection(FIELD_FIELDS_IN_VIEW, new ArrayList<IncludeFieldInView>(), ReadAs.OBJECT, OnError.THROW_EXCEPTION);
	}

	/**
	 * This is how we tell if the advancedsearchfield is either a text field or New
	 * 
	 * @author andrew.towe
	 *
	 */
	// CODE REVIEW: Place this class in its own file
	public enum AdvancedSearchFieldType implements StandardEnum
	{
		TEXT("text"), COMBO_BOX("combo-box");
		static public final MyConverter CONVERTER = new MyConverter();

		private String code;

		@Override
		public String getSimpleCode()
		{
			return code;
		}

		public String toString()
		{
			return code;
		}

		private AdvancedSearchFieldType( String code )
		{
			Validator.notNull(code);
			this.code = Normalizer.lowerCase(code);
		}

		static public class MyConverter extends StandardEnum.Converter<AdvancedSearchFieldType>
		{
			public AdvancedSearchFieldType fromCode( String code, AdvancedSearchFieldType default_value )
			{
				if ( code == null )
					return default_value;

				for ( AdvancedSearchFieldType t : AdvancedSearchFieldType.values() )
				{
					if ( t.getSimpleCode().equalsIgnoreCase(code) )
						return t;
				}

				return default_value;
			}
		}
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
		// TODO: YOU need to freeze 

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
		
		// CODE REVIEW: You also need to make sure that the collections contain no nulls using a call to 	Validator.containsNoNulls(collection);
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
