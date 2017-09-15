package org.jimmutable.cloud.elasticsearch;

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

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.serialization.FieldName;

/**
 * Use this class for general searching and document upserts with Elasticsearch
 * 
 * @author trevorbox
 *
 */
public class Search
{

	private static final Logger logger = LogManager.getLogger(Search.class);

	private static final ExecutorService pool = (ExecutorService) new ThreadPoolExecutor(8, 8, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

	private volatile TransportClient client;

	public Search(TransportClient client)
	{
		this.client = client;
	}

	/**
	 * Gracefully shutdown the running threads. Note: the TransportClient should be
	 * closed where instantiated. This is not handles by this.
	 * 
	 * @return
	 */
	public boolean shutdownThreadPool()
	{
		pool.shutdown();

		boolean terminated = true;

		try {
			terminated = pool.awaitTermination(25, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, "Shutdown of runnable pool was interrupted!", e);
		}

		if (!terminated) {
			pool.shutdownNow();
		}

		return pool.isTerminated();
	}

	/**
	 * Upsert a document to a search index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean
	 * @throws InterruptedException
	 */
	public boolean upsertDocumentAsync(Indexable object)
	{

		if (object == null) {
			logger.error("Null object!");
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);

		try {
			pool.execute(new Runnable()
			{
				@Override
				public void run()
				{

					try {

						String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
						String index_type = Indexable.DEFAULT_TYPE;
						String document_name = object.getSimpleSearchDocumentId().getSimpleValue();
						Map<String, Object> data = writer.getSimpleFieldsMap();

						IndexResponse response = client.prepareIndex(index_name, index_type, document_name).setSource(data).get();

						Level level;
						switch (response.getResult()) {
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

						logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, index_type, document_name, data));

					} catch (Exception e) {
						logger.log(Level.FATAL, "Failure during upsert operation!", e);
					}
				}
			});
		} catch (Exception e) {
			logger.log(Level.FATAL, "Failure during thread pool execution!", e);
			return false;
		}

		return true;
	}

	/**
	 * Search an index with a query string.
	 * 
	 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
	 * 
	 * @param index
	 * @param request
	 * @return
	 */
	public JSONServletResponse search(IndexDefinition index, StandardSearchRequest request)
	{

		if (index == null || request == null) {
			return new SearchResponseError(request, "Null parameter(s)!");
		}

		try {

			String index_name = index.getSimpleValue();
			String index_type = Indexable.DEFAULT_TYPE;
			int from = request.getSimpleStartResultsAfter();
			int size = request.getSimpleMaxResults();

			SearchRequestBuilder builder = client.prepareSearch(index_name);
			builder.setTypes(index_type);
			builder.setFrom(from);
			builder.setSize(size);
			builder.setQuery(QueryBuilders.queryStringQuery(request.getSimpleQueryString()));

			SearchResponse response = builder.get();

			List<OneSearchResult> results = new LinkedList<OneSearchResult>();

			response.getHits().forEach(hit -> {
				Map<FieldName, String> map = new HashMap<FieldName, String>();
				hit.getSourceAsMap().forEach((k, v) -> {
					map.put(new FieldName(k), v.toString());
				});
				results.add(new OneSearchResult(map));
			});

			int next_page = from + size;

			// if the size was met try and see if there are more results

			logger.info(String.format("TOTAL:%s SIZE:%s", response.getHits().totalHits, response.getHits().getHits().length));

			boolean has_more_results = response.getHits().totalHits > next_page;

			boolean has_previous_results = from != 0;

			Level level;
			switch (response.status()) {
			case OK:
				level = Level.INFO;
				break;
			default:
				level = Level.WARN;
				break;
			}

			SearchResponseOK ok = new SearchResponseOK(request, results, from, has_more_results, has_previous_results, next_page, from);

			logger.log(level, String.format("Status:%s Hits:%s TotalHits:%s StandardSearchRequest:%s first_result_idx:%s has_more_results:%s has_previous_results:%s start_of_next_page_of_results:%s start_of_previous_page_of_results:%s", response.status(), results.size(), response.getHits().totalHits, ok.getSimpleSearchRequest(), ok.getSimpleFirstResultIdx(), ok.getSimpleHasMoreResults(), ok.getSimpleHasMoreResults(), ok.getSimpleStartOfNextPageOfResults(), ok.getSimpleStartOfPreviousPageOfResults()));
			logger.trace(ok.getSimpleResults().toString());

			return ok;

		} catch (Exception e) {
			logger.warn(String.format("Search failed for %s", request), e);
			return new SearchResponseError(request, e.getMessage());
		}

	}

}
