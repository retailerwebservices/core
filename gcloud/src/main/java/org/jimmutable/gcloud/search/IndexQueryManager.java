package org.jimmutable.gcloud.search;

import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

/**
 * Query an index
 * 
 * @author trevorbox
 *
 */
public class IndexQueryManager {

	private static Index getIndex(IndexId indexId) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexId.getSimpleValue()).build();
		return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	}

	public static Results<ScoredDocument> search(IndexQuery query) {

		IndexQueryWriter w = new IndexQueryWriter();
		query.writeQuery(w);

		Index index = getIndex(query.getSimpleIndexId());

		Results<ScoredDocument> results = null;

		final int maxRetry = 3;
		int attempts = 0;
		int delay = 2;
		while (true) {
			try {
				results = index.search(w.createQuery());
			} catch (SearchException e) {
				if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode()) && ++attempts < maxRetry) {
					// retry
					try {
						Thread.sleep(delay * 1000);
					} catch (InterruptedException e1) {
						// ignore
					}
					delay *= 2; // easy exponential backoff
					continue;
				} else {
					throw e;
				}
			}
			break;
		}

		return results;

	}

}
