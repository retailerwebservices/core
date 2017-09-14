package org.jimmutable.cloud.elasticsearch;

/**
 * Any class that can be added to a search index should implement the Indexable
 * interface.
 *
 * @author kanej
 *
 */
public interface Indexable
{

    public static final String DEFAULT_TYPE = "default";

    /**
     * Get the index definition that this object should be added to
     * 
     * @return An IndexDefinition
     */
    public IndexDefinition getSimpleSearchIndexDefinition();

    /**
     * Get the search document id. Frequently (but not always) this is also the
     * ObjectId of the object.
     * 
     * @return The search document id for the document
     */
    public SearchDocumentId getSimpleSearchDocumentId();

    /**
     * Write (using SearchDocumentWriter) the search document correcponding to this
     * object
     * 
     * @param writer
     *            The DocumentWriter to write to
     */
    public void writeSearchDocument(SearchDocumentWriter writer);
}