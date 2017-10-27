package org.jimmutable.cloud.elasticsearch;

import java.util.List;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.serialization.FieldName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

/**
 * Any class that implements Search should have the following implementations
 * 
 * @author trevorbox
 *
 */
public interface ISearch
{

	public boolean shutdownDocumentUpsertThreadPool(int seconds);

	public boolean upsertDocumentAsync(Indexable object);

	public JSONServletResponse search(IndexDefinition index, StandardSearchRequest request);

	public boolean indexExists(IndexDefinition index);

	public boolean indexExists(SearchIndexDefinition index);

	public boolean indexProperlyConfigured(SearchIndexDefinition index);

	public boolean upsertIndex(SearchIndexDefinition index);

	public boolean writeAllToCSV(IndexDefinition index, String query_string, List<FieldName> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors);
}
