package org.jimmutable.aws.elasticsearch;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;

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
	 * Gracefully shutdown the running threads and then the elasticsearch
	 * TransportClient
	 * 
	 * @return
	 */
	public boolean shutdown()
	{
		pool.shutdown();

		boolean terminated = true;

		try {
			terminated = pool.awaitTermination(25, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, "Shutdown of runnable pool was interrupted!", e);
		}

		if (!terminated) {
			pool.shutdownNow();
		}

		client.close();
		return pool.isTerminated();
	}

	public boolean upsertDocumentAsync(Indexable object) throws InterruptedException
	{

		if (object == null) {
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);

		pool.execute(new Runnable()
		{
			@Override
			public void run()
			{

				try {

					String index_name = object.getSimpleSearchIndexDefinition().getSimpleValue();
					String inted_type = Indexable.DEFAULT_TYPE;
					String document_name = object.getSimpleSearchDocumentId().getSimpleValue();
					Map<String, Object> data = writer.getSimpleFieldsMap();

					IndexResponse response = client.prepareIndex(index_name, inted_type, document_name).setSource(data).get();

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

					logger.log(level, String.format("%s %s/%s/%s %s", response.getResult().name(), index_name, inted_type, document_name, data));

				} catch (Exception e) {
					logger.log(Level.FATAL, "Failure during upsert operation!", e);
				}
			}
		});

		return true;
	}

	// TODO

	// public JSONResponse search(IndexDefinition index, StandardSearchRequest
	// request)
	// {
	// return null;
	// }

}
