package org.jimmutable.gcloud.search;

/**
 * Any class that can be added to a search index should implement the Indexable
 * interface.
 * 
 * @author kanej
 *
 */
public interface Indexable 
{
	/**
	 * Get the index id that this object should be added to
	 * @return An IndexId
	 */
	public IndexId getSimpleSearchIndexId();
	
	/**
	 * Get the search document id. Frequently (but not always) this is also the
	 * ObjectId of the object.
	 * 
	 * @return The search document id for the document
	 */
	public DocumentId getSimpleSearchDocumentId();
	
	/**
	 * Write (using DocumentWriter) the search document coresponding to this object
	 * 
	 * @param writer
	 *            The DocumentWriter to write to
	 */
	public void writeSearchDocument(DocumentWriter writer);
}
