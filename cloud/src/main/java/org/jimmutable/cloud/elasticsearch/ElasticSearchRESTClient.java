package org.jimmutable.cloud.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.elasticsearch.ElasticSearchTransportClient.GenericStorableAndIndexable;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.FieldName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO Once we're ready to fully deprecate the TransportClient used in Dev, the
 * methods in this class can replace the methods in the standard ElasticSearch
 * 
 * @author salvador.salazar
 * @author avery.gonzales
 */
public class ElasticSearchRESTClient implements ISearch
{
	private static final Logger logger = LogManager.getLogger(ElasticSearchRESTClient.class);

	/**
	 * The RestClient replaces the TransportClient that we used previously. The
	 * RestClient is more robust, and it doesn't strictly require us to have the
	 * same version on client/server.
	 */
	protected volatile RestHighLevelClient high_level_rest_client;

	//This value is the Base64 encoded user:pass. If someone changes the authentication this will break.
	private final BasicHeader HEADER_BASIC_AUTH = new BasicHeader("Authorization", "Basic ZWxhc3RpYzpnVWM2clZNa1Z0MUdEeXBneHV4ZTdaalI=");
	private final BasicHeader HEADER_CONTENT_TYPE = new BasicHeader("Content-Type", "application/json");

	private String PRODUCTION_ELASTICSEARCH_HOST = "f8bfe258266ee6bd44cece0dde4326d5.us-west-2.aws.found.io";
	private int PRODUCTION_ELASTICSEARCH_PORT = 9243;

	private String DEV_ELASTICSEARCH_HOST = ElasticSearchEndpoint.CURRENT.getSimpleHost();
	//This is the default dev REST port, there isn't a way to get this through the API as far as I can tell for now
	private int DEV_ELASTICSEARCH_PORT = 9200;

