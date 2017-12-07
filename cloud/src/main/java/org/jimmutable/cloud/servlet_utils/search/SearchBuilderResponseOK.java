package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class SearchBuilderResponseOK extends JSONServletResponse
{

	static public final TypeName TYPE_NAME = new TypeName("search_builder_response_ok");
	static public final FieldDefinition.String FIELD_SEARCH_REQUEST = new FieldDefinition.String("search_request", null);
	static public final FieldDefinition.Collection FIELD_RESULTS = new FieldDefinition.Collection("results", null);
	static public final FieldDefinition.Boolean FIELD_HAS_MORE_RESULTS = new FieldDefinition.Boolean("has_more_results", null);
	static public final FieldDefinition.Long FIELD_TOTAL_HITS = new FieldDefinition.Long("total_hits", null);

	static public final int HTTP_STATUS_CODE_OK = 200;

	private String search_request; // required
	private FieldList<OneSearchResult> results; // required
	private boolean has_more_results; // required
	private long total_hits; //

	
	public SearchBuilderResponseOK( String search_request, List<OneSearchResult> results, long total_hits, boolean has_more_results )
	{
		this.search_request = search_request;
		this.results = new FieldArrayList<OneSearchResult>(results);
		this.total_hits = total_hits;
		this.has_more_results = has_more_results;
		
		complete();
	}

	public SearchBuilderResponseOK( ObjectParseTree t )
	{
		this.search_request = t.getString(FIELD_SEARCH_REQUEST);
		this.results = t.getCollection(FIELD_RESULTS, new FieldArrayList<OneSearchResult>(), ReadAs.OBJECT, OnError.SKIP);
		this.total_hits =  t.getLong(FIELD_TOTAL_HITS);
		this.has_more_results = t.getBoolean(FIELD_HAS_MORE_RESULTS);
	}
	
	
	public String getSimpleSearchRequest()
	{
		return search_request;
	}

	public FieldList<OneSearchResult> getSimpleResults()
	{
		return results;
	}

	public boolean getSimpleHasMoreResults()
	{
		return has_more_results;
	}

	public long getTotalHits()
	{
		return total_hits;
	}

	@Override
	public int compareTo(JSONServletResponse obj)
	{
		if (!(obj instanceof SearchBuilderResponseOK))
		{
			return -1;
		}
		SearchBuilderResponseOK other = (SearchBuilderResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleSearchRequest(), other.getSimpleSearchRequest());
		ret = Comparison.continueCompare(ret, getSimpleResults().size(), other.getSimpleResults().size());
		ret = Comparison.continueCompare(ret, getTotalHits(), other.getTotalHits());
		ret = Comparison.continueCompare(ret, getSimpleHasMoreResults(), other.getSimpleHasMoreResults());

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
		writer.writeBoolean(FIELD_HAS_MORE_RESULTS, getSimpleHasMoreResults());
		writer.writeLong(FIELD_TOTAL_HITS, getTotalHits());
		writer.writeCollection(FIELD_RESULTS, getSimpleResults(), WriteAs.OBJECT);
		writer.writeString(FIELD_SEARCH_REQUEST, getSimpleSearchRequest());
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_OK;
	}

	@Override
	public void freeze()
	{
		results.freeze();
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.notNull(search_request, results, has_more_results, total_hits);
		Validator.containsNoNulls(results);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(search_request, results, has_more_results, total_hits);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SearchBuilderResponseOK))
			return false;

		SearchBuilderResponseOK other = (SearchBuilderResponseOK) obj;
		if (!Objects.equals(getSimpleSearchRequest(), other.getSimpleSearchRequest()))
			return false;

		if (!Objects.equals(getSimpleResults(), other.getSimpleResults()))
			return false;

		if (getTotalHits() != other.getTotalHits())
			return false;

		if (getSimpleHasMoreResults() != other.getSimpleHasMoreResults())
			return false;

		return true;
	}

}
