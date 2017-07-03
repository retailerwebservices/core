package org.jimmutable.core.small_document;

abstract public class SmallDocumentSource
{
	abstract public State readNextDocument();
	abstract public State getSimpleState();
	abstract public String getCurrentDocument(String default_value);
	
	static public enum State
	{
		READ_DOCUMENT_NOT_YET_ATTEMPTED,
		DOCUMENT_AVAILABLE,
		NO_MORE_DOCUMENTS,
		ERROR_ENCOUNTERED;
	}
	
}
