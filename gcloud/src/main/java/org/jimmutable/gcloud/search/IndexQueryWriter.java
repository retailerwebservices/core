package org.jimmutable.gcloud.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Query;

public class IndexQueryWriter {

	Query.Builder builder;

	String query;

	public IndexQueryWriter() {
		builder = Query.newBuilder();
	}

	public void setQueryString(String queryString) {
		// TODO add stuff
		query = queryString;
	}

	public Query createQuery() {
		return builder.build(query);
	}

}
