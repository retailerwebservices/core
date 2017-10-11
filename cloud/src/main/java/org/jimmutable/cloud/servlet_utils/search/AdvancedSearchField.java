package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
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
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

// CODE REVIEW: You need closing LI tags in the java doc...
/**
 * This is the class that we use for our advanced search fields
 * We have a couple of things in this class
 * <li> A label field for what our advanced search Field calls itself i.e. name
 * <li> A Search Document Field that connects our advanced search field to the information that we are searching for
 * <li> The Field type: either a text field or a Combo Box area
 * <li> Combo Box Choices: all the things the user can select if the field type is a Combo box. 
 * @author andrew.towe
 *
 */

public class AdvancedSearchField extends StandardImmutableObject<AdvancedSearchField>
{
	static public final FieldDefinition.String FIELD_LABEL = new FieldDefinition.String("label", null);
	static public final FieldDefinition.Stringable<SearchFieldId> FIELD_SEARCH_DOCUMENT_FIELD = new FieldDefinition.Stringable<SearchFieldId>("searchdocumentfield", null, SearchFieldId.CONVERTER);
	static public final FieldDefinition.Enum<AdvancedSearchFieldType> FIELD_TYPE = new FieldDefinition.Enum<AdvancedSearchFieldType>("type", AdvancedSearchFieldType.TEXT, AdvancedSearchFieldType.CONVERTER);
	static public final FieldDefinition.Collection FIELD_COMBO_BOX_CHOICES = new FieldDefinition.Collection("combo_box_choices", null);

	static public final TypeName TYPE_NAME = new TypeName("advancedsearchfield");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("advancedsearchfield"), new IndexVersion("v1"));

	static public final TopicDefinition TOPIC_DEF = new TopicDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new TopicId("advancedsearchfield"));

	// CODE REVIEW: This class does not implement indexable, so no search index definitions etc. are needed...
	
	static private final SearchIndexFieldDefinition SEARCH_FIELD_LABEL = new SearchIndexFieldDefinition(FIELD_LABEL.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_SEARCH_DOCUMENT_FIELD = new SearchIndexFieldDefinition(FIELD_SEARCH_DOCUMENT_FIELD.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_TYPE = new SearchIndexFieldDefinition(FIELD_TYPE.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_COMBO_BOX_CHOICES = new SearchIndexFieldDefinition(FIELD_COMBO_BOX_CHOICES.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static public final SearchIndexDefinition INDEX_MAPPING;

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_LABEL);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_SEARCH_DOCUMENT_FIELD);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_TYPE);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_COMBO_BOX_CHOICES);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}

	private String label;// required
	private SearchFieldId search_document_field;// required
	private AdvancedSearchFieldType type;// required // CODE REVIEW: AdvancedSearchFieldType should be moved into its own file
	private List<AdvancedSearchComboBoxChoice> combo_box_choices;// optional // CODE REVIEW: You need to use FieldArrayList to ensure immutability

	public AdvancedSearchField( String label, SearchFieldId search_document_field, AdvancedSearchFieldType type, List<AdvancedSearchComboBoxChoice> combo_box_choices )
	{
		this.label = label;
		this.search_document_field = search_document_field;
		this.type = type;
		this.combo_box_choices = combo_box_choices; // CODE REVIEW: This breaks immutability.  Use FieldArrayList and copy
		complete();
	}

	public AdvancedSearchField( ObjectParseTree o )
	{
		this.label = o.getString(FIELD_LABEL);
		this.search_document_field = o.getStringable(FIELD_SEARCH_DOCUMENT_FIELD);
		this.type = o.getEnum(FIELD_TYPE);
		this.combo_box_choices = o.getCollection(FIELD_COMBO_BOX_CHOICES, new ArrayList<AdvancedSearchComboBoxChoice>(), ReadAs.OBJECT, OnError.SKIP); // Code review: Use field array list
	}

	@Override
	public int compareTo( AdvancedSearchField other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleLabel(), other.getSimpleLabel());
		ret = Comparison.continueCompare(ret, getSimpleSearchDocumentField(), other.getSimpleSearchDocumentField());
		ret = Comparison.continueCompare(ret, getSimpleType(), other.getSimpleType());
		ret = Comparison.continueCompare(ret, getOptionalComboBoxChoices(null).size(), other.getOptionalComboBoxChoices(null).size());

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
		writer.writeStringable(FIELD_SEARCH_DOCUMENT_FIELD, getSimpleSearchDocumentField());
		writer.writeEnum(FIELD_TYPE, getSimpleType());
		writer.writeCollection(FIELD_COMBO_BOX_CHOICES, getOptionalComboBoxChoices(null), WriteAs.OBJECT);
	}

	@Override
	public void freeze()
	{
		// CODE REVIEW: Freeze the combo_box_choices feild
	}

	@Override
	public void normalize()
	{
		

	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleLabel(), getSimpleSearchDocumentField(), getSimpleType());
		// CODE REVIEW: You need to verify that combo_box_choices does not contain any null values

	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleLabel(), getSimpleSearchDocumentField(), getSimpleType(), getOptionalComboBoxChoices(null));
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof AdvancedSearchField) )
			return false;

		AdvancedSearchField other = (AdvancedSearchField) obj;

		if ( !Objects.equals(getSimpleLabel(), (other.getSimpleLabel())) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleSearchDocumentField(), other.getSimpleSearchDocumentField()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleType(), (other.getSimpleType())) )
		{
			return false;
		}
		if ( !Objects.equals(getOptionalComboBoxChoices(null), other.getOptionalComboBoxChoices(null)) )
		{
			return false;
		}

		return true;
	}

	public String getSimpleLabel()
	{
		return label;
	}

	public SearchFieldId getSimpleSearchDocumentField()
	{
		return search_document_field;
	}

	public AdvancedSearchFieldType getSimpleType()
	{
		return type;
	}

	public List<AdvancedSearchComboBoxChoice> getOptionalComboBoxChoices( List<AdvancedSearchComboBoxChoice> default_value )
	{
		return Optional.getOptional(combo_box_choices, null, default_value);
	}

	public boolean hasComboBoxChoices()
	{
		return getSimpleType().equals(AdvancedSearchFieldType.COMBO_BOX);
	}
}
