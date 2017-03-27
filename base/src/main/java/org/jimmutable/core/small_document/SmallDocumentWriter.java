package org.jimmutable.core.small_document;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jimmutable.core.serialization.RandomData;

public class SmallDocumentWriter
{
	// Statistics variables
	private long time_of_first_write = 0;
	private int document_count = 0;
	
	private Writer out;
	private RandomData random_data = new RandomData();
	private String last_delimiter = null;
	
	
	public SmallDocumentWriter(Writer out)
	{
		this.out = out;
	}
	
	public SmallDocumentWriter(OutputStream out)
	{
		this(new OutputStreamWriter(out));
	}
	
	public void writeDocument(String xml) throws IOException
	{
		if ( xml == null ) return;
		if ( document_count == 0 ) time_of_first_write = System.currentTimeMillis();
		
		String delimiter = createDelimeter(xml);
		
		out.write(delimiter);
		out.write(xml);
		out.write(delimiter);
		out.write("\n");
		
		document_count++;
	}
	
	public int getDocumentCount() { return document_count; }
	public boolean hasWrittenFirstDocument() { return time_of_first_write != 0; }
	public long getTimeOfFirstWrite() { return time_of_first_write; }
	
	public void close() throws IOException
	{
		writeDocument(SmallDocumentReader.EOF_DOCUMENT);
		out.close();
	}
	
	private String createDelimeter(String xml)
	{
		while(true)
		{
			if ( last_delimiter != null )
			{
				if ( xml == null || xml.indexOf(last_delimiter) == -1 )
					return last_delimiter;
			}
			
			last_delimiter = String.format("<?%s?>", random_data.randomStringOfLength(RandomData.ALPHABET_ALPHA_NUMERIC, 7));
		}
	}

	public void printStatus(int every_n)
	{
		if ( document_count % every_n != 0 ) return;
		printStatus();
	}
	
	public void printStatus()
	{
		System.out.println(String.format("Write documents: %,d documents written in %,d ms, %,d MB RAM used"
				, document_count
				, System.currentTimeMillis()-time_of_first_write
				, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
	}
	
	public void flush() throws IOException
	{
		out.flush();
	}
}
