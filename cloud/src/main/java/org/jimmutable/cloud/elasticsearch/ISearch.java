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

/**
 * Any class that implements Search should have the following implementations
 * 
 * @author trevorbox
 *
 */
public interface ISearch
{

	/**
	 * Gracefully shutdown the running threads. Note: the TransportClient should be
	 * closed where instantiated. This is not handles by this.
	 * 
	 * @return boolean if shutdown correctly or not
	 */
	public boolean shutdownDocumentUpsertThreadPool(int seconds);

	/**
	 * Upsert a document to a search index asynchronously
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertDocumentAsync(Indexable object);

	/**
	 * Upsert a document to a search index asynchronously AND without logging to
	 * INFO
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertQuietDocumentAsync(Indexable object);

	/**
	 * Upsert a document to a search index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertDocument(Indexable object);

	/**
	 * Search an index with a query string.
	 * 
	 * @see <a href=
	 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html">query-dsl-query-string-query</a>
	 * 
	 * @param index
	 *            The IndexDefinition
	 * @param request
	 *            The StandardSearchRequest
	 * @return JSONServletResponse
	 */
	public JSONServletResponse search(IndexDefinition index, StandardSearchRequest request);

	public List<OneSearchResultWithTyping> search(IndexDefinition index, StandardSearchRequest request, List<OneSearchResultWithTyping> default_value);

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            IndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(IndexDefinition index);

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(SearchIndexDefinition index);

	/**
	 * An index is properly configured if it exists and its field names and
	 * datatypes match
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index is properly configured or not
	 */
	public boolean indexProperlyConfigured(SearchIndexDefinition index);

	/**
	 * A re-index operation syncs a Storable and Indexable Kinds data from
	 * Storage into Search. By the end of the operation Search for a Kind should
	 * be as identical to current Storage for a Kind as possible.
	 * 
	 * @param IStorage
	 *            The implementation of IStorage that is being used
	 * 
	 * @param Kind
	 *            The kind to attempt to re-index on
	 * @return boolean if the index was fully successfully re-indexed
	 */
	public boolean reindex(IStorage storage, Kind... kinds);
	
	/**
	 * Upsert if the index doesn't exist or is not properly configured already
	 * 
	 * BE CAREFUL!!!
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the upsert was successful or not
	 */
	public boolean upsertIndex(SearchIndexDefinition index);

	/**
	 * Runs a search and writes the results to the passed in ICsvListWriter.
	 * 
	 * @param index
	 *            The IndexDefinition
	 * @param query_string
	 *            String
	 * @param sorted_header
	 *            List<SearchFieldId>
	 * @param list_writer
	 *            ICsvListWriter
	 * @param cell_processors
	 *            CellProcessor[]
	 * @return boolean if successful or not
	 */
	public boolean writeAllToCSV(IndexDefinition index, String query_string, List<SearchFieldId> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors);

	/**
	 * Delete a document within an index
	 * 
	 * @param index
	 * @param document_id
	 * @return
	 */
	public boolean deleteDocument(IndexDefinition index, SearchDocumentId document_id);

	/**
	 * Useful to call custom searches from the builder class when simple text search
	 * is not enough. NOTE: Be sure to set the TYPE. For example
	 * builder.setTypes(ElasticSearch.ELASTICSEARCH_DEFAULT_TYPE); This method will
	 * not set anything for you in the builder. </br>
	 * Example: </br>
	 * SearchRequestBuilder builder =
	 * CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().getBuilder(index_name);</br>
	 * builder.setTypes(ElasticSearch.ELASTICSEARCH_DEFAULT_TYPE);</br>
	 * builder.setSize(size); builder.set String my_field_name =
	 * "the_field_name";</br>
	 * //get the max value from a field</br>
	 * builder.addAggregation(AggregationBuilders.max(my_field_name)); </br>
	 * //order the results ascending by field </br>
	 * builder.addSort(SortBuilders.fieldSort(my_field_name).order(SortOrder.ASC));</br>
	 * builder.setQuery(QueryBuilders.queryStringQuery("search string"));</br>
	 * </br>
	 * 
	 * 
	 * @param index
	 *            The IndexDefinition
	 * @return SearchRequestBuilder
	 */
	public SearchRequestBuilder getBuilder(IndexDefinition index);

	/**
	 * Deletes an entire index
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean - true if successfully deleted, else false
	 */
	public boolean deleteIndex(SearchIndexDefinition index);

	/**
	 * Puts all field mappings into an existing index. If the index doesn't already
	 * exist or a field name with a different type already exists the operation will
	 * fail.
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return if successful or not
	 */
	public boolean putAllFieldMappings(SearchIndexDefinition index);

}
