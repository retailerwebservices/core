package org.jimmutable.aws.elasticsearch;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.client.transport.TransportClient;

public class Search
{

	private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(8, 8, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

	private static TransportClient CLIENT;

	public boolean upsertDocumentAsync(SearchIndexDefinition index, Indexable object)
	{

		if (index == null || object == null) {
			return false;
		}

		SearchDocumentWriter writer = new SearchDocumentWriter();
		object.writeSearchDocument(writer);

		EXECUTOR.execute(new Runnable()
		{
			@Override
			public void run()
			{
				CLIENT.prepareIndex(index.getSimpleIndex().getSimpleValue(), Indexable.DEFAULT_TYPE, object.getSimpleSearchDocumentId().getSimpleValue()).setSource(writer.getSimpleFieldsMap()).get();
			}
		});

		return true;
	}

	// public JSONResponse search(IndexDefinition index, StandardSearchRequest
	// request)
	// {
	// return null;
	// }

}
