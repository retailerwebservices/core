package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;
import java.util.Set;

import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldSet;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class RequestExportCSV extends StandardImmutableObject<RequestExportCSV>
{
	static public final TypeName TYPE_NAME = new TypeName("request_export_csv");

	static public final FieldDefinition.Stringable<IndexDefinition> FIELD_INDEX = new FieldDefinition.Stringable<IndexDefinition>("index", null, IndexDefinition.CONVERTER);
	static public final FieldDefinition.Boolean FIELD_EXPORT_ALL_DOCUMENTS = new FieldDefinition.Boolean("export_all_documents", null);
	static public final FieldDefinition.String FIELD_QUERY_STRING = new FieldDefinition.String("query_string", null);
	static public final FieldDefinition.Collection FIELD_FIELD_TO_INCLUDE_IN_EXPORT = new FieldDefinition.Collection("field_to_include_in_export", null);

	private IndexDefinition index;// (IndexDefinition, required)
	private boolean export_all_documents;// (boolean, required)
	private String query_string; // (String required, only has meaning when export_all_documents is false)
	private FieldSet<SearchIndexFieldDefinition> field_to_include_in_export;// (Set<SearchIndexFieldDefinition>, required, can be empty)

	public RequestExportCSV(IndexDefinition index, boolean export_all_documents, String query_string, Set<SearchIndexFieldDefinition> field_to_include_in_export)
	{
		this.index = index;
		this.export_all_documents = export_all_documents;
		this.query_string = query_string;
		this.field_to_include_in_export = new FieldHashSet<>(field_to_include_in_export);
		complete();
	}

	public RequestExportCSV(ObjectParseTree t)
	{
		this.index = t.getStringable(FIELD_INDEX);
		this.export_all_documents = t.getBoolean(FIELD_EXPORT_ALL_DOCUMENTS);
		this.field_to_include_in_export = t.getCollection(FIELD_FIELD_TO_INCLUDE_IN_EXPORT, new FieldHashSet<SearchIndexFieldDefinition>(), SearchIndexFieldDefinition.CONVERTER, ObjectParseTree.OnError.SKIP);
		
		this.query_string = t.getString(FIELD_QUERY_STRING);
	}

	public IndexDefinition getSimpleIndex()
	{
		return index;
	}

	public boolean getSimpleExportAllDocuments()
	{
		return export_all_documents;
	}

	public String getSimpleQueryString()
	{
		return query_string;
	}

	public Set<SearchIndexFieldDefinition> getSimpleFieldToIncludeInExport()
	{
		return field_to_include_in_export;
	}

	@Override
	public int compareTo(RequestExportCSV other)
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleIndex(), other.getSimpleIndex());
		ret = Comparison.continueCompare(ret, getSimpleExportAllDocuments(), other.getSimpleExportAllDocuments());
		ret = Comparison.continueCompare(ret, getSimpleQueryString(), other.getSimpleQueryString());
		ret = Comparison.continueCompare(ret, getSimpleFieldToIncludeInExport().size(), other.getSimpleFieldToIncludeInExport().size());

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
		writer.writeStringable(FIELD_INDEX, getSimpleIndex());
		writer.writeBoolean(FIELD_EXPORT_ALL_DOCUMENTS, getSimpleExportAllDocuments());
		writer.writeCollection(FIELD_FIELD_TO_INCLUDE_IN_EXPORT, getSimpleFieldToIncludeInExport(), WriteAs.OBJECT);
		writer.writeString(FIELD_QUERY_STRING, getSimpleQueryString());
	}

	@Override
	public void freeze()
	{
		field_to_include_in_export.freeze();
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.notNull(index, field_to_include_in_export, export_all_documents, query_string);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(index, field_to_include_in_export, export_all_documents, query_string);
	}

	@Override
	public boolean equals(Object obj)
	{

		if (!(obj instanceof RequestExportCSV))
		{
			return false;
		}

		RequestExportCSV other = (RequestExportCSV) obj;

		if (!getSimpleIndex().equals(other.getSimpleIndex()))
		{
			return false;
		}
		if (getSimpleExportAllDocuments() != other.getSimpleExportAllDocuments())
		{
			return false;
		}

		if (!getSimpleFieldToIncludeInExport().equals(other.getSimpleFieldToIncludeInExport()))
		{
			return false;
		}
		if (!getSimpleQueryString().equals(other.getSimpleQueryString()))
		{
			return false;
		}

		return true;
	}

}