	public ElasticSearchRESTClient()
	{
		// Once we deprecate the TransportClient in dev, we can simply add a
		// RestClientBuilder in construction, that will swap between dev and prod
		EnvironmentType type = CloudExecutionEnvironment.getEnvironmentTypeFromSystemProperty(null);
		if ( type == EnvironmentType.PRODUCTION )
		{
			RestClientBuilder builder = RestClient.builder(new HttpHost(PRODUCTION_ELASTICSEARCH_HOST, PRODUCTION_ELASTICSEARCH_PORT, "https")).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback()
			{
				@Override
				public RequestConfig.Builder customizeRequestConfig( RequestConfig.Builder requestConfigBuilder )
				{
					return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000);
				}
			});
			builder.setDefaultHeaders(new Header[] { HEADER_BASIC_AUTH, HEADER_CONTENT_TYPE }).setMaxRetryTimeoutMillis(60000);
			high_level_rest_client = new RestHighLevelClient(builder);
		}
		else
		{
			RestClientBuilder builder = RestClient.builder(new HttpHost(DEV_ELASTICSEARCH_HOST, DEV_ELASTICSEARCH_PORT, "http"));
			high_level_rest_client = new RestHighLevelClient(builder);
		}
	}

	public boolean upsertIndex( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.fatal("Cannot upsert a null Index");
			return false;
		}

		// if it exists and is not configured correctly delete and add
		if ( indexExists(index) )
		{
			logger.info(String.format("No upsert needed for index %s", index.getSimpleIndex().getSimpleValue()));
			return true;
		}
		else
		{
			// index is new
			return createIndex(index);
		}
	}

	private boolean createIndex( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.fatal("Cannot create a null Index");
			return false;
		}

		String timestamp_index_name = createTimestampIndex(index, null);
		if ( timestamp_index_name == null )
		{
			logger.fatal("Cannot create a timestamp Index for index " + index);
			return false;
		}

		try
		{
			IndicesAliasesRequest request = new IndicesAliasesRequest();

			AliasActions add_alias_action = new AliasActions(AliasActions.Type.ADD);
			add_alias_action.index(timestamp_index_name);
			add_alias_action.alias(index.getSimpleIndex().getSimpleValue());
			request.addAliasAction(add_alias_action);

			IndicesAliasesResponse indicesAliasesResponse = high_level_rest_client.indices().updateAliases(request);

			if ( !indicesAliasesResponse.isAcknowledged() )
			{
				logger.fatal(String.format("Alias addition not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		}
		catch ( Exception e )
		{
			logger.fatal("Alias creation failure for index " + index.getSimpleIndex().getSimpleValue(), e);
			return false;
		}

		return true;
	}

	/**
	 * This will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name).
	 * 
	 * @param index
	 *            The SearchIndexDefinition that the index will relate to
	 * @param default_value
	 *            value to return on failure
	 * @return the new unique index name on success, default_value on failure
	 */

	private String createTimestampIndex( SearchIndexDefinition index, String default_value )
	{
		if ( index == null )
		{
			logger.fatal("Cannot create a null Index");
			return default_value;
		}
		
		String index_name = index.getSimpleIndex().getSimpleValue() + "_" + System.currentTimeMillis();

		try
		{
			CreateIndexRequest request = new CreateIndexRequest(index_name).mapping(ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, ElasticSearchCommon.getMappingBuilder(index, null));
			CreateIndexResponse createResponse = high_level_rest_client.indices().create(request);

			if ( !createResponse.isAcknowledged() )
			{
				logger.fatal(String.format("Index Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return default_value;
			}

			return index_name;
		}
		catch ( Exception e )
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			//The indices can successfully be created but then has other issues. We will try to delete if so.
			if(!deleteWithRetry(index_name))
			{
				logger.log(Level.ERROR, String.format("Failed to delete possibly left over empty index %s", index_name));
			}
			return default_value;
		}
	}

	/**
	 * Delete a document within an index
	 * 
	 * @param index
	 * @param document_id
	 * @return
	 */
	@Override
	public boolean deleteDocument( IndexDefinition index, SearchDocumentId document_id )
	{
		if ( index == null || document_id == null )
		{
			logger.fatal("Null index or document id");
			return false;
		}

		try
		{
			String delete_request = "/" + index.getSimpleValue() + "/" + document_id.getTypeName().getSimpleName() + "/" + document_id.getSimpleValue();

			DeleteRequest del = new DeleteRequest(index.getSimpleValue(), ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_id.getSimpleValue()).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			DeleteResponse response = high_level_rest_client.delete(del);

			boolean successfully_deleted = response.getResult().equals(Result.DELETED);

			if ( !successfully_deleted )
			{
				logger.error("delete unsuccessful for: " + delete_request);
			}
			else
			{
				logger.info("successful delete for: " + delete_request);
			}

			return successfully_deleted;
		}
		catch ( Exception e )
		{
			logger.error(e);
			return false;
		}
	}
	
	private boolean deleteWithRetry( String index_name )
	{
		int i = 2;
		while ( i < 7 )
		{
			if ( !deleteIndex(index_name, i) )
			{
				logger.error("Could not delete index " + index_name + " with timeout set to " + i + " minutes. Retrying deletion.");
			}
			else
			{
				return true;
			}
			
			i++;
		}
		
		return false;
	}

	/**
	 * Deletes an entire index
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean - true if successfully deleted, else false
	 */

	private boolean deleteIndex( String index_name,  int timeout_minutes)
	{
		if ( index_name == null )
		{
			logger.fatal("Cannot delete a null Index");
			return false;
		}

		try
		{
			DeleteIndexRequest del = new DeleteIndexRequest(index_name);
			del.timeout(TimeValue.timeValueMinutes(timeout_minutes));
			DeleteIndexResponse response = high_level_rest_client.indices().delete(del);

			if ( response.isAcknowledged() )
			{
				logger.info("successfully deleted: " + index_name);
				return true;
			}
			else
			{
				logger.info("couldn't delete index: " + index_name);
				return false;
			}
		}
		catch ( Exception e )
		{
			logger.error("Exception thrown deleting index: " + index_name, e);
			return false;
		}
	}

	/**
	 * Upsert a document to a search index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocument( Indexable object )
	{
		if ( object == null )
		{
			logger.error("Null object!");
			return false;
		}

		try
		{
			SearchDocumentWriter writer = new SearchDocumentWriter();
			object.writeSearchDocument(writer);
			Map<String, Object> data = writer.getSimpleFieldsMap();

			String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
			String document_name = object.getSimpleSearchDocumentId().getSimpleValue();

			IndexRequest request = new IndexRequest(index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name).source(data).setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
			IndexResponse response = high_level_rest_client.index(request);

			Level level;
			switch ( response.getResult() )
			{
			case CREATED:
				level = Level.DEBUG;
				break;
			case UPDATED:
				level = Level.DEBUG;
				break;
			default:
				level = Level.FATAL;
				break;
			}

			logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name, data));

			boolean success = response.getResult().equals(Result.CREATED) || response.getResult().equals(Result.UPDATED);
			return success;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public SearchRequestBuilder getBuilder( IndexDefinition index )
	{
		throw new UnsupportedOperationException("The ElasticSearch REST client doesn't support SearchRequestBuilder in it's implementation. Please use @");
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            IndexDefinition
	 * @return boolean if the index exists or not
	 */
	@Override
	public boolean indexExists( IndexDefinition index )
	{
		if ( index == null )
		{
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}
		try
		{
			Response resp = high_level_rest_client.getLowLevelClient().performRequest("GET", "/" + index.getSimpleValue());

			if ( resp.getStatusLine().getStatusCode() != 200 )
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch ( Exception e )
		{
			// index exists checks throw an exception when not found. It's a pretty dumb way
			// to handle exists checks, but I couldn't find a quieter way to do it with
			// the high level client 6.2.4
			return false;
		}
	}

	/**
	 * This will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name). Once it has found all indices that
	 * reference the alias it will prepare to atomically remove all the old
	 * references and attach the single new index reference to the alias. After that
	 * is successfully done, this will remove all indices that no longer have a
	 * relation to the alias.
	 * 
	 * @param definition
	 *            The definition of the Kind that is being reindexed
	 * @param index_name
	 *            The unique index name that data was upserted to
	 * @return is_success
	 */

	private boolean updateAlias( SearchIndexDefinition definition, String index_name )
	{

		Set<String> old_indices = getCurrentIndiciesFromAliasName(definition.getSimpleIndex().getSimpleValue());

		// Has to be atomic so if it fails we don't add a new alias
		try
		{
			IndicesAliasesRequest request = new IndicesAliasesRequest();

			AliasActions add_alias_action = new AliasActions(AliasActions.Type.ADD);
			add_alias_action.index(index_name);
			add_alias_action.alias(definition.getSimpleIndex().getSimpleValue());
			request.addAliasAction(add_alias_action);

			// deletes old indices for (String index : indices_to_delete) { AliasActions
			for ( String old_index : old_indices )
			{
				AliasActions delete_alias_action = new AliasActions(AliasActions.Type.REMOVE_INDEX);
				delete_alias_action.index(old_index);
				request.addAliasAction(delete_alias_action);
			}

			IndicesAliasesResponse indicesAliasesResponse = high_level_rest_client.indices().updateAliases(request);

			if ( !indicesAliasesResponse.isAcknowledged() )
			{
				logger.fatal(String.format("Alias addition not acknowledged for index %s", index_name));
				return false;
			}

		}
		catch ( Exception e )
		{
			logger.error("Alias addition and removal failed", e);
			return false;
		}

		return true;
	}

	/**
	 * This will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name).
	 * 
	 * @param alias_name
	 *            The alias_name that all the indices are related to
	 * @return the set of indices that have relation to the alias
	 */
	private Set<String> getCurrentIndiciesFromAliasName( String alias_name )
	{
		Set<String> all_indicies_with_alias = new HashSet<>();
		try
		{
			Response resp = high_level_rest_client.getLowLevelClient().performRequest("GET", "/_alias/" + alias_name);
			String response_body = EntityUtils.toString(resp.getEntity());

			JsonNode node = new ObjectMapper().readTree(response_body);

			Iterator<String> iterator = node.fieldNames();

			while ( iterator.hasNext() )
			{
				String index_using_alias = iterator.next();
				all_indicies_with_alias.add(index_using_alias);
			}
		}
		catch ( Exception e )
		{
			logger.error("Unable to parse get request for aliases", e);
		}

		return all_indicies_with_alias;
	}

	/**
	 * This will scan all of Storage for the Kind passed in and for each item found
	 * it will attempt to upsert its matching search document.
	 * 
	 * @return true on success
	 */

	private boolean syncSearchAndStorage( Kind kind, String index_name )
	{
		//This handles the initial scanning of storage to get all the requests. It blocks so once successfully finished we will have all of the requests ready for a bulk request.
		BulkUpsertScanHandler scan_handler = new BulkUpsertScanHandler(index_name);
		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().scan(kind, scan_handler, 10) )
		{
			logger.warn("Storage Scanner for Kind " + kind + " was unable to successfully run. This Kind may not be fully re-indexed or there may currently not be any entries of Kind in Storage. The index will not be swapped on the alias.");
			return false;
		}

		
		try
		{
			
			if(scan_handler.getSimpleBulkRequest().requests().isEmpty())
			{
				logger.info("No documents were found in Storage for index " + index_name);
				return false;
			}
			
			BulkResponse bulk_response = high_level_rest_client.bulk(scan_handler.getSimpleBulkRequest());
			if(bulk_response.hasFailures())
			{
				logger.error("Failure for full successful bulk upsert.");
				for ( BulkItemResponse bulkItemResponse : bulk_response )
				{
					if ( bulkItemResponse.isFailed() )
					{
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
						logger.error("Failure on bulk upsert occurred with index " + index_name + " id " + bulkItemResponse.getId() + ". Failure msg " + failure.getMessage());
					}
				}
				
				return false;
			}
		}
		catch ( Exception e )
		{
			logger.log(Level.FATAL, String.format("Failed to execute bulk reindex upsert for index %s", index_name), e);
			return false;
		}
		
		return true;
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	@Override
	public boolean indexExists( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}

		return indexExists(index.getSimpleIndex());
	}

	@Override
	public boolean indexProperlyConfigured( SearchIndexDefinition index )
	{

		if ( index == null )
		{
			return false;
		}

		if ( indexExists(index) )
		{

			// compare the expected index fields to the actual index fields
			Map<String, String> expected = new HashMap<String, String>();
			index.getSimpleFields().forEach(fields ->
			{
				expected.put(fields.getSimpleFieldName().getSimpleName(), fields.getSimpleType().getSimpleSearchType());
			});

			try
			{
				Response resp = high_level_rest_client.getLowLevelClient().performRequest("GET", "/" + index.getSimpleIndex().getSimpleValue());
				String response_body = EntityUtils.toString(resp.getEntity());

				JsonNode node = new ObjectMapper().readTree(response_body).findValue("mappings").findValue("default").findValue("properties");

				Map<String, String> actual = new HashMap<String, String>();

				node.fields().forEachRemaining(fieldMapping ->
				{
					if ( !fieldMapping.getKey().contains(ElasticSearchCommon.SORT_FIELD_NAME_JIMMUTABLE) ) // Skip our keyword fields
					{
						actual.put(fieldMapping.getKey(), fieldMapping.getValue().get("type").asText());
					}
				});

				if ( !expected.equals(actual) )
				{
					logger.info(String.format("Index: %s not properly configured", index.getSimpleIndex().getSimpleValue()));
					logger.info(String.format("Expected fields=%s", expected));
					logger.info(String.format("Actual   fields=%s", actual));
					return false;
				}

				return true;

			}
			catch ( Exception e )
			{
				logger.log(Level.FATAL, String.format("Failed to get the index mapping for index %s", index.getSimpleIndex().getSimpleValue()), e);
			}
		}

		return false;
	}

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
	@Override
	public JSONServletResponse search( IndexDefinition index, StandardSearchRequest request )
	{
		if ( index == null || request == null )
		{
			return new SearchResponseError(request, "Null parameter(s)!");
		}

		try
		{
			String index_name = index.getSimpleValue();
			int from = request.getSimpleStartResultsAfter();
			int size = request.getSimpleMaxResults();

			SearchSourceBuilder query_builder = new SearchSourceBuilder().from(from).size(size).query(QueryBuilders.queryStringQuery(request.getSimpleQueryString()));
			// Sorting
			for ( SortBy sort_by : request.getSimpleSort().getSimpleSortOrder() )
			{
				FieldSortBuilder sort_builder = ElasticSearchCommon.getSort(sort_by, null);
				if ( sort_builder == null )
					continue;

				query_builder.sort(sort_builder);
			}
			
			SearchRequest ext_request = new SearchRequest(index_name).types(ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE).source(query_builder);

			SearchResponse response = high_level_rest_client.search(ext_request);

			List<OneSearchResult> results = new LinkedList<OneSearchResult>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, String> map = new HashMap<FieldName, String>();
				hit.getSourceAsMap().forEach(( k, v ) ->
				{
					map.put(new FieldName(k), v.toString());
				});
				results.add(new OneSearchResult(map));
			});

			long total_hits = response.getHits().totalHits;

			int next_page = from + size;
			int previous_page = (from - size) < 0 ? -1 : (from - size);

			boolean has_more_results = total_hits > next_page;

			boolean has_previous_results = from > 0;

			Level level;
			switch ( response.status() )
			{
			case OK:
				level = Level.INFO;
				break;
			default:
				level = Level.WARN;
				break;
			}

			SearchResponseOK ok = new SearchResponseOK(request, results, from, has_more_results, has_previous_results, next_page, previous_page, total_hits);

			logger.log(level, String.format("QUERY:%s INDEX:%s STATUS:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", ok.getSimpleSearchRequest().getSimpleQueryString(), index.getSimpleValue(), response.status(), results.size(), ok.getSimpleTotalHits(), ok.getSimpleSearchRequest().getSimpleMaxResults(), ok.getSimpleSearchRequest().getSimpleStartResultsAfter()));
			logger.trace(String.format("FIRST_RESULT_IDX:%s HAS_MORE_RESULTS:%s HAS_PREVIOUS_RESULTS:%s START_OF_NEXT_PAGE_OF_RESULTS:%s START_OF_PREVIOUS_PAGE_OF_RESULTS:%s", ok.getSimpleFirstResultIdx(), ok.getSimpleHasMoreResults(), ok.getSimpleHasMoreResults(), ok.getSimpleStartOfNextPageOfResults(), ok.getSimpleStartOfPreviousPageOfResults()));
			logger.trace(ok.getSimpleResults().toString());

			return ok;

		}
		catch ( Exception e )
		{
			if ( e.getCause() instanceof QueryShardException )
			{
				logger.warn(String.format("%s on index %s", e.getCause().getMessage(), index.getSimpleValue()));
				return new SearchResponseError(request, e.getCause().getMessage());
			}
			else
			{
				logger.error(String.format("Search failed for %s on index %s", request.getSimpleQueryString(), index.getSimpleValue()), e);
				return new SearchResponseError(request, e.getMessage());
			}
		}
	}

	@Override
	public List<OneSearchResultWithTyping> search( IndexDefinition index, StandardSearchRequest request, List<OneSearchResultWithTyping> default_value )
	{
		if ( index == null || request == null )
		{
			logger.warn(String.format("Search failed: Null parameter(s) for %s", request));
			return default_value;
		}

		try
		{
			String index_name = index.getSimpleValue();
			int from = request.getSimpleStartResultsAfter();
			int size = request.getSimpleMaxResults();

			SearchSourceBuilder query_builder = new SearchSourceBuilder().from(from).size(size).query(QueryBuilders.queryStringQuery(request.getSimpleQueryString()));

			// Sorting
			for ( SortBy sort_by : request.getSimpleSort().getSimpleSortOrder() )
			{
				FieldSortBuilder sort_builder = ElasticSearchCommon.getSort(sort_by, null);
				if ( sort_builder == null )
					continue;

				query_builder.sort(sort_builder);
			}

			SearchRequest ext_request = new SearchRequest(index_name).types(ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE).source(query_builder);

			SearchResponse response = high_level_rest_client.search(ext_request);

			List<OneSearchResultWithTyping> results = new LinkedList<OneSearchResultWithTyping>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, String[]> map = new TreeMap<FieldName, String[]>();
				hit.getSourceAsMap().forEach(( k, v ) ->
				{
					FieldName name = new FieldName(k);
					String[] array_val = map.get(name);

					String[] new_array_val = null;

					if ( v instanceof ArrayList<?> )
					{
						List<Object> array_as_list = (ArrayList<Object>) v;
						new_array_val = new String[array_as_list.size()];

						for ( int i = 0; i < array_as_list.size(); i++ )
						{
							new_array_val[i] = String.valueOf(array_as_list.get(i));
						}
					}
					else
					{
						if ( array_val == null )
							array_val = new String[0];
						new_array_val = new String[] { String.valueOf(v) };
					}

					if ( new_array_val != null )
						map.put(name, new_array_val);
				});
				results.add(new OneSearchResultWithTyping(map));
			});

			int next_page = from + size;

			boolean has_more_results = response.getHits().totalHits > next_page;

			boolean has_previous_results = from != 0;

			Level level;
			switch ( response.status() )
			{
			case OK:
				level = Level.INFO;
				break;
			default:
				level = Level.WARN;
				break;
			}

			logger.log(level, String.format("QUERY:%s INDEX:%s STATUS:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", request.getSimpleQueryString(), index.getSimpleValue(), response.status(), results.size(), response.getHits().totalHits, request.getSimpleMaxResults(), request.getSimpleStartResultsAfter()));
			logger.trace(String.format("SORT:%s FIRST_RESULT_IDX:%s HAS_MORE_RESULTS:%s HAS_PREVIOUS_RESULTS:%s START_OF_NEXT_PAGE_OF_RESULTS:%s START_OF_PREVIOUS_PAGE_OF_RESULTS:%s", request.getSimpleSort().getSimpleSortOrder().stream().map(e ->
			{
				return String.format("%s:%s", e.getSimpleField().getSimpleFieldName().getSimpleName(), e.getSimpleDirection().getSimpleCode());
			}).collect(Collectors.toList()), from, has_more_results, has_previous_results, next_page, from));
			logger.trace(results.toString());

			return results;

		}
		catch ( Exception e )
		{
			if ( e.getCause() instanceof QueryShardException )
			{
				logger.warn(String.format("%s on index %s", e.getCause().getMessage(), index.getSimpleValue()));
				return default_value;
			}
			else
			{
				logger.error(String.format("Search failed for %s on index %s", request.getSimpleQueryString(), index.getSimpleValue()), e);
				return default_value;
			}
		}
	}

	@Override
	public SearchResponse searchRaw( SearchRequest request )
	{
		if ( request == null )
		{
			throw new NullPointerException();
		}

		try
		{
			return high_level_rest_client.search(request);
		}
		catch ( Exception e )
		{
			logger.error("Failed to search!", e);
		}

		return null;
	}

	@Override
	public SearchResponse searchScrollRaw( SearchScrollRequest request )
	{
		if ( request == null )
		{
			throw new NullPointerException();
		}

		try
		{
			return high_level_rest_client.searchScroll(request);

		}
		catch ( Exception e )
		{
			logger.error("Failed to search scroll!", e);
		}

		return null;
	}

	@Override
	public boolean clearScrollRaw( ClearScrollRequest request )
	{
		if ( request == null )
		{
			throw new NullPointerException();
		}

		try
		{
			ClearScrollResponse resp_raw = high_level_rest_client.clearScroll(request);

			return resp_raw.isSucceeded();
		}
		catch ( Exception e )
		{
			logger.error("Failed to clear the scroll context!", e);
		}

		return false;
	}

	@Override
	public boolean reindex( IStorage storage, Kind... kinds )
	{
		if ( storage == null )
		{
			logger.error("Null storage passed in for re-indexing");
			return false;
		}

		if ( kinds == null )
		{
			logger.error("Null kinds passed in for re-indexing");
			return false;
		}

		for ( Kind kind : kinds )
		{
			SearchIndexDefinition index_definition = SearchSync.getSimpleAllRegisteredIndexableKindsMap().get(kind);
			if ( index_definition == null )
			{
				logger.error("Kind " + kind + " passed in for re-indexing is not registered with SearchSync.registerIndexableKind");
				return false;
			}

			String index_name = createTimestampIndex(index_definition, null);
			if ( index_name == null )
			{
				logger.error("Kind " + kind + " could not create a new index to reindex documents with");
				return false;
			}

			boolean success = syncSearchAndStorage(kind, index_name);
			if ( !success )
			{
				logger.error("Kind " + kind + " did not complete sync of storage and search, no swap made");
				if ( !deleteWithRetry(index_name) )
				{
					logger.error("Error removing index " + index_name + " on failure");
				}
				return false;
			}

			// Swap alias
			success = updateAlias(index_definition, index_name);
			if ( !success )
			{
				logger.error("Kind " + kind + " could not complete full alias swap");
				if ( !deleteWithRetry(index_name) )
				{
					logger.error("Error removing index " + index_name + " on failure.");
				}
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean writeAllToCSV( IndexDefinition index, String query_string, List<SearchFieldId> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors )
	{
		if ( index == null || query_string == null )
		{
			return false;
		}

		String index_name = index.getSimpleValue();

		SearchSourceBuilder query_builder = new SearchSourceBuilder().query(QueryBuilders.queryStringQuery(query_string)).sort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC).size(1_000);

		SearchRequest ext_request = new SearchRequest(index_name).types(ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE).scroll(TimeValue.timeValueMinutes(1)).source(query_builder);

		try
		{
			SearchResponse scrollResp = high_level_rest_client.search(ext_request);

			do
			{
				String[] document;
				for ( SearchHit hit : scrollResp.getHits().getHits() )
				{
					logger.info(scrollResp.getHits().totalHits);
					document = new String[sorted_header.size()];

					Map<String, Object> resultMap = hit.getSourceAsMap();

					for ( int i = 0; i < sorted_header.size(); i++ )
					{
						if ( resultMap.containsKey(sorted_header.get(i).getSimpleValue()) )
						{
							document[i] = resultMap.get(sorted_header.get(i).getSimpleValue()).toString();
						}
					}

					try
					{
						list_writer.write(Arrays.asList(document), cell_processors);
					}
					catch ( IOException e )
					{
						logger.error(e);
						return false;
					}
				}

				scrollResp.scrollId(scrollResp.getScrollId());
			}
			while ( scrollResp.getHits().getHits().length != 0 ); // Zero hits mark the end of the scroll and the
																	// while
																	// loop.
			return true;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This handles checking an individual file in Storage. It will check that the
	 * item is able to be deserialized as a Storable and Indexable. If it is we will
	 * set our Kind's Indexable to match that of the Object deserialized if not yet
	 * set. Then attempt to upsert the single Object's search document into Search.
	 */
	private class BulkUpsertScanHandler implements StorageKeyHandler
	{
		private String index_name;
		private BulkRequest requests;
		
		private BulkUpsertScanHandler( String index_name )
		{
			this.index_name = index_name;
			this.requests = new BulkRequest(); 
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void handle( StorageKey key )
		{
			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null);

			GenericStorableAndIndexable<?> obj = null;
			try
			{
				obj = new GenericStorableAndIndexable(bytes);
			}
			catch ( Exception e )
			{
				logger.error("This object from StorageKey " + key + " was unable to be deserialized as a Storable and Indexable object...", e);
				return;
			}

			SearchDocumentWriter writer = new SearchDocumentWriter();

			Indexable indexable = (Indexable) obj.getObject();
			indexable.writeSearchDocument(writer);
			Map<String, Object> data = writer.getSimpleFieldsMap();

			// Not async so it's only finished once the whole scan does so
			try
			{
				String document_name = indexable.getSimpleSearchDocumentId().getSimpleValue();
				
				IndexRequest request = new IndexRequest(index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name).source(data);
				requests.add(request);
			}
			catch ( Exception e )
			{
				logger.log(Level.FATAL, String.format("Failure during operation of Document id:%s on Index:%s", indexable.getSimpleSearchDocumentId().getSimpleValue(), indexable.getSimpleSearchIndexDefinition().getSimpleValue()), e);
			}
		}
		
		private BulkRequest getSimpleBulkRequest()
		{
			return requests;
		}
	}

	@Override
	public boolean shutdownDocumentUpsertThreadPool( int timeout_seconds )
	{
		return ElasticSearchCommon.shutdownDocumentUpsertThreadPool(timeout_seconds);
	}

	@Override
	public boolean upsertDocumentAsync( Indexable object )
	{
		if ( object == null )
		{
			logger.error("Null object!");
			return false;
		}

		try
		{
			ElasticSearchCommon.document_upsert_pool.execute(new UpsertDocumentRunnable(object));
		}
		catch ( Exception e )
		{
			logger.log(Level.FATAL, "Failure during thread pool execution!", e);
			return false;
		}

		return true;
	}

	private class UpsertDocumentRunnable implements Runnable
	{
		private Indexable object;

		public UpsertDocumentRunnable( Indexable object )
		{
			this.object = object;
		}

		@Override
		public void run()
		{
			try
			{
				upsertDocument(object);
			}
			catch ( Exception e )
			{
				logger.log(Level.FATAL, "Failure during upsert operation!", e);
			}
		}
	}

	@Override
	public boolean upsertQuietDocumentAsync( Indexable object )
	{
		if ( object == null )
		{
			logger.error("Null object!");
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);
		Map<String, Object> data = writer.getSimpleFieldsMap();

		try
		{
			ElasticSearchCommon.document_upsert_pool.execute(new UpsertQuietDocumentRunnable(object, data));
		}
		catch ( Exception e )
		{
			logger.log(Level.FATAL, "Failure during thread pool execution!", e);
			return false;
		}

		return true;
	}

	private class UpsertQuietDocumentRunnable implements Runnable
	{
		private Indexable object;
		private Map<String, Object> data;

		public UpsertQuietDocumentRunnable( Indexable object, Map<String, Object> data )
		{
			this.object = object;
			this.data = data;
		}

		@Override
		public void run()
		{
			try
			{
				String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
				String document_name = object.getSimpleSearchDocumentId().getSimpleValue();

				String request_str = "/" + index_name + "/" + ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE + "/" + document_name;

				IndexRequest request = new IndexRequest(index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name).source(data).setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
				IndexResponse response = high_level_rest_client.index(request);
			}
			catch ( Exception e )
			{
				logger.log(Level.FATAL, "Failure during upsert operation!", e);
			}
		}
	}

	@Override
	public boolean putAllFieldMappings( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.fatal("Null index");
			return false;
		}

		if ( !indexExists(index) )
		{
			logger.fatal(String.format("Index %s does not exist!", index.getSimpleIndex().getSimpleValue()));
			return false;
		}

		try
		{
			PutMappingRequest request = new PutMappingRequest(index.getSimpleIndex().getSimpleValue());
			request.type(ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE);
			request.source(ElasticSearchCommon.getMappingBuilder(index, null));

			PutMappingResponse put_response = high_level_rest_client.indices().putMapping(request);

			if ( !put_response.isAcknowledged() )
			{
				logger.fatal(String.format("Put Mappings result not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}
		}
		catch ( Exception e )
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return false;
		}

		return true;
	}

}