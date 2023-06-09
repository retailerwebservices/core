package org.jimmutable.cloud.elasticsearch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.core.objects.common.Kind;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

/**
 * Any class that implements Search should have the following implementations
 * 
 * @author trevorbox
 *
 */
public interface ISearch
{

	/**
	 * Blocks until all tasks have completed execution after a shutdown request, or
	 * the timeout occurs, or the current thread is interrupted, whichever happens
	 * first.
	 * 
	 * After timeout is reached shutdown now is called.
	 * 
	 * @param timeout_seconds
	 *            int seconds to await graceful termination of threads
	 * 
	 * @return boolean if shutdown correctly or not
	 * 
	 */
	public boolean shutdownDocumentUpsertThreadPool( int timeout_seconds );

	/**
	 * Upsert a document to a search index asynchronously
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertDocumentAsync( Indexable object );

	/**
	 * Upsert a document to a search index asynchronously AND without logging to
	 * INFO
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertQuietDocumentAsync( Indexable object );

	/**
	 * Upsert a document to a search index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	public boolean upsertDocument( Indexable object );
	
	/**
	 * Upsert documents to a search index
	 * 
	 * @param object
	 *            The set of Indexable objects
	 * @return boolean If successful or not
	 */
	public boolean upsertDocuments( Set<Indexable> object );
	
	
	/**
	 * Upsert documents to a search index
	 * 
	 * @param object
	 *            The set of Indexable objects
	 * @return boolean If successful or not
	 */
	public boolean upsertDocumentsImmediate( Set<Indexable> object );

	/**
	 * Uses ElasticSearch's {@link org.elasticsearch.action.search.SearchRequest} to
	 * prepare and send out a search. This method is useful for complex queries,
	 * that require more than a simple text search.
	 * 
	 * @param index
	 * @param request
	 *            The request to send directly to ElasticSearch. For examples on
	 *            creating the request, look at
	 *            {@link ISearch#search(IndexDefinition index, SearchRequest request)}
	 * @return SearchResponse with all matching searches
	 */
	public SearchResponse<Indexable> searchRaw( SearchRequest request );

	public List<OneSearchResultWithTyping> search( IndexDefinition index, StandardSearchRequest request, List<OneSearchResultWithTyping> default_value );

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            IndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists( IndexDefinition index );

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists( SearchIndexDefinition index );

	/**
	 * An index is properly configured if it exists and its field names and
	 * datatypes match
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index is properly configured or not
	 */
	public boolean indexProperlyConfigured( SearchIndexDefinition index );

	/**
	 * A re-index operation syncs a Storable and Indexable Kinds data from Storage
	 * into Search. By the end of the operation Search for a Kind should be as
	 * identical to current Storage for a Kind as possible.
	 * 
	 * @param IStorage
	 *            The implementation of IStorage that is being used
	 * 
	 * @param Kind
	 *            The kind to attempt to re-index on
	 * @return boolean if the index was fully successfully re-indexed
	 */
	public boolean reindex( IStorage storage, Kind... kinds );

	/**
	 * Upsert if the index doesn't exist or is not properly configured already
	 * 
	 * BE CAREFUL!!!
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the upsert was successful or not
	 */
	public boolean upsertIndex( SearchIndexDefinition index );

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
	public boolean writeAllToCSV( IndexDefinition index, String query_string, List<SearchFieldId> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors );

	/**
	 * Delete a document within an index
	 * 
	 * @param index
	 * @param document_id
	 * @return
	 */
	public boolean deleteDocument( IndexDefinition index, SearchDocumentId document_id );

	/**
	 * Puts all field mappings into an existing index. If the index doesn't already
	 * exist or a field name with a different type already exists the operation will
	 * fail.
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return if successful or not
	 */
	public boolean putAllFieldMappings( SearchIndexDefinition index );

	public ScrollResponse<Indexable> searchScrollRaw( ScrollRequest request );

	boolean clearScrollRaw( ClearScrollRequest request );

	public default String normalizeReturnedValue( Object o )
	{
		if ( o instanceof Collection<?> || o instanceof Map<?, ?> )
		{
			//For Readability sake, we are removing the beginning and end brackets from collections 
			return o.toString().replaceFirst("^\\[", "").replaceAll("\\]$", "");
		}
		return o.toString();

	}

}
