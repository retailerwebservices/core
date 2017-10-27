package org.jimmutable.cloud.elasticsearch;

import java.util.List;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.serialization.FieldName;
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
	public boolean writeAllToCSV(IndexDefinition index, String query_string, List<FieldName> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors)
	{
		throw new RuntimeException("This should have never been called for unit testing, use a different implementation for integration testing!");
	}

}