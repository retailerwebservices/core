package org.jimmutable.core.small_document;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;


public class SmallDocumentReader extends SmallDocumentSource
{
	static public final int MAXIMUM_DELIMITER_LENGTH_IN_CHARACTERS 			= 32;
	static public final int MAXIMUM_DOCUMENT_LENGTH_IN_CHARACTERS			= 10*1024*1024; // Maximum document size, roughly 10 MB
	
	static public final String EOF_DOCUMENT = "--end-of-file--";
	
	private Reader reader;
	
	private String buffer = "";
	private char grow_buffer[] = new char[32*1024]; // 32 KB reusable buffer
	
	private SmallDocumentSource.State state;
	private String current_document = null;
	
	public SmallDocumentReader(Reader reader)
	{
		this.reader = reader;
		this.state = State.READ_DOCUMENT_NOT_YET_ATTEMPTED;
	}
	
	public State readNextDocument()
	{
		if ( state == State.NO_MORE_DOCUMENTS ) return state;  // at EOF, don't try another read...
		if ( state == State.ERROR_ENCOUNTERED ) return state; // an error occurred, don't try another read
		
		FindDocumentResult document = findFirstDocument(buffer,null);
		
		if ( document != null )
		{
			buffer = document.getRemainder();
			current_document = document.getDocument();
			
			if ( isEOFDocument(current_document) )
				return state = state.NO_MORE_DOCUMENTS;
			
			return state = State.DOCUMENT_AVAILABLE;
		}
		
		if ( growBuffer() )
		{
			return readNextDocument();
		}
		
		return state = State.ERROR_ENCOUNTERED; // Can't grow, did not find an EOF document, so, an error (probably a truncated input stream)
	}
	
	public State getSimpleState()
	{
		return state;
	}

	public String getCurrentDocument(String default_value)
	{
		if ( current_document == null ) return default_value;
		if ( state != State.DOCUMENT_AVAILABLE ) return default_value;
		
		return current_document;
	}

	private boolean growBuffer()
	{
		if ( buffer.length() >= MAXIMUM_DOCUMENT_LENGTH_IN_CHARACTERS ) return false; // can't grow the buffer anymore, we are over the maximum allowable buffer size
		
		try
		{
			int amount_read = reader.read(grow_buffer);
			
			if ( amount_read == -1 ) return false; // unable to grow the buffer because we are at the EOF
			
			buffer += new String(grow_buffer,0,amount_read); // grow the buffer
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	static public class FindDocumentResult
	{
		private int container_start;
		private int document_start;
		
		private int document_end;
		private int container_end;
		
		private String buffer;
		
		public FindDocumentResult(String buffer, int container_start, int document_start, int document_end, int container_end)
		{
			this.buffer = buffer;
			
			this.container_start = container_start;
			this.document_start = document_start;
			
			this.document_end = document_end;
			this.container_end = container_end;
		}
		
		public String toString() { return String.format("{container_start:%d, document_start:%d, document_end:%d, container_end:%d}", container_start, document_start, document_end, container_end); }
		
		public String getDocument() { return buffer.substring(document_start, document_end); }
		public String getRemainder() { return buffer.substring(container_end); }
	} 
	
	static public FindDocumentResult findFirstDocument(String buffer, FindDocumentResult default_value)
	{
		if ( buffer == null ) return default_value;
		
		int container_start = buffer.indexOf("<?");
		if ( container_start == -1 ) return default_value;
		
		int document_start = buffer.indexOf("?>",container_start)+2;
		
		if ( document_start <= 1 ) return default_value;
		
		String label = buffer.substring(container_start,document_start);
		
		if ( label.length() > MAXIMUM_DELIMITER_LENGTH_IN_CHARACTERS ) return default_value;
		
		int document_end = buffer.indexOf(label,document_start);
		if ( document_end == -1 ) return default_value;
		
		int container_end = document_end + label.length();
		
		return new FindDocumentResult(buffer, container_start, document_start, document_end, container_end);
	}
	
	
	
	static public boolean isEOFDocument(String document)
	{
		if ( document == null ) return false;
		return document.equals(EOF_DOCUMENT);
	} 
	
	static public void main(String args[])
	{
		String documents = " <?a?>A<?a?> <?foo?>B<?foo?> <?z?>C<?z?> <?a?>--end-of-file--<?a?>";
		
		System.out.println(findFirstDocument(documents, null));
		System.out.println(findFirstDocument(documents, null).getDocument());
		System.out.println(findFirstDocument(documents, null).getRemainder());
	}
}
