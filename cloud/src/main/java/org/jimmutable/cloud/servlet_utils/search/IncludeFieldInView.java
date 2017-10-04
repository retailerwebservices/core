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
 * This class is used to handle the various fields that we use. 
 * We will have the:
 * <li> name of the field we are searching for
 * <li> The Search Document Field that will connect the Field that we input data on to the 
 * <li> Whether or not the field is included by default or not. 
 * @author andrew.towe
 *
 */

public class IncludeFieldInView extends StandardImmutableObject<IncludeFieldInView>
{
	static public final FieldDefinition.String FIELD_LABEL = new FieldDefinition.String("label", null);
	static public final FieldDefinition.Stringable<SearchFieldId> FIELD_SEARCH_DOCUMENT_FIELD = new FieldDefinition.Stringable<SearchFieldId>("search_document_field", null, SearchFieldId.CONVERTER);
	static public final FieldDefinition.Boolean FIELD_INCLUDED_BY_DEFAULT = new FieldDefinition.Boolean("included_by_default", null);

	static public final TypeName TYPE_NAME = new TypeName("includefieldinview");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("includefieldinview"), new IndexVersion("v1"));

	static public final TopicDefinition TOPIC_DEF = new TopicDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new TopicId("includefieldinview"));

	static private final SearchIndexFieldDefinition SEARCH_FIELD_LABEL = new SearchIndexFieldDefinition(FIELD_LABEL.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_SEARCH_DOCUMENT_FIELD = new SearchIndexFieldDefinition(FIELD_SEARCH_DOCUMENT_FIELD.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_INCLUDED_BY_DEFAULT = new SearchIndexFieldDefinition(FIELD_INCLUDED_BY_DEFAULT.getSimpleFieldName(), SearchIndexFieldType.BOOLEAN);
	
	static public final SearchIndexDefinition INDEX_MAPPING;

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_LABEL);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_SEARCH_DOCUMENT_FIELD);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_INCLUDED_BY_DEFAULT);
		
		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}
	
	private String label;//required
	private SearchFieldId search_document_field;//required
	private boolean includeded_by_default;//required
	
	public  IncludeFieldInView( String label, SearchFieldId search_document_field, boolean included_by_default )
	{
		this.label = label;
		this.search_document_field = search_document_field;
		this.includeded_by_default = included_by_default;
		complete();
	}

	public IncludeFieldInView( ObjectParseTree t )
	{
		this.label = t.getString(FIELD_LABEL);
		this.search_document_field=(SearchFieldId) t.getStringable(FIELD_SEARCH_DOCUMENT_FIELD);
		this.includeded_by_default = t.getBoolean(FIELD_INCLUDED_BY_DEFAULT);
		
	}
	
	@Override
	public int compareTo( IncludeFieldInView other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleLabel(), other.getSimpleLabel());
		ret = Comparison.continueCompare(ret, getSimpleSearchDocumentField(), other.getSimpleSearchDocumentField());
		ret = Comparison.continueCompare(ret, isSimpleIncludedByDefault(), other.isSimpleIncludedByDefault());
		
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
		writer.writeString(FIELD_LABEL, getSimpleLabel());
		writer.writeString(FIELD_SEARCH_DOCUMENT_FIELD, getSimpleSearchDocumentField().getSimpleValue());
		writer.writeBoolean(FIELD_INCLUDED_BY_DEFAULT, isSimpleIncludedByDefault());

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
		Validator.notNull(getSimpleLabel(), getSimpleSearchDocumentField(), isSimpleIncludedByDefault());

	}

	public boolean isSimpleIncludedByDefault()
	{
		return includeded_by_default;
	}

	public SearchFieldId getSimpleSearchDocumentField()
	{
		return search_document_field;
	}

	public String getSimpleLabel()
	{
		return label;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleLabel(), getSimpleSearchDocumentField(), isSimpleIncludedByDefault());
	}

	@Override
	public boolean equals( Object obj )
	{
		if (!(obj instanceof IncludeFieldInView))
			return false;

		IncludeFieldInView other = (IncludeFieldInView) obj;

		if (!Objects.equals(getSimpleLabel(), (other.getSimpleLabel())))
		{
			return false;
		}
		if (!Objects.equals(getSimpleSearchDocumentField(), other.getSimpleSearchDocumentField()))
		{
			return false;
		}
		if (!Objects.equals(isSimpleIncludedByDefault(), other.isSimpleIncludedByDefault()))
		{
			return false;
		}

		return true;
	}

}
