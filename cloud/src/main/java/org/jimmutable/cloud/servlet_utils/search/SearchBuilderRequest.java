package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.jimmutable.core.utils.Validator;

public class SearchBuilderRequest
{

	private SearchRequestBuilder builder; // required
	private int max_results; // required
	private int start_results_after; // required

	static public int DEFAULT_MAX_RESULTS = 100;
	static public int DEFAULT_START_RESULTS_AFTER = 0;

	public SearchBuilderRequest(SearchRequestBuilder builder, int max_results, int start_results_after)
	{
		this.builder = builder;
		this.max_results = max_results;
		this.start_results_after = start_results_after;
		validate();
	}

	public SearchBuilderRequest(SearchRequestBuilder builder)
	{
		this(builder, DEFAULT_MAX_RESULTS, DEFAULT_START_RESULTS_AFTER);
	}



	public SearchRequestBuilder getSimpleBuilder()
	{
		return builder;
	}

	public int getSimpleMaxResults()
	{
		return max_results;
	}

	public int getSimpleStartResultsAfter()
	{
		return start_results_after;
	}

	public void validate()
	{
		Validator.notNull(builder);

	}

	@Override
	public int hashCode()
	{

		return Objects.hash(getSimpleBuilder(), getSimpleMaxResults(), getSimpleStartResultsAfter());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SearchBuilderRequest))
			return false;

		SearchBuilderRequest other = (SearchBuilderRequest) obj;
		if (!Objects.equals(getSimpleBuilder(), other.getSimpleBuilder()))
			return false;
		if (getSimpleMaxResults() != other.getSimpleMaxResults())
			return false;
		if (getSimpleStartResultsAfter() != other.getSimpleStartResultsAfter())
			return false;

		return true;
	}

}
