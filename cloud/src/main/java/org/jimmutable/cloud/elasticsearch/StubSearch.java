package org.jimmutable.cloud.elasticsearch;

import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.core.objects.common.Kind;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

public class StubSearch implements ISearch
{

	@Override
	public boolean shutdownDocumentUpsertThreadPool(int seconds)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsertDocumentAsync(Indexable object)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public JSONServletResponse search(IndexDefinition index, StandardSearchRequest request)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}
	
	@Override
	public List<OneSearchResultWithTyping> search(IndexDefinition index, StandardSearchRequest request, List<OneSearchResultWithTyping> default_value)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean indexExists(IndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean indexExists(SearchIndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean indexProperlyConfigured(SearchIndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsertIndex(SearchIndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean writeAllToCSV(IndexDefinition index, String query_string, List<SearchFieldId> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean deleteDocument(IndexDefinition index, SearchDocumentId document_id)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public SearchRequestBuilder getBuilder(IndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsertDocument(Indexable object)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean upsertQuietDocumentAsync(Indexable object)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

	@Override
	public boolean reindex(IStorage storage, Kind... kind)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}
	
	@Override
	public boolean putAllFieldMappings(SearchIndexDefinition index)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

}
