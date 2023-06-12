package org.jimmutable.cloud.elasticsearch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.logging.log4j.Level;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryShardException;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.SortDirection;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesRequest;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesResponse;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.elasticsearch.indices.update_aliases.AddAction;
import co.elastic.clients.elasticsearch.indices.update_aliases.RemoveAction;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;

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
	protected volatile ElasticsearchClient esClient;
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
		RestClientBuilder lowLevelClientBuilder;
		if ( type == EnvironmentType.PRODUCTION )
		{
			lowLevelClientBuilder = RestClient.builder(new HttpHost(PRODUCTION_ELASTICSEARCH_HOST, PRODUCTION_ELASTICSEARCH_PORT, "https"));
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

		}
		else
		{
			String elasticsearch_address = System.getProperty("elasticsearch.address");
			String host = NetUtils.extractHostFromHostPortPair(elasticsearch_address, DEFAULT_DEV_ELASTICSEARCH_HOST);
			int port = NetUtils.extractPortFromHostPortPair(elasticsearch_address, DEFAULT_DEV_ELASTICSEARCH_PORT);

			lowLevelClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
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

		}
		ElasticsearchTransport transport = new RestClientTransport(lowLevelClientBuilder.build(), new JacksonJsonpMapper());

		esClient = new ElasticsearchClient(transport);
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
			logger.error("Cannot create a timestamp Index for index "
					+ index);
			return false;
		}

		try
		{
			Action.Builder add_alias_action_builder = new Action.Builder();
			AddAction.Builder add_action_builder = new AddAction.Builder();
			add_action_builder.index(timestamp_index_name);
			add_action_builder.alias(index.getSimpleIndex().getSimpleValue());
			add_alias_action_builder.add(add_action_builder.build());

			UpdateAliasesRequest request = new UpdateAliasesRequest.Builder().actions(Arrays.asList(add_alias_action_builder.build())).build();
			UpdateAliasesResponse indicesAliasesResponse = esClient.indices().updateAliases(request);

			if ( !indicesAliasesResponse.acknowledged() )
			{
				logger.error(String.format("Alias addition not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		}
		catch ( Exception e )
		{
			logger.error("Alias creation failure for index "
					+ index.getSimpleIndex().getSimpleValue(), e);
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
			logger.error("Cannot create a null Index");
			return default_value;
		}

		String index_name = index.getSimpleIndex().getSimpleValue()
				+ "_"
				+ System.currentTimeMillis();
		try
		{

			CreateIndexRequest request = CreateIndexRequest.of(i -> i.index(index_name)//
					.mappings(ElasticSearchCommon.getMappingBuilderTypeMapping(index, null)));//

			CreateIndexResponse createResponse = esClient.indices().create(request);

			if ( !createResponse.acknowledged() )
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
			String delete_request = "/"
					+ index.getSimpleValue()
					+ "/"
					+ document_id.getTypeName().getSimpleName()
					+ "/"
					+ document_id.getSimpleValue();

			DeleteRequest del = DeleteRequest.of(i -> i.index(index.getSimpleValue())//
					.id(document_id.getSimpleValue())//
					.refresh(Refresh.True));
			DeleteResponse response = esClient.delete(del);

			boolean successfully_deleted = response.result().equals(Result.Deleted);

			if ( !successfully_deleted )
			{
				logger.error("delete unsuccessful for: "
						+ delete_request);
			}
			else
			{
				logger.info("successful delete for: "
						+ delete_request);
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
				logger.error("Could not delete index "
						+ index_name
						+ " with timeout set to "
						+ i
						+ " minutes. Retrying deletion.");
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

	private boolean deleteIndex( String index_name, int timeout_minutes )
	{
		if ( index_name == null )
		{
			logger.error("Cannot delete a null Index");
			return false;
		}

		try
		{
			DeleteIndexRequest del = DeleteIndexRequest.of(i -> i.index(index_name)//
					.timeout(new Time.Builder().time(TimeValue.timeValueMinutes(timeout_minutes).getMillis()
							+ "ms")//
							.build()));
			DeleteIndexResponse response = esClient.indices().delete(del);

			if ( response.acknowledged() )
			{
				logger.info("successfully deleted: "
						+ index_name);
				return true;
			}
			else
			{
				logger.info("couldn't delete index: "
						+ index_name);
				return false;
			}
		}
		catch ( Exception e )
		{
			logger.error("Exception thrown deleting index: "
					+ index_name, e);
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
		return upsert(object, Refresh.WaitFor);
	}

	public boolean upsert( Indexable object, Refresh refresh_policy )
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

			IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i.index(index_name)//
					.id(document_name)//
					.refresh(refresh_policy)//
					.document(data));
			IndexResponse response = null;
			try
			{
				response = esClient.index(request);
			}
			catch ( Exception e )
			{
				logger.error("Exception thrown while index while upserting", e);
				return false;
			}

			if ( response == null )
			{
				logger.error("Response was null from index while upserting");
				return false;
			}

			// Expected results, otherwise log an error
			if ( response.result() != Result.Created && response.result() != Result.Updated )
			{
				logger.error(String.format("%s %s/%s/%s %s", response.result().name(), index_name, ElasticSearchCommon.ELASTICSEARCH_DEFAULT_TYPE, document_name, data));
			}

			boolean success = response.result().equals(Result.Created) || response.result().equals(Result.Updated);
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
		return upsertDocumentsBulk(object, Refresh.False);
	}

	/**
	 * Upsert a set of document to a search index immediately. THIS IS EXPENSIVE USE
	 * SPARINGLY!
	 * 
	 * @param Set<Indexable>
	 *            objects The Indexable objects
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocumentsImmediate( Set<Indexable> object )
	{
		return upsertDocumentsBulk(object, Refresh.True);
	}

	public boolean upsertDocumentsBulk( Set<Indexable> objects, Refresh refresh_policy )
	{
		if ( objects == null )
		{
			logger.error("Null object!");
			return false;
		}

		try
		{
			BulkRequest.Builder bulk_request = new BulkRequest.Builder();
			bulk_request.refresh(refresh_policy);
			for ( Indexable object : objects )
			{

				SearchDocumentWriter writer = new SearchDocumentWriter();
				object.writeSearchDocument(writer);
				Map<String, Object> data = writer.getSimpleFieldsMap();
				String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
				String document_name = object.getSimpleSearchDocumentId().getSimpleValue();

				bulk_request.operations(op -> op.index(idx -> idx.index(index_name)//
						.id(document_name)//
						.document(data)));

			}

			boolean success = true;
			BulkResponse bulk_response = esClient.bulk(bulk_request.build());
			for ( BulkResponseItem response : bulk_response.items() )
			{
				Level level;
				if ( response.error() != null )
				{
					logger.error(String.format("%s %s/%s/", response.result(), response.id(), response.error().reason()));
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
	 *            IndexDefinition
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
			ExistsRequest exist_request = ExistsRequest.of(i -> i.index(index.getSimpleValue()));
			BooleanResponse resp = esClient.indices().exists(exist_request);

			return resp.value();

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

			UpdateAliasesRequest.Builder request_builder = new UpdateAliasesRequest.Builder();

			List<Action> actions = new ArrayList<Action>();
			Action add_alias_action = new Action.Builder().add(new AddAction.Builder().index(index_name).alias(definition.getSimpleIndex().getSimpleValue()).build()).build();

			actions.add(add_alias_action);

			// deletes old indices for (String index : indices_to_delete) { AliasActions
			for ( String old_index : old_indices )
			{

				actions.add(new Action.Builder().remove(new RemoveAction.Builder().index(old_index).build()).build());

			}

			request_builder.actions(actions);
			UpdateAliasesResponse indicesAliasesResponse = esClient.indices().updateAliases(request_builder.build());

			if ( !indicesAliasesResponse.acknowledged() )
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
	 *            The alias_name that all the indices are related to
	 * @return the set of indices that have relation to the alias
	 */
	private Set<String> getCurrentIndiciesFromAliasNameComplex( String alias_name, Set<String> default_value )
	{
		Set<String> all_indicies_with_alias = new HashSet<>();
		try
		{

			GetAliasRequest request = GetAliasRequest.of(i -> i.index(alias_name));
			GetAliasResponse resp = esClient.indices().getAlias(request);

			all_indicies_with_alias.addAll(resp.result().keySet());
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
			logger.warn("Storage Scanner for Kind "
					+ kind
					+ " was unable to successfully run. This Kind may not be fully re-indexed or there may currently not be any entries of Kind in Storage. The index will not be swapped on the alias.");
			return false;
		}
		scan_handler.injestor.close();

		logger.info(String.format("Total number of requests successfully submited to index %s: %d. Failures: %d ", index_name, scan_handler.getSimpleSuccessfullyUpsertedDocumentCount(), scan_handler.getSimpleFailedUpsertDocumentCount()));

		if ( scan_handler.hasElasticSearchUpsertFailures() )
		{
			logger.error(String.format("Failed to execute bulk reindex upsert for index %s due to failures to upsert", index_name));

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

				GetMappingRequest request = GetMappingRequest.of(i -> i.index(index.getSimpleIndex().getSimpleValue()));
				GetMappingResponse resp = esClient.indices().getMapping(request);// .getAlias(request);

				Map<String, String> actual = new HashMap<String, String>();

				resp.result().values().stream().forEach(field_mapping ->
				{
					field_mapping.mappings().properties().forEach(( k, v ) ->
					{
						actual.put(k, v._kind().toString().toLowerCase());
					});
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

			List<SortOptions> sort_options = new ArrayList<SortOptions>();
			for ( SortBy sort_order : request.getSimpleSort().getSimpleSortOrder() )
			{
				SortOptions.Builder sort_options_builder = new SortOptions.Builder();
				SortOrder direction = SortOrder.Asc;
				if ( sort_order.getSimpleDirection().equals(SortDirection.DESCENDING) )
				{
					direction = SortOrder.Desc;
				}
				String field_name = sort_order.getSimpleField().getSimpleFieldName().getSimpleName()
				// + ".keyword"
				;
				if ( sort_order.getSimpleField().getSimpleType().equals(SearchIndexFieldType.ATOM) || sort_order.getSimpleField().getSimpleType().equals(SearchIndexFieldType.TEXT) )
				{
					field_name = field_name
							+ "keyword";
				}

				sort_options_builder.field(new FieldSort.Builder().order(direction).field(field_name).build());
				sort_options.add(sort_options_builder.build());
			}
			SearchRequest search_request = new SearchRequest.Builder()//
					.index(index_name)//
					.size(size)//
					.from(from)//
					.query(new Query.Builder().queryString(new QueryStringQuery.Builder().query(request.getSimpleQueryString()).build()).build())//
					.sort(sort_options)//
					.build();
			SearchResponse<Map> response = esClient.search(search_request, Map.class);

			List<OneSearchResultWithTyping> results = new LinkedList<OneSearchResultWithTyping>();

			response.hits().hits().forEach(hit ->
			{
				Map<FieldName, FieldArrayList<String>> map = new TreeMap<FieldName, FieldArrayList<String>>();
				hit.source().forEach(( k, v ) ->
				{
					FieldName name = new FieldName((String) k);
					FieldArrayList<String> array_val = map.get(name);

					FieldArrayList<String> new_array_val = new FieldArrayList<>();

					if ( v instanceof ArrayList<?> )
					{
						List<String> array_as_list = (ArrayList<String>) v;
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

			logger.info(String.format("QUERY:%s INDEX:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", request.getSimpleQueryString(), index.getSimpleValue(), results.size(), response.hits().hits().size(), request.getSimpleMaxResults(), request.getSimpleStartResultsAfter()));
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
	public SearchResponse<Indexable> searchRaw( SearchRequest request )
	{
		if ( request == null )
		{
			throw new NullPointerException();
		}

		try
		{
			return esClient.search(s -> s.index(request.index().get(0)).query(request.query()), Indexable.class);
		}
		catch ( Exception e )
		{
			logger.error("Failed to search!", e);
		}

		return null;
	}

	@Override
	public ScrollResponse<Indexable> searchScrollRaw( ScrollRequest request )
	{
		if ( request == null )
		{
			throw new NullPointerException();
		}

		try
		{
			return esClient.scroll(request, Indexable.class);

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
			ClearScrollResponse resp_raw = esClient.clearScroll(request);

			return resp_raw.succeeded();
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
				logger.error("Kind "
						+ kind
						+ " passed in for re-indexing is not registered with SearchSync.registerIndexableKind");
				return false;
			}
			String index_name = createTimestampIndex(index_definition, null);
			if ( index_name == null )
			{
				logger.error("Kind "
						+ kind
						+ " could not create a new index to reindex documents with");
				return false;
			}

			boolean success = syncSearchAndStorage(kind, index_name);
			if ( !success )
			{
				logger.error("Kind "
						+ kind
						+ " did not complete sync of storage and search, no swap made");
				if ( !deleteWithRetry(index_name) )
				{
					logger.error("Error removing index "
							+ index_name
							+ " on failure");
				}
				return false;
			}

			// Swap alias
			success = updateAlias(index_definition, index_name);
			if ( !success )
			{
				logger.error("Kind "
						+ kind
						+ " could not complete full alias swap");
				if ( !deleteWithRetry(index_name) )
				{
					logger.error("Error removing index "
							+ index_name
							+ " on failure.");
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

		try
		{
			SearchResponse<Map> search_response = esClient.search(s -> s.index(index_name)//
					.scroll(new Time.Builder().time(TimeValue.timeValueMinutes(1).getMillis()
							+ "ms").build())//
					.size(1_000)//
					.sort(new SortOptions.Builder().field(FieldSort.of(fs -> fs.field(FieldSortBuilder.DOC_FIELD_NAME).order(SortOrder.Asc))).build()).query(new Query.Builder().queryString(new QueryStringQuery.Builder().query(query_string).build())//
							.build()), Map.class);
			String scroll_id = search_response.scrollId();
			List<Hit<Map>> hits = search_response.hits().hits();
			do
			{
				String[] document;
				for ( Hit<Map> hit : hits )
				{
					document = new String[sorted_header.size()];

					Map<String, JsonData> resultMap = hit.fields();

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
				ScrollResponse<Map> scroll_response = esClient.scroll(new ScrollRequest.Builder()//
						.scroll(new Time.Builder().time(TimeValue.timeValueMinutes(1).getMillis()
								+ "ms").build())//
						.scrollId(scroll_id).build(), Map.class);
				hits = scroll_response.hits().hits();

			}
			while ( hits.size() != 0 ); // Zero hits mark the end of the scroll and the
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

		private AtomicBoolean has_failures = new AtomicBoolean();

		BulkListener<Void> listener;
		BulkIngester<Void> injestor;

		private BulkElasticSearchUpsertScanHandler( String index_name )
		{
			this.index_name = index_name;
			listener = createBulkListener();
			injestor = BulkIngester.of(b -> b.client(esClient).maxOperations(MAX_NO_REQUESTS_IN_BULK_REQUEST).flushInterval(1l, TimeUnit.SECONDS).listener(listener));
		}

		private BulkListener<Void> createBulkListener()
		{
			return new BulkListener<Void>()
			{
				@Override
				public void beforeBulk( long executionId, BulkRequest request, List<Void> contexts )
				{
				}

				@Override
				public void afterBulk( long executionId, BulkRequest request, List<Void> contexts, BulkResponse response )
				{
					int success_count = 0;
					int failure_count = 0;
					for ( BulkResponseItem bulk_item_response : response.items() )
					{
						if ( bulk_item_response.error() != null )
						{
							ErrorCause failure = bulk_item_response.error();
							logger.error("Failure on bulk upsert occurred with index "
									+ index_name
									+ " id "
									+ bulk_item_response.id()
									+ ". Failure msg "
									+ failure.reason());
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
				public void afterBulk( long executionId, BulkRequest request, List<Void> contexts, Throwable failure )
				{
					logger.error("Failure for full successful bulk upsert for index name "
							+ index_name, failure);
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
				logger.error("Could not retrieve object for StorageKey "
						+ key);
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
				logger.error("This object from StorageKey "
						+ key
						+ " was unable to be deserialized as a Storable and Indexable object...", e);
				return;
			}
			Indexable indexable = (Indexable) obj.getObject();

			// Not async so it's only finished once the whole scan does so
			try
			{
				SearchDocumentWriter writer = new SearchDocumentWriter();

				// Someone could have a bug in the search doc writer so we need ot handle
				// that
				// case
				indexable.writeSearchDocument(writer);
				Map<String, Object> data = writer.getSimpleFieldsMap();
				String document_name = indexable.getSimpleSearchDocumentId().getSimpleValue();
				injestor.add(op -> op.index(index -> index.index(index_name).id(document_name).document(data)));

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

				IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i.index(index_name)//
						.id(document_name)//
						.document(data)//
						.refresh(Refresh.WaitFor));
				esClient.index(request);
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
			PutMappingRequest request = new PutMappingRequest.Builder().index(index.getSimpleIndex().getSimpleValue())//
					.source(ElasticSearchCommon.getMappingBuilderSourceField(index, null)).build();

			PutMappingResponse put_response = esClient.indices().putMapping(request);

			if ( !put_response.acknowledged() )
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