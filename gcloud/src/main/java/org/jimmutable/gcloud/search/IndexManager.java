package org.jimmutable.gcloud.search;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.gcloud.logging.LogSupplier;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.OperationResult;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.PutResponse;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

public class IndexManager {

	private static final Logger logger = Logger.getLogger(IndexManager.class.getName());

	private static Index getIndex(IndexId indexId) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexId.getSimpleValue()).build();
		return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	}

	/**
	 * Performs a get operation on the index for the given DocumentId
	 * 
	 * @param index_id
	 *            IndexId
	 * @param document_id
	 *            DocumentId
	 * @param default_value
	 *            The Document to return in the event the document is not found
	 * @return
	 */
	public static Document getComplex(IndexId index_id, DocumentId document_id, Document default_value) {
		Index index = getIndex(index_id);
		Document d = index.get(document_id.getSimpleValue());
		if (d != null) {
			return d;
		}
		return default_value;
	}

	/**
	 * Performs a deletion on a DocumentId within an IndexId and will retry up to
	 * three times in the even a runtimeException is thrown.
	 * 
	 * @param index_id
	 *            IndexId
	 * @param document_id
	 *            DocumentId
	 * @throws InterruptedException
	 */
	public static void delete(IndexId index_id, DocumentId document_id) throws InterruptedException {
		Index index = getIndex(index_id);

		final int maxRetry = 3;
		int attempts = 0;
		int delay = 2;
		while (attempts < maxRetry) {
			attempts++;
			try {
				index.delete(index_id.getSimpleValue());
				logger.info(new LogSupplier("Deleted documentId %s in indexId %s", index_id.getSimpleValue(),
						document_id.getSimpleValue()));
				break;
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, e, new LogSupplier("Failed to delete documentId %s in indexId %s"));
				Thread.sleep(delay * 1000);
				delay *= 2; // easy exponential backoff
			}
		}
	}

	/**
	 * Performs a deletion on a DocumentId within an IndexId and will retry up to
	 * three times in the even a runtimeException is thrown.
	 * 
	 * @param indexable
	 *            Indexable
	 * @throws InterruptedException
	 */
	public static void delete(Indexable indexable) throws InterruptedException {
		delete(indexable.getSimpleSearchIndexId(), indexable.getSimpleSearchDocumentId());
	}

	/**
	 * 
	 * Creates a Document using DocumentWriter.createDocument() and puts it into the
	 * index. This will retry up to three times if the put fails from a system
	 * related issue.
	 * 
	 * @param indexable
	 *            Indexable
	 * @throws InterruptedException
	 */
	public static void upsert(Indexable indexable) throws InterruptedException {

		Index index = getIndex(indexable.getSimpleSearchIndexId());

		DocumentWriter writer = new DocumentWriter(indexable.getSimpleSearchDocumentId());
		indexable.writeSearchDocument(writer);

		final int maxRetry = 3;
		int attempts = 0;
		int delay = 2;
		while (true) {
			try {
				index.put(writer.createDocument());
				logger.info(new LogSupplier("Upserted documentId %s into indexId %s",
						indexable.getSimpleSearchDocumentId().getSimpleValue(),
						indexable.getSimpleSearchIndexId().getSimpleValue()));
			} catch (PutException e) {
				if ((StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
						|| StatusCode.CONCURRENT_TRANSACTION_ERROR.equals(e.getOperationResult().getCode())
						|| StatusCode.INTERNAL_ERROR.equals(e.getOperationResult().getCode())
						|| StatusCode.TIMEOUT_ERROR.equals(e.getOperationResult().getCode()))
						&& ++attempts < maxRetry) { // retrying

					logger.log(Level.WARNING, e, new LogSupplier(
							"Upsert for documentId %s into indexId %s failed with status code %s. Retrying in %d seconds...",
							indexable.getSimpleSearchDocumentId().getSimpleValue(),
							indexable.getSimpleSearchIndexId().getSimpleValue(),
							e.getOperationResult().getCode().name(), delay));

					Thread.sleep(delay * 1000);
					delay *= 2; // easy exponential backoff
					continue;
				} else {

					logger.log(Level.SEVERE, e,
							new LogSupplier("Upsert for documentId %s into indexId %s failed with status code %s",
									indexable.getSimpleSearchDocumentId().getSimpleValue(),
									indexable.getSimpleSearchIndexId().getSimpleValue(),
									e.getOperationResult().getCode().name()));

					// PutException is a RuntimeException
					throw e;
				}
			}
			break;
		}
	}

}
