package org.jimmutable.cloud.elasticsearch;

import java.io.ByteArrayOutputStream;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final String PRODUCTION_ELASTICSEARCH_PASSWORD = "production_elastic_password";

	private static final String PRODUCTION_ELASTICSEARCH_USERNAME = "production_elastic_username";

	private static final int MAX_NO_REQUESTS_IN_BULK_REQUEST = 2000;
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchRESTClient.class);

	/**
	 * The RestClient replaces the TransportClient that we used previously. The
	 * RestClient is more robust, and it doesn't strictly require us to have the
	 * same version on client/server.
	 */
	protected volatile RestHighLevelClient high_level_rest_client;

	private String PRODUCTION_ELASTICSEARCH_HOST = //
			"7381bd4eeaa34c1f8e197493b45987b1.us-west-2.aws.found.io";
	// "f8bfe258266ee6bd44cece0dde4326d5.us-west-2.aws.found.io";
	private int PRODUCTION_ELASTICSEARCH_PORT = 9243;

	private static final String DEFAULT_DEV_ELASTICSEARCH_HOST = ElasticSearchEndpoint.CURRENT.getSimpleHost();
	// This is the default dev REST port, there isn't a way to get this through the
	// API as far as I can tell for now
	private static final int DEFAULT_DEV_ELASTICSEARCH_PORT = 9200;

	public static final int FIVE_SECOND_CONNECT_TIMEOUT_MILLIS = 5000;
	public static final int SIXTY_SECOND_SOCKET_TIMEOUT_MILLIS = 60000;

	public ElasticSearchRESTClient()
	{
		// Once we deprecate the TransportClient in dev, we can simply add a
		// RestClientBuilder in construction, that will swap between dev and prod
		EnvironmentType type = CloudExecutionEnvironment.getEnvironmentTypeFromSystemProperty(null);
		if ( type == EnvironmentType.PRODUCTION )
		{
			RestClientBuilder lowLevelClientBuilder = RestClient.builder(new HttpHost(PRODUCTION_ELASTICSEARCH_HOST, PRODUCTION_ELASTICSEARCH_PORT, "https"));
			String production_elastic_username = System.getProperty(PRODUCTION_ELASTICSEARCH_USERNAME);
			String production_elastic_password = System.getProperty(PRODUCTION_ELASTICSEARCH_PASSWORD);
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(production_elastic_username, production_elastic_password));
			try
			{
				lowLevelClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
				{
					@Override
					public HttpAsyncClientBuilder customizeHttpClient( HttpAsyncClientBuilder httpClientBuilder )
					{
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				});

				lowLevelClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback()
				{
					@Override
					public RequestConfig.Builder customizeRequestConfig( RequestConfig.Builder requestConfigBuilder )
					{
						return requestConfigBuilder.setConnectTimeout(FIVE_SECOND_CONNECT_TIMEOUT_MILLIS).setSocketTimeout(SIXTY_SECOND_SOCKET_TIMEOUT_MILLIS);
					}
				});
			}
			catch ( Exception e )
			{
				logger.debug(e.getMessage());
			}

			high_level_rest_client = new RestHighLevelClient(lowLevelClientBuilder);
		}
		else
		{
			String elasticsearch_address = System.getProperty("elasticsearch.address");
			String host = NetUtils.extractHostFromHostPortPair(elasticsearch_address, DEFAULT_DEV_ELASTICSEARCH_HOST);
			int port = NetUtils.extractPortFromHostPortPair(elasticsearch_address, DEFAULT_DEV_ELASTICSEARCH_PORT);

			RestClientBuilder lowLevelClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
			try
			{

				lowLevelClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback()
				{

					@Override
					public RequestConfig.Builder customizeRequestConfig( RequestConfig.Builder requestConfigBuilder )
					{
						return requestConfigBuilder.setConnectTimeout(FIVE_SECOND_CONNECT_TIMEOUT_MILLIS).setSocketTimeout(SIXTY_SECOND_SOCKET_TIMEOUT_MILLIS);
					}
				});
			}
			catch (

			Exception e )
			{
				logger.debug(e.getMessage());
			}

			high_level_rest_client = new RestHighLevelClient(lowLevelClientBuilder);
		}
	}

	public boolean upsertIndex( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.error("Cannot upsert a null Index");
			return false;
		}

		// if it exists and is not configured correctly delete and add
		if ( indexExists(index) )
		{
			logger.info(String.format("No upsert needed for index %s", index.getSimpleIndex().getSimpleValue()));
			return true;
		}
		// index is new
		return createIndex(index);
	}

	private boolean createIndex( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.error("Cannot create a null Index");
			return false;
		}

		String timestamp_index_name = createTimestampIndex(index, null);
		if ( timestamp_index_name == null )
		{
			logger.error("Cannot create a timestamp Index for index " + index);
			return false;
		}

		try
		{
			IndicesAliasesRequest request = new IndicesAliasesRequest();

			AliasActions add_alias_action = new AliasActions(AliasActions.Type.ADD);
			add_alias_action.index(timestamp_index_name);
			add_alias_action.alias(index.getSimpleIndex().getSimpleValue());
			request.addAliasAction(add_alias_action);

			AcknowledgedResponse indicesAliasesResponse = high_level_rest_client.indices().updateAliases(request, RequestOptions.DEFAULT);

			if ( !indicesAliasesResponse.isAcknowledged() )
			{
				logger.error(String.format("Alias addition not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		}
		catch ( Exception e )
		{
			logger.error("Alias creation failure for index " + index.getSimpleIndex().getSimpleValue(), e);
			return false;
		}

		return true;
	}

	/**
	 * This will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name).
	 * 
	 * @param index
	 *                          The SearchIndexDefinition that the index will relate
	 *                          to
	 * @param default_value
	 *                          value to return on failure
	 * @return the new unique index name on success, default_value on failure
	 */

	private String createTimestampIndex( SearchIndexDefinition index, String default_value )
	{
		if ( index == null )
		{
			logger.error("Cannot create a null Index");
			return default_value;
		}

		String index_name = index.getSimpleIndex().getSimpleValue() + "_" + System.currentTimeMillis();

		try
		{
			CreateIndexRequest request = new CreateIndexRequest(index_name).mapping(ElasticSearchCommon.getMappingBuilder(index, null));
			CreateIndexResponse createResponse = high_level_rest_client.indices().create(request, RequestOptions.DEFAULT);

			if ( !createResponse.isAcknowledged() )
			{
				logger.error(String.format("Index Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return default_value;
			}

			return index_name;
		}
		catch ( Exception e )
		{
			logger.error(String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			// The indices can successfully be created but then has other issues. We will
			// try to delete if so.
			if ( !deleteWithRetry(index_name) )
			{
				logger.error(String.format("Failed to delete possibly left over empty index %s", index_name));
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
			logger.error("Null index or document id");
			return false;
		}

		try
		{
			String delete_request = "/" + index.getSimpleValue() + "/" + document_id.getTypeName().getSimpleName() + "/" + document_id.getSimpleValue();

			DeleteRequest del = new DeleteRequest(index.getSimpleValue(), document_id.getSimpleValue()).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			DeleteResponse response = high_level_rest_client.delete(del, RequestOptions.DEFAULT);

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
			logger.error("Error", e);
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
	 *                  SearchIndexDefinition
	 * @return boolean - true if successfully deleted, else false
	 */

	private boolean deleteIndex( String index_name, int timeout_minutes )
	{
		if ( index_name == null )
		{
			logger.error("Cannot delete a null Index");
			return false;
		}

		try
		{
			DeleteIndexRequest del = new DeleteIndexRequest(index_name);
			del.timeout(TimeValue.timeValueMinutes(timeout_minutes));
			AcknowledgedResponse response = high_level_rest_client.indices().delete(del, RequestOptions.DEFAULT);

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
	 *                   The Indexable object
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocument( Indexable object )
	{
		return upsert(object, RefreshPolicy.WAIT_UNTIL);
	}

	public boolean upsert( Indexable object, RefreshPolicy refresh_policy )
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

			IndexRequest request = new IndexRequest(index_name).id(document_name).source(data).setRefreshPolicy(refresh_policy);
			IndexResponse response = null;
			try
			{
				response = high_level_rest_client.index(request, RequestOptions.DEFAULT);
			}
			catch ( Exception e )
			{
				logger.error("Exception thrown deleting index while upserting", e);
				return false;
			}

			if ( response == null )
			{
				logger.error("Response was null from index while upserting");
				return false;
			}

			// Expected results, otherwise log an error
			if ( response.getResult() != Result.CREATED && response.getResult() != Result.UPDATED )
			{
				logger.error(String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name, data));
			}

			boolean success = response.getResult().equals(Result.CREATED) || response.getResult().equals(Result.UPDATED);
			return success;
		}
		catch ( Exception e )
		{
			logger.error("Exception thrown deleting index: ", e);
			return false;
		}
	}

	@Override
	public boolean upsertDocuments( Set<Indexable> object )
	{
		return upsertDocumentsBulk(object, RefreshPolicy.NONE);
	}

	/**
	 * Upsert a set of document to a search index immediately. THIS IS EXPENSIVE USE
	 * SPARINGLY!
	 * 
	 * @param Set<Indexable>
	 *                           objects The Indexable objects
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocumentsImmediate( Set<Indexable> object )
	{
		return upsertDocumentsBulk(object, RefreshPolicy.IMMEDIATE);
	}

	public boolean upsertDocumentsBulk( Set<Indexable> objects, RefreshPolicy refresh_policy )
	{
		if ( objects == null )
		{
			logger.error("Null object!");
			return false;
		}

		try
		{
			BulkRequest bulk_request = new BulkRequest();
			bulk_request.setRefreshPolicy(refresh_policy);
			for ( Indexable object : objects )
			{

				SearchDocumentWriter writer = new SearchDocumentWriter();
				object.writeSearchDocument(writer);
				Map<String, Object> data = writer.getSimpleFieldsMap();
				String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
				String document_name = object.getSimpleSearchDocumentId().getSimpleValue();
				bulk_request.add(new IndexRequest().index(index_name).id(document_name).source(data));

			}

			boolean success = true;
			BulkResponse bulk_response = high_level_rest_client.bulk(bulk_request, RequestOptions.DEFAULT);
			for ( BulkItemResponse response : bulk_response.getItems() )
			{
				Level level;
				if ( response.isFailed() )
				{
					logger.error(String.format("%s %s/%s/", response.getResponse(), response.getId(), (response.isFailed() ? response.getFailure().getMessage() : "")));
					success = false;
				}
			}

			return success;
		}
		catch ( Exception e )
		{
			logger.error(String.format("Failure during upsert operation of documents!"), e);
			return false;
		}
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *                  IndexDefinition
	 * @return boolean if the index exists or not
	 */
	@Override
	public boolean indexExists( IndexDefinition index )
	{
		if ( index == null )
		{
			logger.error("Cannot check the existence of a null Index");
			return false;
		}
		try
		{
			Request request = new Request("GET", "/" + index.getSimpleValue());
			Response resp = high_level_rest_client.getLowLevelClient().performRequest(request);

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
	 *                       The definition of the Kind that is being reindexed
	 * @param index_name
	 *                       The unique index name that data was upserted to
	 * @return is_success
	 */

	private boolean updateAlias( SearchIndexDefinition definition, String index_name )
	{
		Set<String> old_indices = getCurrentIndiciesFromAliasNameComplex(definition.getSimpleIndex().getSimpleValue(), null);
		if ( old_indices == null )
		{
			logger.error(String.format("Old aliases not found for index name %s. Should always have index created by startup process. Not reindexing.", index_name));
			// Something went wrong with the request. In the past this has been caused by ES
			// being under high load and returning the wrong values:
			// https://stackoverflow.com/questions/49054451/atomic-alias-swap-fails-with-index-not-found-exception-on-a-totally-unrelated-in
			return false;
		}

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

			AcknowledgedResponse indicesAliasesResponse = high_level_rest_client.indices().updateAliases(request, RequestOptions.DEFAULT);

			if ( !indicesAliasesResponse.isAcknowledged() )
			{
				logger.error(String.format("Alias addition not acknowledged for index %s", index_name));
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
	 *                       The alias_name that all the indices are related to
	 * @return the set of indices that have relation to the alias
	 */
	private Set<String> getCurrentIndiciesFromAliasNameComplex( String alias_name, Set<String> default_value )
	{
		Set<String> all_indicies_with_alias = new HashSet<>();
		try
		{
			Request request = new Request("GET", "/_alias/" + alias_name);
			Response resp = high_level_rest_client.getLowLevelClient().performRequest(request);
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
			return default_value;
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
		// This handles the initial scanning of storage to get all the requests. It
		// blocks until all requests have been given to our bulk processor for ES. It
		// does not mean that all requests will be done once the lock is released so we
		// wait until the processor closes before continuing.
		BulkElasticSearchUpsertScanHandler scan_handler = new BulkElasticSearchUpsertScanHandler(index_name);
		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().scan(kind, scan_handler, 20) )
		{
			logger.warn("Storage Scanner for Kind " + kind + " was unable to successfully run. This Kind may not be fully re-indexed or there may currently not be any entries of Kind in Storage. The index will not be swapped on the alias.");
			return false;
		}

		boolean is_bulk_processor_closed = false;
		try
		{
			is_bulk_processor_closed = scan_handler.getSimpleBulkProcessor().awaitClose(SearchSync.MAX_REINDEX_COMPLETION_TIME_MINUTES - 1, TimeUnit.MINUTES);
		}
		catch ( Exception e )
		{
			logger.error("Failure to close bulk ES client for index name " + index_name, e);
		}

		if ( !is_bulk_processor_closed )
		{
			try
			{
				scan_handler.getSimpleBulkProcessor().close();
			}
			catch ( Exception e2 )
			{
				logger.error("2nd failure to close bulk ES client for index name " + index_name, e2);
			}
		}
		logger.info(String.format("Total number of requests successfully submited to index %s: %d. Failures: %d ", index_name, scan_handler.getSimpleSuccessfullyUpsertedDocumentCount(), scan_handler.getSimpleFailedUpsertDocumentCount()));

		if ( scan_handler.hasElasticSearchUpsertFailures() )
		{
			logger.error(String.format("Failed to execute bulk reindex upsert for index %s due to failures to upsert", index_name));
			return false;
		}

		if ( !is_bulk_processor_closed )
		{
			logger.error(String.format("Failed to close bulk processor %s", index_name));
		}

		return true;
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *                  SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	@Override
	public boolean indexExists( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.error("Cannot check the existence of a null Index");
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
				Request request = new Request("GET", "/" + index.getSimpleIndex().getSimpleValue());
				Response resp = high_level_rest_client.getLowLevelClient().performRequest(request);
				String response_body = EntityUtils.toString(resp.getEntity());

				JsonNode node = new ObjectMapper().readTree(response_body).findValue("mappings").findValue("properties");

				Map<String, String> actual = new HashMap<String, String>();

				node.fields().forEachRemaining(fieldMapping ->
				{
					if ( !fieldMapping.getKey().contains(ElasticSearchCommon.SORT_FIELD_NAME_JIMMUTABLE) ) // Skip our
																											// keyword
																											// fields
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
				logger.error(String.format("Failed to get the index mapping for index %s", index.getSimpleIndex().getSimpleValue()), e);
			}
		}

		return false;
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

			SearchRequest ext_request = new SearchRequest(index_name).source(query_builder);
			SearchResponse response = high_level_rest_client.search(ext_request, RequestOptions.DEFAULT);

			List<OneSearchResultWithTyping> results = new LinkedList<OneSearchResultWithTyping>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, FieldArrayList<String>> map = new TreeMap<FieldName, FieldArrayList<String>>();
				hit.getSourceAsMap().forEach(( k, v ) ->
				{
					FieldName name = new FieldName(k);
					FieldArrayList<String> array_val = map.get(name);

					FieldArrayList<String> new_array_val = new FieldArrayList<>();

					if ( v instanceof ArrayList<?> )
					{
						List<Object> array_as_list = (ArrayList<Object>) v;
						new_array_val = new FieldArrayList<String>();

						for ( int i = 0; i < array_as_list.size(); i++ )
						{
							new_array_val.add(String.valueOf(array_as_list.get(i)));
						}
					}
					else
					{
						if ( array_val == null )
							array_val = new FieldArrayList<String>();
						new_array_val.add(String.valueOf(v));
					}

					if ( new_array_val != null )
						map.put(name, new_array_val);
				});
				results.add(new OneSearchResultWithTyping(map));
			});

			logger.info(String.format("QUERY:%s INDEX:%s STATUS:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", request.getSimpleQueryString(), index.getSimpleValue(), response.status(), results.size(), response.getHits().getTotalHits(), request.getSimpleMaxResults(), request.getSimpleStartResultsAfter()));
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
			return high_level_rest_client.search(request, RequestOptions.DEFAULT);
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
			return high_level_rest_client.searchScroll(request, RequestOptions.DEFAULT);

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
			ClearScrollResponse resp_raw = high_level_rest_client.clearScroll(request, RequestOptions.DEFAULT);

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

		SearchRequest ext_request = new SearchRequest(index_name).scroll(TimeValue.timeValueMinutes(1)).source(query_builder);

		try
		{
			SearchResponse scrollResp = high_level_rest_client.search(ext_request, RequestOptions.DEFAULT);

			do
			{
				String[] document;
				for ( SearchHit hit : scrollResp.getHits().getHits() )
				{
					document = new String[sorted_header.size()];

					Map<String, Object> resultMap = hit.getSourceAsMap();

					for ( int i = 0; i < sorted_header.size(); i++ )
					{
						if ( resultMap.containsKey(sorted_header.get(i).getSimpleValue()) )
						{
							document[i] = normalizeReturnedValue(resultMap.get(sorted_header.get(i).getSimpleValue()));
						}
					}

					try
					{
						list_writer.write(Arrays.asList(document), cell_processors);
					}
					catch ( IOException e )
					{
						logger.error("Error writing documents", e);
						return false;
					}
				}

				scrollResp = high_level_rest_client.searchScroll(new SearchScrollRequest(scrollResp.getScrollId()).scroll(TimeValue.timeValueMinutes(1)), RequestOptions.DEFAULT);

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
	private class BulkElasticSearchUpsertScanHandler implements StorageKeyHandler
	{
		private String index_name;
		private AtomicInteger successful_upsert_document_count = new AtomicInteger();
		private AtomicInteger failed_upsert_document_count = new AtomicInteger();

		private BulkProcessor bulk_processor;
		private AtomicBoolean has_failures = new AtomicBoolean();

		private BulkElasticSearchUpsertScanHandler( String index_name )
		{
			this.index_name = index_name;
			BulkProcessor.Listener listener = createBulkProcessorListener();
			BulkProcessor.Builder builder = BulkProcessor.builder(( request, bulkListener ) -> high_level_rest_client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener), listener);
			builder.setBulkActions(MAX_NO_REQUESTS_IN_BULK_REQUEST);
			this.bulk_processor = builder.build();
		}

		private BulkProcessor.Listener createBulkProcessorListener()
		{
			return new BulkProcessor.Listener()
			{
				@Override
				public void beforeBulk( long executionId, BulkRequest request )
				{

				}

				// For successful execution only
				@Override
				public void afterBulk( long executionId, BulkRequest request, BulkResponse response )
				{
					int success_count = 0;
					int failure_count = 0;
					for ( BulkItemResponse bulk_item_response : response )
					{
						if ( bulk_item_response.isFailed() )
						{
							BulkItemResponse.Failure failure = bulk_item_response.getFailure();
							logger.error("Failure on bulk upsert occurred with index " + index_name + " id " + bulk_item_response.getId() + ". Failure msg " + failure.getMessage());
							failure_count++;
						}
						else
						{
							success_count++;
						}
					}

					// Update shared variable across threads only once per bulk upsert to reduce
					// overhead of locking for the atomic variable
					if ( failure_count != 0 )
					{
						failed_upsert_document_count.addAndGet(failure_count);
					}
					successful_upsert_document_count.addAndGet(success_count);
				}

				@Override
				public void afterBulk( long executionId, BulkRequest request, Throwable failure )
				{
					logger.error("Failure for full successful bulk upsert for index name " + index_name, failure);
					has_failures.set(true);
				}
			};
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void handle( StorageKey key )
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			boolean retrieved = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersionStreaming(key, out);

			if ( !retrieved )
			{
				logger.error("Could not retrieve object for StorageKey " + key);
				return;
			}

			byte[] bytes = out.toByteArray();

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
			Indexable indexable = (Indexable) obj.getObject();

			// Not async so it's only finished once the whole scan does so
			try
			{
				SearchDocumentWriter writer = new SearchDocumentWriter();

				// Someone could have a bug in the search doc writer so we need ot handle that
				// case
				indexable.writeSearchDocument(writer);
				Map<String, Object> data = writer.getSimpleFieldsMap();
				String document_name = indexable.getSimpleSearchDocumentId().getSimpleValue();

				IndexRequest request = new IndexRequest(index_name).id(document_name).source(data).timeout("5m");
				if ( request != null )
				{
					bulk_processor.add(request);
				}
			}
			catch ( Exception e )
			{
				logger.error(String.format("Failure during operation of Document id:%s on Index:%s", indexable.getSimpleSearchDocumentId().getSimpleValue(), indexable.getSimpleSearchIndexDefinition().getSimpleValue()), e);
			}
		}

		private boolean hasElasticSearchUpsertFailures()
		{
			return failed_upsert_document_count.get() != 0;
		}

		private int getSimpleSuccessfullyUpsertedDocumentCount()
		{
			return successful_upsert_document_count.get();
		}

		private int getSimpleFailedUpsertDocumentCount()
		{
			return failed_upsert_document_count.get();
		}

		private BulkProcessor getSimpleBulkProcessor()
		{
			return bulk_processor;
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
			logger.error("Failure during thread pool execution!", e);
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
				logger.error("Failure during upsert operation!", e);
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
			logger.error("Failure during thread pool execution!", e);
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
				IndexRequest request = new IndexRequest(index_name).id(document_name).source(data).setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
				high_level_rest_client.index(request, RequestOptions.DEFAULT);
			}
			catch ( Exception e )
			{
				logger.error("Failure during upsert operation!", e);
			}
		}
	}

	@Override
	public boolean putAllFieldMappings( SearchIndexDefinition index )
	{
		if ( index == null )
		{
			logger.error("Null index");
			return false;
		}

		if ( !indexExists(index) )
		{
			logger.error(String.format("Index %s does not exist!", index.getSimpleIndex().getSimpleValue()));
			return false;
		}

		try
		{
			PutMappingRequest request = new PutMappingRequest(index.getSimpleIndex().getSimpleValue());
			request.source(ElasticSearchCommon.getMappingBuilder(index, null));

			AcknowledgedResponse put_response = high_level_rest_client.indices().putMapping(request, RequestOptions.DEFAULT);

			if ( !put_response.isAcknowledged() )
			{
				logger.error(String.format("Put Mappings result not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}
		}
		catch ( Exception e )
		{
			logger.error(String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return false;
		}

		return true;
	}

}