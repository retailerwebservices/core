package org.jimmutable.cloud.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.SortDirection;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.TimeOfDay;
import org.jimmutable.core.serialization.FieldName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

import com.carrotsearch.hppc.ObjectLookupContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Use this class for general searching and document upserts with Elasticsearch
 * 
 * @author trevorbox
 *
 */
public class ElasticSearch implements ISearch
{

	private static final Logger logger = LogManager.getLogger(ElasticSearch.class);

	private static final ExecutorService document_upsert_pool = (ExecutorService) new ThreadPoolExecutor(8, 8, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

	public static final String ELASTICSEARCH_DEFAULT_TYPE = "default";
	public static final String SORT_FIELD_NAME_JIMMUTABLE = "jimmutable_sort_field";

	private volatile TransportClient client;

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
	 */
	@Override
	public SearchRequestBuilder getBuilder(IndexDefinition index)
	{
		if (index == null)
		{
			throw new RuntimeException("Null IndexDefinition");
		}
		return client.prepareSearch(index.getSimpleValue());
	}

	public ElasticSearch(TransportClient client)
	{
		this.client = client;
	}

	public boolean writeAllToCSV(IndexDefinition index, String query_string, List<SearchFieldId> sorted_header, ICsvListWriter list_writer, CellProcessor[] cell_processors)
	{
		if (index == null || query_string == null)
		{
			return false;
		}

		SearchResponse scrollResp = client.prepareSearch(index.getSimpleValue()).addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC).setScroll(new TimeValue(60000)).setQuery(QueryBuilders.queryStringQuery(query_string)).setSize(1000).get();

		do
		{

			String[] document;
			for (SearchHit hit : scrollResp.getHits().getHits())
			{

				document = new String[sorted_header.size()];

				Map<String, Object> resultMap = hit.getSourceAsMap();

				for (int i = 0; i < sorted_header.size(); i++)
				{
					if (resultMap.containsKey(sorted_header.get(i).getSimpleValue()))
					{
						document[i] = resultMap.get(sorted_header.get(i).getSimpleValue()).toString();
					}
				}

				try
				{
					list_writer.write(Arrays.asList(document), cell_processors);
				} catch (IOException e)
				{
					logger.error(e);
					return false;
				}

			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while
																// loop.
		return true;
	}

	/**
	 * Gracefully shutdown the running threads. Note: the TransportClient should be
	 * closed where instantiated. This is not handles by this.
	 * 
	 * @return boolean if shutdown correctly or not
	 */
	@Override
	public boolean shutdownDocumentUpsertThreadPool(int seconds)
	{

		long start = System.currentTimeMillis();

		document_upsert_pool.shutdown();

		boolean terminated = true;

		try
		{
			terminated = document_upsert_pool.awaitTermination(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e)
		{
			logger.log(Level.FATAL, "Shutdown of runnable pool was interrupted!", e);
		}

		if (!terminated)
		{
			logger.error("Failed to terminate in %s seconds. Calling shutdownNow...", seconds);
			document_upsert_pool.shutdownNow();
		}

		boolean success = document_upsert_pool.isTerminated();

		if (success)
		{
			logger.warn(String.format("Successfully terminated pool in %s milliseconds", (System.currentTimeMillis() - start)));
		} else
		{
			logger.warn(String.format("Unsuccessful termination of pool in %s milliseconds", (System.currentTimeMillis() - start)));
		}
		return success;
	}

	/**
	 * Runnable class to upsert the document
	 * 
	 * @author trevorbox
	 *
	 */
	private class UpsertDocumentRunnable implements Runnable
	{
		private Indexable object;
		private Map<String, Object> data;

		public UpsertDocumentRunnable(Indexable object, Map<String, Object> data)
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
				IndexResponse response = client.prepareIndex(index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name).setSource(data).get();

				Level level;
				switch (response.getResult())
				{
				case CREATED:
					level = Level.INFO;
					break;
				case UPDATED:
					level = Level.INFO;
					break;
				default:
					level = Level.FATAL;
					break;
				}

				logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name, data));

			} catch (Exception e)
			{
				logger.log(Level.FATAL, "Failure during upsert operation!", e);
			}
		}
	}

	/**
	 * Runnable class to upsert the document
	 * 
	 * @author trevorbox
	 *
	 */
	private class UpsertQuietDocumentRunnable implements Runnable
	{
		private Indexable object;
		private Map<String, Object> data;

		public UpsertQuietDocumentRunnable(Indexable object, Map<String, Object> data)
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
				client.prepareIndex(index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name).setSource(data).get();

			} catch (Exception e)
			{
				logger.log(Level.FATAL, "Failure during upsert operation!", e);
			}
		}
	}

	/**
	 * Upsert a document to a search index asynchronously AND without logging to
	 * INFO
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertQuietDocumentAsync(Indexable object)
	{

		if (object == null)
		{
			logger.error("Null object!");
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);
		Map<String, Object> data = writer.getSimpleFieldsMap();

		try
		{
			document_upsert_pool.execute(new UpsertQuietDocumentRunnable(object, data));
		} catch (Exception e)
		{
			logger.log(Level.FATAL, "Failure during thread pool execution!", e);
			return false;
		}

		return true;
	}

	/**
	 * Upsert a document to a search index asynchronously
	 * 
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocumentAsync(Indexable object)
	{

		if (object == null)
		{
			logger.error("Null object!");
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);
		Map<String, Object> data = writer.getSimpleFieldsMap();

		try
		{
			document_upsert_pool.execute(new UpsertDocumentRunnable(object, data));
		} catch (Exception e)
		{
			logger.log(Level.FATAL, "Failure during thread pool execution!", e);
			return false;
		}

		return true;
	}

	/**
	 * Upsert a document to a search index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean If successful or not
	 */
	@Override
	public boolean upsertDocument(Indexable object)
	{

		if (object == null)
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
			IndexResponse response = client.prepareIndex(index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name).setRefreshPolicy(RefreshPolicy.IMMEDIATE).setSource(data).get();

			Level level;
			switch (response.getResult())
			{
			case CREATED:
				level = Level.INFO;
				break;
			case UPDATED:
				level = Level.INFO;
				break;
			default:
				level = Level.FATAL;
				break;
			}

			logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name, data));

		} catch (Exception e)
		{
			logger.log(Level.FATAL, String.format("Failure during upsert operation of Document id:%s on Index:%s", object.getSimpleSearchDocumentId().getSimpleValue(), object.getSimpleSearchIndexDefinition().getSimpleValue()), e);
			return false;
		}

		return true;
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
	public JSONServletResponse search(IndexDefinition index, StandardSearchRequest request)
	{

		if (index == null || request == null)
		{
			return new SearchResponseError(request, "Null parameter(s)!");
		}

		try
		{

			String index_name = index.getSimpleValue();
			int from = request.getSimpleStartResultsAfter();
			int size = request.getSimpleMaxResults();

			SearchRequestBuilder builder = client.prepareSearch(index_name);
			builder.setTypes(ELASTICSEARCH_DEFAULT_TYPE);
			builder.setFrom(from);
			builder.setSize(size);
			builder.setQuery(QueryBuilders.queryStringQuery(request.getSimpleQueryString()));
			
			// Sorting
			for ( SortBy sort_by : request.getSimpleSort().getSimpleSortOrder() )
			{
				FieldSortBuilder sort_builder = getSort(sort_by, null);
				if ( sort_builder == null ) continue;
				
				builder.addSort(sort_builder);
			}

			SearchResponse response = builder.get();

			List<OneSearchResult> results = new LinkedList<OneSearchResult>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, String> map = new TreeMap<FieldName, String>();
				hit.getSourceAsMap().forEach((k, v) ->
				{
					map.put(new FieldName(k), v.toString());
				});
				results.add(new OneSearchResult(map));
			});

			long total_hits = response.getHits().totalHits;

			int next_page = from + size;
			int previous_page = (from - size) < 0 ? 0 : (from - size);

			boolean has_more_results = total_hits > next_page;

			boolean has_previous_results = from > 0;

			Level level;
			switch (response.status())
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

		} catch (Exception e)
		{
			if (e.getCause() instanceof QueryShardException)
			{
				logger.warn(String.format("%s on index %s", e.getCause().getMessage(), index.getSimpleValue()));
				return new SearchResponseError(request, e.getCause().getMessage());
			} else
			{
				logger.error(String.format("Search failed for %s on index %s", request.getSimpleQueryString(), index.getSimpleValue()), e);
				return new SearchResponseError(request, e.getMessage());
			}
		}

	}
	
	/**
	 * Search an index with a query string and return a List of OneSearchResult 's.
	 * 
	 * @see <a href=
	 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html">query-dsl-query-string-query</a>
	 * 
	 * @param index
	 *            The IndexDefinition
	 * @param request
	 *            The StandardSearchRequest
	 * @param default_value
	 *            The default value should it fail
	 * @return List<OneSearchResult>
	 */
	@Override
	public List<OneSearchResultWithTyping> search(IndexDefinition index, StandardSearchRequest request, List<OneSearchResultWithTyping> default_value)
	{
		if (index == null || request == null)
		{
			logger.warn(String.format("Search failed: Null parameter(s) for %s", request));
			return default_value;
		}

		try
		{
			String index_name = index.getSimpleValue();
			int from = request.getSimpleStartResultsAfter();
			int size = request.getSimpleMaxResults();

			SearchRequestBuilder builder = client.prepareSearch(index_name);
			builder.setTypes(ELASTICSEARCH_DEFAULT_TYPE);
			builder.setFrom(from);
			builder.setSize(size);
			builder.setQuery(QueryBuilders.queryStringQuery(request.getSimpleQueryString()));
			
			// Sorting
			for ( SortBy sort_by : request.getSimpleSort().getSimpleSortOrder() )
			{
				FieldSortBuilder sort_builder = getSort(sort_by, null);
				if ( sort_builder == null ) continue;
								
				builder.addSort(sort_builder);
			}

			SearchResponse response = builder.get();

			List<OneSearchResultWithTyping> results = new LinkedList<OneSearchResultWithTyping>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, String[]> map = new TreeMap<FieldName, String[]>();
				hit.getSourceAsMap().forEach((k, v) ->
				{
					FieldName name = new FieldName(k);
					String[] array_val = map.get(name);

					String[] new_array_val = null;

					if (v instanceof ArrayList<?>)
					{
						List<Object> array_as_list = (ArrayList<Object>) v;
						new_array_val = new String[array_as_list.size()];

						for (int i = 0; i < array_as_list.size(); i++)
						{
							new_array_val[i] = String.valueOf(array_as_list.get(i));
						}
					} else
					{
						if (array_val == null)
							array_val = new String[0];
						new_array_val = new String[]
						{ String.valueOf(v) };
					}

					if (new_array_val != null)
						map.put(name, new_array_val);
				});
				results.add(new OneSearchResultWithTyping(map));
			});

			int next_page = from + size;

			boolean has_more_results = response.getHits().totalHits > next_page;

			boolean has_previous_results = from != 0;

			Level level;
			switch (response.status())
			{
			case OK:
				level = Level.INFO;
				break;
			default:
				level = Level.WARN;
				break;
			}

			logger.log(level, String.format("QUERY:%s INDEX:%s STATUS:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", request, index.getSimpleValue(), response.status(), results.size(), response.getHits().totalHits, request.getSimpleMaxResults(), request.getSimpleStartResultsAfter()));
			logger.trace(String.format("FIRST_RESULT_IDX:%s HAS_MORE_RESULTS:%s HAS_PREVIOUS_RESULTS:%s START_OF_NEXT_PAGE_OF_RESULTS:%s START_OF_PREVIOUS_PAGE_OF_RESULTS:%s",from, has_more_results, has_previous_results, next_page, from));
			logger.trace(results.toString());

			return results;

		} catch (Exception e)
		{
			logger.warn(String.format("Search failed for %s", request), e);
			return default_value;
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
	public boolean indexExists(IndexDefinition index)
	{
		if (index == null)
		{
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}
		try
		{
			return client.admin().indices().prepareExists(index.getSimpleValue()).get().isExists();
		} catch (Exception e)
		{
			logger.log(Level.FATAL, "Failed to check if index exists", e);
			return false;
		}
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	@Override
	public boolean indexExists(SearchIndexDefinition index)
	{
		if (index == null)
		{
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}
		try
		{
			return client.admin().indices().prepareExists(index.getSimpleIndex().getSimpleValue()).get().isExists();
		} catch (Exception e)
		{
			logger.log(Level.FATAL, "Failed to check if index exists", e);
			return false;
		}
	}

	/**
	 * An index is properly configured if it exists and its field names and
	 * datatypes match
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index is properly configured or not
	 */
	@Override
	public boolean indexProperlyConfigured(SearchIndexDefinition index)
	{
		if (index == null)
		{
			return false;
		}

		if (indexExists(index))
		{
			// compare the expected index fields to the actual index fields
			Map<String, String> expected = new TreeMap<String, String>();
			index.getSimpleFields().forEach(fields ->
			{
				expected.put(fields.getSimpleFieldName().getSimpleName(), fields.getSimpleType().getSimpleCode());
			});
			
			try
			{
				GetMappingsResponse response = client.admin().indices().prepareGetMappings(index.getSimpleIndex().getSimpleValue()).get();
				ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> response_map = response.getMappings();

				for (ObjectCursor<String> typeName : response_map.keys())
				{
					ImmutableOpenMap<String, MappingMetaData> typeMapping = response_map.get(typeName.value);
					String json = response.getMappings().get(typeName.value).get(ELASTICSEARCH_DEFAULT_TYPE).source().string();

					Map<String, String> actual = new TreeMap<String, String>();

					new ObjectMapper().readTree(json).get(ELASTICSEARCH_DEFAULT_TYPE).get("properties").fields().forEachRemaining(fieldMapping ->
					{
						if (!fieldMapping.getKey().contains(SORT_FIELD_NAME_JIMMUTABLE)) // Skip our keyword fields
						{
							actual.put(fieldMapping.getKey(), fieldMapping.getValue().get("type").asText());
						}
					});

					if (!expected.equals(actual))
					{

						logger.warn(String.format("Index: %s not properly configured", index.getSimpleIndex().getSimpleValue()));
						logger.warn(String.format("Expected fields=%s", expected));
						logger.warn(String.format("Actual   fields=%s", actual));

						if (expected.size() > actual.size())
						{
							logger.warn("There are field missing in the current index.");
						}
						else if (expected.size() < actual.size())
						{
							logger.info("There are more fields than expected in the current index.");
						}
						//
						//
						// for (String key : expected.keySet())
						// {
						// String expected_value = expected.get(key);
						// String actual_value = actual.get(key);
						// if (!expected_value.equals(actual_value))
						// {
						// logger.info(String.format("Issue lies in that for Field %s the expected field
						// value is: %s", key, expected_value));
						// logger.info(String.format("However, currently for Field %s the actual field
						// value is: %s", key, actual_value));
						// }
						// }

						return false;
					}
				}
			}
			catch (Exception e)
			{
				logger.log(Level.FATAL, String.format("Failed to get the index mapping for index %s", index.getSimpleIndex().getSimpleValue()), e);
			}
			
			return true;
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
	public boolean deleteIndex(SearchIndexDefinition index)
	{
		//TODO This was instructed to not be refactored when we made the cutover to use aliases. Generally reindexing can be used over this.
		//It will likely not work if the interface is ever updated to use deletion of indexes again.
		if (index == null)
		{
			logger.fatal("Cannot delete a null Index");
			return false;
		}

		try
		{
			DeleteIndexResponse deleteResponse = client.admin().indices().prepareDelete(index.getSimpleIndex().getSimpleValue()).get();
			if (!deleteResponse.isAcknowledged())
			{
				logger.fatal(String.format("Index Deletion not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (Exception e)
		{
			logger.fatal(String.format("Index Deletion failed for index %s", index.getSimpleIndex().getSimpleValue()));
			return false;
		}
		logger.info(String.format("Deleted index %s", index.getSimpleIndex().getSimpleValue()));
		return true;

	}

	/**
	 * Upsert if the index doesn't exist or is not properly configured already
	 * 
	 * BE CAREFUL!!!
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the upsert was successful or not
	 */
	@Override
	public boolean upsertIndex(SearchIndexDefinition index)
	{
		if (index == null)
		{
			logger.fatal("Cannot upsert a null Index");
			return false;
		}

		// if it exists and is not configured correctly delete and add
		if (indexExists(index))
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
	
	/**
	 * Upsert the index and attach it to an alias
	 * 
	 * @return boolean if the upsert was successful or not
	 */
	private boolean createIndex(SearchIndexDefinition index)
	{
		if (index == null)
		{
			logger.fatal("Cannot create a null Index");
			return false;
		}
		String timestamp_index_name = createTimestampIndex(index, null);
		if(timestamp_index_name == null)
		{
			logger.fatal("Cannot create a timestamp Index for index " + index);
			return false;
		}
		
		IndicesAliasesResponse response = client.admin().indices().prepareAliases().addAlias(timestamp_index_name, index.getSimpleIndex().getSimpleValue()).execute().actionGet();
		if (!response.isAcknowledged())
		{
			logger.fatal(String.format("Alias Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
			return false;
		}

		logger.info(String.format("Created index %s", index.getSimpleIndex().getSimpleValue()));
		return true;
	}

	/**
	 * Delete a document within an index
	 * 
	 * @param index
	 * @param document_id
	 * @return
	 */
	public boolean deleteDocument(IndexDefinition index, SearchDocumentId document_id)
	{

		if (index == null || document_id == null)
		{
			logger.fatal("Null index or document id");
			return false;
		}

		try
		{
			DeleteResponse response = client.prepareDelete(index.getSimpleValue(), ELASTICSEARCH_DEFAULT_TYPE, document_id.getSimpleValue()).setRefreshPolicy(RefreshPolicy.IMMEDIATE).get();

			logger.info(String.format("Result:%s SearchDocumentId:%s IndexDefinition:%s", response.getResult(), response.getId(), response.getIndex()));

			return response.getResult().equals(Result.DELETED);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}
	}
	
	/**
	 * Using a SortBy object, construct a SortBuilder used by ElasticSearch. This method handles the unique sorting cases for Text, Time of Day, and Instant.
	 * 
	 * @param sort_by
	 * @param default_value
	 * @return
	 */
	static private FieldSortBuilder getSort(SortBy sort_by, FieldSortBuilder default_value)
	{
		SortOrder order = null;
		if ( sort_by.getSimpleDirection() == SortDirection.ASCENDING ) order = SortOrder.ASC;
		if ( sort_by.getSimpleDirection() == SortDirection.DESCENDING ) order = SortOrder.DESC;
		
		if ( order == null ) return default_value;
		
		FieldName field_name = sort_by.getSimpleField().getSimpleFieldName();
		String sort_on_string = field_name.getSimpleName();
		
		if ( sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.TEXT ) sort_on_string = getSortFieldNameText(sort_by.getSimpleField().getSimpleFieldName());
		if ( sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.TIMEOFDAY ) sort_on_string = getSortFieldNameTimeOfDay(sort_by.getSimpleField().getSimpleFieldName());
		if ( sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.INSTANT ) sort_on_string = getSortFieldNameInstant(sort_by.getSimpleField().getSimpleFieldName());
		
		return SortBuilders.fieldSort(sort_on_string).order(order).unmappedType(SearchIndexFieldType.ATOM.getSimpleCode());
	}

	/**
	 * Sorting on text fields is impossible without enabling fielddata in ElasticSearch. To get around this, we instead use a keyword field for every text field written.
	 * This solution is recommended by ElasticSearch over enabling fielddata. For more information, read here:
	 * 
	 * https://www.elastic.co/guide/en/elasticsearch/reference/5.4/fielddata.html#before-enabling-fielddata
	 * https://www.elastic.co/blog/support-in-the-wild-my-biggest-elasticsearch-problem-at-scale
	 * 
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameText(FieldName field)
	{
		return getSortFieldNameText(field.getSimpleName());
	}
	
	/**
	 * Sorting on text fields is impossible without enabling fielddata in ElasticSearch. To get around this, we instead use a keyword field for every text field written.
	 * This solution is recommended by ElasticSearch over enabling fielddata. For more information, read here:
	 * 
	 * https://www.elastic.co/guide/en/elasticsearch/reference/5.4/fielddata.html#before-enabling-fielddata
	 * https://www.elastic.co/blog/support-in-the-wild-my-biggest-elasticsearch-problem-at-scale
	 * 
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameText(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + SearchIndexFieldType.ATOM.getSimpleCode();
	}
	
	/**
	 * In order to sort TimeOfDay objects, we need to look at the ms_from_midnight field from within. This method creates a consistent field name to sort by.
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameTimeOfDay(FieldName field)
	{
		return getSortFieldNameTimeOfDay(field.getSimpleName());
	}
	
	/**
	 * In order to sort TimeOfDay objects, we need to look at the ms_from_midnight field from within. This method creates a consistent field name to sort by.
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameTimeOfDay(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + TimeOfDay.FIELD_MS_FROM_MIDNIGHT.getSimpleFieldName().getSimpleName();
	}
	
	/**
	 * In order to sort Instant objects, we need to look at the ms_from_epoch field from within. This method creates a consistent field name to sort by.
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameInstant(FieldName field)
	{
		return getSortFieldNameInstant(field.getSimpleName());
	}
	
	/**
	 * In order to sort Instant objects, we need to look at the ms_from_epoch field from within. This method creates a consistent field name to sort by.
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameInstant(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + Instant.FIELD_MS_FROM_EPOCH.getSimpleFieldName().getSimpleName();
	}

	/**
	 * For all given kinds, this will go through and create a new unique index
	 * for the kind, scan storage for all of the Kind's Indexable Storables and
	 * upsert their respective search documents. After that is successfully done
	 * this will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name). Once it has found all indices that
	 * reference the alias it will prepare to atomically remove all the old
	 * references and attach the single new index reference to the alias. After
	 * that is successfully done, this will remove all indices that no longer
	 * have a relation to the alias.
	 * 
	 * @param storage
	 *            The storage implementation your App is using
	 * @param kinds
	 *            The kinds that should be reindexed. All kinds passed in must
	 *            be registered with SearchSync in order to be used here
	 * @return is_success
	 */
	@Override
	public boolean reindex(IStorage storage, Kind... kinds)
	{
		if(storage == null)
		{
			logger.error("Null storage passed in for re-indexing");
			return false;
		}
		
		if(kinds == null)
		{
			logger.error("Null kinds passed in for re-indexing");
			return false;
		}
		
		for(Kind kind : kinds)
		{
			SearchIndexDefinition index_definition = SearchSync.getSimpleAllRegisteredIndexableKindsMap().get(kind);
			if(index_definition == null)
			{
				logger.error("Kind " + kind + " passed in for re-indexing is not registered with SearchSync.registerIndexableKind");
				return false;
			}
			
			String index_name = createTimestampIndex(index_definition, null);
			if(index_name == null)
			{
				logger.error("Kind " + kind + " could not create a new index to reindex documents with");
				return false;
			}

			boolean success = syncSearchAndStorage(kind, index_name);
			if(!success)
			{
				logger.error("Kind " + kind + " could not complete sync of storage and search, no swap made");
				if(!deleteIndex(index_name))
				{
					logger.error("Error removing index " + index_name + " on failure.");
				}
				return false;
			}

			//Swap alias 
			success = updateAlias(index_definition, index_name);
			if(!success)
			{
				logger.error("Kind " + kind + " could not complete full alias swap");
				if(!deleteIndex(index_name))
				{
					logger.error("Error removing index " + index_name + " on failure.");
				}
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This will find all the indices that currently reference the
	 * alias(SearchIndexDefinition name). Once it has found all indices that
	 * reference the alias it will prepare to atomically remove all the old
	 * references and attach the single new index reference to the alias. After
	 * that is successfully done, this will remove all indices that no longer
	 * have a relation to the alias.
	 * 
	 * @param definition
	 *            The definition of the Kind that is being reindexed
	 * @param index_name
	 *            The unique index name that data was upserted to
	 * @return is_success
	 */
	private boolean updateAlias(SearchIndexDefinition definition, String index_name)
	{
		
		Set<String> old_index = getCurrentIndiciesFromAliasName(definition.getSimpleIndex().getSimpleValue());
		String[] indices = old_index.toArray(new String[old_index.size()]);

		//Has to be atomic so if it fails we don't add a new alias
		try
		{
			IndicesAliasesResponse response = client.admin().indices().prepareAliases().addAlias(index_name, definition.getSimpleIndex().getSimpleValue()).removeAlias(indices, definition.getSimpleIndex().getSimpleValue()).execute().actionGet();
			if (!response.isAcknowledged())
			{
				logger.fatal(String.format("Alias addition not acknowledged for index %s", index_name));
				return false;
			}
		}
		catch(Exception e)
		{
			logger.error("Alias addition and removal failed", e);
			return false;
		}


		//Once we have confirmed the swap we no longer need the indices that we removed from the alias so we will delete them all
		//This operation could be handled by a reaper thread in the future if we want to keep the job of deleting indices outside of the reindexer
		for(String index : indices)
		{
			if(!deleteIndex(index))
			{
				logger.error("Left over index " + index + " removal failed, continuing so others can be attempted to remove.");
			}
		}

		return true;
	}
	
	private boolean deleteIndex(String index_name)
	{
		try
		{
			DeleteIndexResponse deleteResponse = client.admin().indices().delete(new DeleteIndexRequest(index_name)).get();
			if (!deleteResponse.isAcknowledged())
			{
				logger.fatal(String.format("Alias removal not acknowledged for index %s", index_name));
				return false;
			}
		}
		catch (InterruptedException | ExecutionException e)
		{
			logger.error("Alias removal failed", e);
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
	public Set<String> getCurrentIndiciesFromAliasName(String alias_name)
	{
		Set<String> all_indicies_with_alias = new HashSet<>();

		ClusterStateRequestBuilder builder = client.admin().cluster().prepareState();
		for (IndexMetaData index_meta_data : builder.execute().actionGet().getState().metaData())
		{
			for (ObjectCursor<String> cursor : index_meta_data.getAliases().keys())
			{
				if (cursor.value.equals(alias_name))
				{
					all_indicies_with_alias.add(index_meta_data.getIndex().getName());
					continue;
				}
			}
		}

		return all_indicies_with_alias;
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
	private String createTimestampIndex(SearchIndexDefinition index, String default_value)
	{
		if (index == null)
		{
			logger.fatal("Cannot create a null Index");
			return default_value;
		}

		try
		{
			String index_name = index.getSimpleIndex().getSimpleValue() + "_" + System.currentTimeMillis();
			CreateIndexResponse createResponse = client.admin().indices().prepareCreate(index_name).addMapping(ELASTICSEARCH_DEFAULT_TYPE, getMappingBuilder(index, null)).get();

			if (!createResponse.isAcknowledged())
			{
				logger.fatal(String.format("Index Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return default_value;
			}

			return index_name;
		}
		catch (Exception e)
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return default_value;
		}
	}
	
	
	/**
	 * This will build all the mapping fields for an index defintion
	 * 
	 */
	private static XContentBuilder getMappingBuilder(SearchIndexDefinition index, XContentBuilder default_value)
	{
		try
		{
			XContentBuilder mappingBuilder = jsonBuilder();
			mappingBuilder.startObject().startObject(ELASTICSEARCH_DEFAULT_TYPE).startObject("properties");
			for (SearchIndexFieldDefinition field : index.getSimpleFields())
			{
				// if (field.getSimpleType().equals(SearchIndexFieldType.OBJECTID))
				// {
				// // EXPLICIT MAPPING FOR OBJECTID - does not rely on enum's simple code
				// // https://www.elastic.co/blog/strings-are-dead-long-live-strings
				// mappingBuilder.startObject(field.getSimpleFieldName().getSimpleName());
				// /* */mappingBuilder.field("type", "text");
				// /* */mappingBuilder.startObject("fields");
				// /* */mappingBuilder.startObject("keyword");
				// /* */mappingBuilder.field("type", "keyword");
				// /* */mappingBuilder.field("ignore_above", 256);
				// /* */mappingBuilder.endObject();
				// /* */mappingBuilder.endObject();
				// mappingBuilder.endObject();
				// } else
				// {
				mappingBuilder.startObject(field.getSimpleFieldName().getSimpleName());
				/*	*/mappingBuilder.field("type", field.getSimpleType().getSimpleCode());
				mappingBuilder.endObject();
				// }

				// Create a keyword for every text field
				if (field.getSimpleType() == SearchIndexFieldType.TEXT)
				{
					mappingBuilder.startObject(getSortFieldNameText(field.getSimpleFieldName()));
					mappingBuilder.field("type", SearchIndexFieldType.ATOM.getSimpleCode());
					mappingBuilder.endObject();
				}

				// Create a long field for every instant field
				if (field.getSimpleType() == SearchIndexFieldType.INSTANT)
				{
					mappingBuilder.startObject(getSortFieldNameInstant(field.getSimpleFieldName()));
					mappingBuilder.field("type", SearchIndexFieldType.LONG.getSimpleCode());
					mappingBuilder.endObject();
				}

				// Create a long field for every timeofday field
				if (field.getSimpleType() == SearchIndexFieldType.TIMEOFDAY)
				{
					mappingBuilder.startObject(getSortFieldNameTimeOfDay(field.getSimpleFieldName()));
					mappingBuilder.field("type", SearchIndexFieldType.LONG.getSimpleCode());
					mappingBuilder.endObject();
				}
			}
			mappingBuilder.endObject().endObject().endObject();

			return mappingBuilder;

		}
		catch (Exception e)
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return default_value;
		}
	}

	/**
	 * Puts all field mappings into an existing index. If the index doesn't already
	 * exist or a field name with a different type already exists the operation will
	 * fail.
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return if successful or not
	 */
	public boolean putAllFieldMappings(SearchIndexDefinition index)
	{

		if (index == null)
		{
			logger.fatal("Null index");
			return false;
		}

		if (!indexExists(index))
		{
			logger.fatal(String.format("Index %s does not exist!", index.getSimpleIndex().getSimpleValue()));
			return false;
		}

		try
		{

			PutMappingResponse put_response = client.admin().indices().preparePutMapping(index.getSimpleIndex().getSimpleValue()).setType(ELASTICSEARCH_DEFAULT_TYPE).setSource(getMappingBuilder(index, null)).get();

			if (!put_response.isAcknowledged())
			{
				logger.fatal(String.format("Put Mappings result not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (Exception e)
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return false;
		}

		return true;

	}
	
	/**
	 * This will scan all of Storage for the Kind passed in and for each item
	 * found it will attempt to upsert its matching search document.
	 * 
	 * @return true on success
	 */
	private boolean syncSearchAndStorage(Kind kind, String index_name)
	{
		if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().scan(kind, new UpsertDataHandler(index_name), 10))
		{
			logger.warn("Storage scanner for Kind " + kind + " was unable to successfully run. This Kind may not be fully re-indexed or there may currently not be any entries of Kind in Storage.");
			return false;
		}
		return true;
	}

	/**
	 * This handles checking an individual file in Storage. It will check that
	 * the item is able to be deserialized as a Storable and Indexable. If it is
	 * we will set our Kind's Indexable to match that of the Object deserialized
	 * if not yet set. Then attempt to upsert the single Object's search
	 * document into Search.
	 */
	private class UpsertDataHandler implements StorageKeyHandler
	{
		private String index_name; 
		
		private UpsertDataHandler(String index_name)
		{
			this.index_name = index_name;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void handle(StorageKey key)
		{
			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null);

			GenericStorableAndIndexable<?> obj = null;
			try
			{
				obj = new GenericStorableAndIndexable(bytes);
			}
			catch (Exception e)
			{
				logger.error("This object from StorageKey " + key + " was unable to be deserialized as a Storable and Indexable object...", e);
				return;
			}
			
			SearchDocumentWriter writer = new SearchDocumentWriter();
			((Indexable) obj.getObject()).writeSearchDocument(writer);
			Map<String, Object> data = writer.getSimpleFieldsMap();

			//Not async so it's only finished once the whole scan does so
			try
			{
				String document_name = ((Indexable) obj.getObject()).getSimpleSearchDocumentId().getSimpleValue();
				IndexResponse response = client.prepareIndex(index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name).setSource(data).get();

				Level level;
				switch (response.getResult())
				{
				case CREATED:
					level = Level.INFO;
					break;
				case UPDATED:
					level = Level.INFO;
					break;
				default:
					level = Level.FATAL;
					break;
				}

				logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, ELASTICSEARCH_DEFAULT_TYPE, document_name, data));

			}
			catch (Exception e)
			{
				logger.log(Level.FATAL, "Failure during upsert operation!", e);
			}

		}
	}
	
	/**
	 * This is a simple class to handle deserializing any Kind's Object and
	 * ensuring that the Kind's Object is both Indexable and Storable.
	 */
	private static class GenericStorableAndIndexable<T>
	{
		private T object;

		@SuppressWarnings("unchecked")
		public GenericStorableAndIndexable(byte[] bytes) throws ValidationException
		{
			StandardObject<?> obj = null;
			try
			{
				obj = StandardObject.deserialize(new String(bytes));
			}
			catch (Exception e)
			{
				throw new ValidationException("Unable to deserialize object", e);
			}

			//Broken out this way, rather than just deserializing T so that we know exactly what a
			if (!(obj instanceof Storable))
			{
				throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Storable.");
			}
			if (!(obj instanceof Indexable))
			{
				throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Indexable.");
			}

			this.object = (T) obj;
		}

		public T getObject()
		{
			return object;
		}
	}
}
