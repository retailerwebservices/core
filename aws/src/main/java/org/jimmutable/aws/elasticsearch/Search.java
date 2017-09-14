package org.jimmutable.aws.elasticsearch;

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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

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
	 * Upsert a document to an index
	 * 
	 * @param object
	 *            The Indexable object
	 * @return boolean
	 * @throws InterruptedException
	 */
	public boolean upsertDocumentAsync(Indexable object) throws InterruptedException
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

	// TODO

	// public JSONResponse search(IndexDefinition index, StandardSearchRequest
	// request)
	public String search(IndexDefinition index, String queryString)
	{

		if (index == null || queryString == null) {
			// return "";
		}

		try {

			String index_name = index.getSimpleValue();
			String index_type = Indexable.DEFAULT_TYPE;

			SearchResponse response = client.prepareSearch(index_name).setTypes(index_type).setQuery(QueryBuilders.queryStringQuery(queryString)).get();

			SearchHits hits = response.getHits();

			Map<String, Map<String, Object>> documents = new HashMap<String, Map<String, Object>>();

			hits.forEach(hit -> {
				documents.put(hit.getId(), hit.getSourceAsMap());
				// hit.docId();
				// hit.getSourceAsMap().forEach((k, v) -> {
				// logger.info(String.format("%s %s %s", hit.getId(), k, v));
				// });
			});

			Level level;
			switch (response.status()) {
			case OK:
				level = Level.INFO;
				break;
			default:
				level = Level.WARN;
				break;
			}
			logger.log(level, String.format("Status:%s Index:%s/%s Query:%s Total hits:%s Took:%sms Documents:%s", response.status(), index_name, index_type, queryString, documents.size(), response.getTookInMillis(), documents.toString()));
		} catch (Exception e) {
			logger.fatal("Failure during search!", e);
		}

		return null;
	}

}
