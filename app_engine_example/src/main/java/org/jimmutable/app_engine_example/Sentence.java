package org.jimmutable.app_engine_example;

import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.DocumentWriter;
import org.jimmutable.gcloud.search.IndexId;
import org.jimmutable.gcloud.search.Indexable;

public class Sentence implements Indexable {

	private static final FieldDefinition.String FIELD_THE_SENTENCE = new FieldDefinition.String("title", "");

	private String theSentence;

	private static final IndexId indexId = new IndexId("MyNewIndexable");

	private DocumentId documentId;

	public Sentence(String documentId, String theSentence) {
		this.documentId = new DocumentId(documentId);
		this.theSentence = theSentence;
	}

	@Override
	public IndexId getSimpleSearchIndexId() {
		return indexId;
	}

	@Override
	public DocumentId getSimpleSearchDocumentId() {
		return documentId;
	}

	@Override
	public void writeSearchDocument(DocumentWriter writer) {
		writer.writeText(FIELD_THE_SENTENCE, this.theSentence);
	}

}
