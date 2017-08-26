package org.jimmutable.gcloud.search;

public interface IndexQuery {

	public IndexId getSimpleIndexId();

	public void writeQuery(IndexQueryWriter writer);

}
