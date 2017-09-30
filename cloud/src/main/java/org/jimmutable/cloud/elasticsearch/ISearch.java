package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;

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
}
