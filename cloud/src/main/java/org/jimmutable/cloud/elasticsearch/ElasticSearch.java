package org.jimmutable.cloud.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.NotSerializableExceptionWrapper;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.serialization.FieldName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;

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
			logger.log(Level.FATAL, "Failure during upsert operation!", e);
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

			SearchResponse response = builder.get();

			List<OneSearchResult> results = new LinkedList<OneSearchResult>();

			response.getHits().forEach(hit ->
			{
				Map<FieldName, String> map = new HashMap<FieldName, String>();
				hit.getSourceAsMap().forEach((k, v) ->
				{
					map.put(new FieldName(k), v.toString());
				});
				results.add(new OneSearchResult(map));
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

			SearchResponseOK ok = new SearchResponseOK(request, results, from, has_more_results, has_previous_results, next_page, from);

			logger.log(level, String.format("QUERY:%s INDEX:%s STATUS:%s HITS:%s TOTAL_HITS:%s MAX_RESULTS:%d START_RESULTS_AFTER:%d", ok.getSimpleSearchRequest().getSimpleQueryString(), index.getSimpleValue(), response.status(), results.size(), response.getHits().totalHits, ok.getSimpleSearchRequest().getSimpleMaxResults(), ok.getSimpleSearchRequest().getSimpleStartResultsAfter()));
			logger.trace(String.format("FIRST_RESULT_IDX:%s HAS_MORE_RESULTS:%s HAS_PREVIOUS_RESULTS:%s START_OF_NEXT_PAGE_OF_RESULTS:%s START_OF_PREVIOUS_PAGE_OF_RESULTS:%s", ok.getSimpleFirstResultIdx(), ok.getSimpleHasMoreResults(), ok.getSimpleHasMoreResults(), ok.getSimpleStartOfNextPageOfResults(), ok.getSimpleStartOfPreviousPageOfResults()));
			logger.trace(ok.getSimpleResults().toString());

			return ok;

		} catch (Exception e)
		{
			logger.warn(String.format("Search failed for %s", request), e);
			return new SearchResponseError(request, e.getMessage());
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
			Map<String, String> expected = new HashMap<String, String>();
			index.getSimpleFields().forEach(fields ->
			{
				expected.put(fields.getSimpleFieldName().getSimpleName(), fields.getSimpleType().getSimpleCode());
			});

			try
			{
				GetMappingsResponse response = client.admin().indices().prepareGetMappings(index.getSimpleIndex().getSimpleValue()).get();

				String json = response.getMappings().get(index.getSimpleIndex().getSimpleValue()).get(ELASTICSEARCH_DEFAULT_TYPE).source().string();

				Map<String, String> actual = new HashMap<String, String>();

				new ObjectMapper().readTree(json).get(ELASTICSEARCH_DEFAULT_TYPE).get("properties").fields().forEachRemaining(fieldMapping ->
				{
					actual.put(fieldMapping.getKey(), fieldMapping.getValue().get("type").asText());
				});

				if (!expected.equals(actual))
				{

					logger.info("Index not properly configured");
					logger.info(String.format("Expected fields=%s", expected));
					logger.info(String.format("Actual   fields=%s", actual));
					return false;

				}

				return true;

			} catch (Exception e)
			{
				logger.log(Level.FATAL, String.format("Failed to get the index mapping for index %s", index.getSimpleIndex().getSimpleValue()), e);
			}
		}

		return false;

	}

	private boolean createIndex(SearchIndexDefinition index)
	{
		if (index == null)
		{
			logger.fatal("Cannot create a null Index");
			return false;
		}

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
			}
			mappingBuilder.endObject().endObject().endObject();

			CreateIndexResponse createResponse = client.admin().indices().prepareCreate(index.getSimpleIndex().getSimpleValue()).addMapping(ELASTICSEARCH_DEFAULT_TYPE, mappingBuilder).get();

			if (!createResponse.isAcknowledged())
			{
				logger.fatal(String.format("Index Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (Exception e)
		{
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return false;
		}

		logger.info(String.format("Created index %s", index.getSimpleIndex().getSimpleValue()));
		return true;
	}

	private boolean deleteIndex(SearchIndexDefinition index)
	{
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
			if (!indexProperlyConfigured(index))
			{
				if (deleteIndex(index))
				{
					return createIndex(index);
				} else
				{
					// deletion failed
					return false;
				}
			}
		} else
		{
			// index is new
			return createIndex(index);
		}

		// index exists and already configured correctly
		logger.info(String.format("No upsert needed for index %s", index.getSimpleIndex().getSimpleValue()));
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
			DeleteResponse response = client.prepareDelete(index.getSimpleValue(), ELASTICSEARCH_DEFAULT_TYPE, document_id.getSimpleValue()).get();

			logger.info(String.format("Result:%s SearchDocumentId:%s IndexDefinition:%s", response.getResult(), response.getId(), response.getIndex()));

			return response.getResult().equals(Result.DELETED);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}
	}

}
