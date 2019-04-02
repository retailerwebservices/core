package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.List;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class SearchResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.SearchResponseOK");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.StandardObject FIELD_SEARCH_REQUEST = new FieldDefinition.StandardObject("search_request", new StandardSearchRequest(""));
	static public final FieldDefinition.Collection FIELD_RESULTS = new FieldDefinition.Collection("results", new ArrayList<OneSearchResultWithTyping>());

	static public final int HTTP_STATUS_CODE_OK = 200;

	private StandardSearchRequest search_request; // required
	private List<OneSearchResultWithTyping> results; // required

	public SearchResponseOK(StandardSearchRequest search_request, List<OneSearchResultWithTyping> results)
	{
		this.search_request = search_request;
		this.results = results;
		complete();
	}

	public SearchResponseOK(ObjectParseTree t)
	{
		this.search_request = (StandardSearchRequest) t.getObject(FIELD_SEARCH_REQUEST);
		this.results = t.getCollection(FIELD_RESULTS, new ArrayList<OneSearchResultWithTyping>(), ReadAs.OBJECT, OnError.SKIP);
	}

	@Override
	public int compareTo(JSONServletResponse obj)
	{
		if (!(obj instanceof SearchResponseOK))
			return 0;

		SearchResponseOK other = (SearchResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleSearchRequest(), other.getSimpleSearchRequest());
		ret = Comparison.continueCompare(ret, getSimpleResults().size(), other.getSimpleResults().size());
		return ret;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeObject(FIELD_SEARCH_REQUEST, getSimpleSearchRequest());
		writer.writeCollection(FIELD_RESULTS, getSimpleResults(), WriteAs.OBJECT);
	}

	public StandardSearchRequest getSimpleSearchRequest()
	{
		return search_request;
	}

	public List<OneSearchResultWithTyping> getSimpleResults()
	{
		return results;
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
//		if (start_of_next_page_of_results == 0)
//		{
//			start_of_next_page_of_results = -1;
//		}
//
//		if (start_of_previous_page_of_results == 0)
//		{
//			start_of_previous_page_of_results = -1;
//		}

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((search_request == null) ? 0 : search_request.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		SearchResponseOK other = (SearchResponseOK) obj;
		if ( results == null )
		{
			if ( other.results != null )
				return false;
		}
		else if ( !results.equals(other.results) )
			return false;
		if ( search_request == null )
		{
			if ( other.search_request != null )
				return false;
		}
		else if ( !search_request.equals(other.search_request) )
			return false;
		return true;
	}
}
