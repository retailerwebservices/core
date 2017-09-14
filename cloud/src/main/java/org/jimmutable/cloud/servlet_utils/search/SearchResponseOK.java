package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * SearchResponseOK Used to indicate successful response to search request and
 * pass results
 * 
 * @author Preston McCumber
 */
public class SearchResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.SearchResponseOK");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	// CODE REVIEW: Andrew has a new auto fomatter that won't break these lines at
	// such a narrow width. Get each of these statemetns on one line

	static public final FieldDefinition.StandardObject FIELD_SEARCH_REQUEST = new FieldDefinition.StandardObject("search_request", new StandardSearchRequest(""));
	static public final FieldDefinition.Collection FIELD_RESULTS = new FieldDefinition.Collection("results", new ArrayList<OneSearchResult>());
	static public final FieldDefinition.Integer FIELD_FIRST_RESULT_IDX = new FieldDefinition.Integer("first_result_idx", 0);
	static public final FieldDefinition.Boolean FIELD_HAS_MORE_RESULTS = new FieldDefinition.Boolean("has_more_results", false);
	static public final FieldDefinition.Boolean FIELD_HAS_PREVIOUS_RESULTS = new FieldDefinition.Boolean("has_previous_results", false);
	static public final FieldDefinition.Integer FIELD_START_OF_NEXT_PAGE_OF_RESULTS = new FieldDefinition.Integer("start_of_next_page_of_results", -1);
	static public final FieldDefinition.Integer FIELD_START_OF_PREVIOUS_PAGE_OF_RESULTS = new FieldDefinition.Integer("start_of_previous_page_of_results", -1);

	static public final int HTTP_STATUS_CODE_OK = 200;

	private StandardSearchRequest search_request; // required
	private List<OneSearchResult> results; // required
	private int first_result_idx; // required
	private boolean has_more_results; // required
	private boolean has_previous_results; // required
	private int start_of_next_page_of_results; // optional
	private int start_of_previous_page_of_results; // optional

	public SearchResponseOK( StandardSearchRequest search_request, List<OneSearchResult> results, int first_result_idx, boolean has_more_results, boolean has_previous_results, int start_of_next_page_of_results, int start_of_previous_page_of_results )
	{
		this.search_request = search_request;
		this.results = results;
		this.first_result_idx = first_result_idx;
		this.has_more_results = has_more_results;
		this.has_previous_results = has_previous_results;
		this.start_of_next_page_of_results = start_of_next_page_of_results;
		this.start_of_previous_page_of_results = start_of_previous_page_of_results;
		complete();
	}

	public SearchResponseOK( ObjectParseTree t )
	{
		this.search_request = (StandardSearchRequest) t.getObject(FIELD_SEARCH_REQUEST);
		this.results = t.getCollection(FIELD_RESULTS, new ArrayList<OneSearchResult>(), ReadAs.OBJECT, OnError.SKIP);
		this.first_result_idx = t.getInt(FIELD_FIRST_RESULT_IDX);
		this.has_more_results = t.getBoolean(FIELD_HAS_MORE_RESULTS);
		this.has_previous_results = t.getBoolean(FIELD_HAS_PREVIOUS_RESULTS);
		this.start_of_next_page_of_results = t.getInt(FIELD_START_OF_NEXT_PAGE_OF_RESULTS);
		this.start_of_previous_page_of_results = t.getInt(FIELD_START_OF_PREVIOUS_PAGE_OF_RESULTS);
	}

	@Override
	public int compareTo( JSONServletResponse obj )
	{
		if ( !(obj instanceof SearchResponseOK) )
			return 0;

		SearchResponseOK other = (SearchResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleSearchRequest(), other.getSimpleSearchRequest());
		ret = Comparison.continueCompare(ret, getSimpleResults().size(), other.getSimpleResults().size());
		ret = Comparison.continueCompare(ret, getSimpleFirstResultIdx(), other.getSimpleFirstResultIdx());
		ret = Comparison.continueCompare(ret, getSimpleHasMoreResults(), other.getSimpleHasMoreResults());
		ret = Comparison.continueCompare(ret, getSimpleHasPreviousResults(), other.getSimpleHasPreviousResults());
		ret = Comparison.continueCompare(ret, getSimpleStartOfNextPageOfResults(), other.getSimpleStartOfNextPageOfResults());
		ret = Comparison.continueCompare(ret, getSimpleStartOfPreviousPageOfResults(), other.getSimpleStartOfPreviousPageOfResults());

		return ret;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeObject(FIELD_SEARCH_REQUEST, getSimpleSearchRequest());
		writer.writeCollection(FIELD_RESULTS, getSimpleResults(), WriteAs.OBJECT);
		writer.writeInt(FIELD_FIRST_RESULT_IDX, getSimpleFirstResultIdx());
		writer.writeBoolean(FIELD_HAS_MORE_RESULTS, getSimpleHasMoreResults());
		writer.writeBoolean(FIELD_HAS_PREVIOUS_RESULTS, getSimpleHasPreviousResults());
		writer.writeInt(FIELD_START_OF_NEXT_PAGE_OF_RESULTS, getSimpleStartOfNextPageOfResults());
		writer.writeInt(FIELD_START_OF_PREVIOUS_PAGE_OF_RESULTS, getSimpleStartOfPreviousPageOfResults());

	}

	public StandardSearchRequest getSimpleSearchRequest()
	{
		return search_request;
	}

	public List<OneSearchResult> getSimpleResults()
	{
		return results;
	}

	public int getSimpleFirstResultIdx()
	{
		return first_result_idx;
	}

	public boolean getSimpleHasMoreResults()
	{
		return has_more_results;
	}

	public boolean getSimpleHasPreviousResults()
	{
		return has_previous_results;
	}

	public int getSimpleStartOfNextPageOfResults()
	{
		return start_of_next_page_of_results;
	}

	public int getSimpleStartOfPreviousPageOfResults()
	{
		return start_of_previous_page_of_results;
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_OK;
	}

	@Override
	public void freeze()
	{
	}

	@Override
	public void normalize()
	{
		if ( start_of_next_page_of_results == 0 )
		{
			start_of_next_page_of_results = -1;
		}

		if ( start_of_previous_page_of_results == 0 )
		{
			start_of_previous_page_of_results = -1;
		}
	}

	@Override
	public void validate()
	{
		Validator.notNull(search_request);
		Validator.notNull(results);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleHTTPResponseCode(), getSimpleSearchRequest(), getSimpleFirstResultIdx(), getSimpleHasMoreResults(), getSimpleHasPreviousResults(), getSimpleStartOfNextPageOfResults(), getSimpleStartOfPreviousPageOfResults());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof SearchResponseOK) )
			return false;

		SearchResponseOK other = (SearchResponseOK) obj;
		if ( !Objects.equals(getSimpleSearchRequest(), other.getSimpleSearchRequest()) )
			return false;

		if ( !Objects.equals(getSimpleResults(), other.getSimpleResults()) )
			return false;

		if ( getSimpleFirstResultIdx() != other.getSimpleFirstResultIdx() )
			return false;

		if ( getSimpleHasMoreResults() != other.getSimpleHasMoreResults() )
			return false;

		if ( getSimpleHasPreviousResults() != other.getSimpleHasPreviousResults() )
			return false;

		if ( getSimpleStartOfNextPageOfResults() != other.getSimpleStartOfNextPageOfResults() )
			return false;

		if ( getSimpleStartOfPreviousPageOfResults() != other.getSimpleStartOfPreviousPageOfResults() )
			return false;

		return true;
	}

}
