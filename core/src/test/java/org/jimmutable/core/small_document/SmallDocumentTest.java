package org.jimmutable.core.small_document;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jimmutable.core.small_document.SmallDocumentReader;
import org.jimmutable.core.small_document.SmallDocumentSource;
import org.jimmutable.core.small_document.SmallDocumentWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SmallDocumentTest  extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SmallDocumentTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SmallDocumentTest.class );
    }

    public void testOnStaticDocuments()
    {
    	String documents = " <?a?>A<?a?> <?foo?>B<?foo?> <?z?>C<?z?> <?a?>--end-of-file--<?a?>";
    	
    	SmallDocumentReader r = new SmallDocumentReader(new StringReader(documents));
    	
    	assertEquals(r.getSimpleState(),SmallDocumentSource.State.READ_DOCUMENT_NOT_YET_ATTEMPTED);
    	
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.DOCUMENT_AVAILABLE);
    	assertEquals(r.getCurrentDocument(null),"A");
    	
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.DOCUMENT_AVAILABLE);
    	assertEquals(r.getCurrentDocument(null),"B");
    	
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.DOCUMENT_AVAILABLE);
    	assertEquals(r.getCurrentDocument(null),"C");
    	
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.NO_MORE_DOCUMENTS);
    	
    	assertEquals(r.getSimpleState(),SmallDocumentSource.State.NO_MORE_DOCUMENTS);
    }
    
    public void testReadingOfContentPreparedByWriter()
    {
    	int size = 100;
    	
    	String documents = createNDocumentsUsingWriter(size);
    	
    	SmallDocumentReader r = new SmallDocumentReader(new StringReader(documents));
    	
    	assertEquals(r.getSimpleState(),SmallDocumentSource.State.READ_DOCUMENT_NOT_YET_ATTEMPTED);

    	for ( int i = 0; i < size; i++ )
    	{
    		assertEquals(r.readNextDocument(), SmallDocumentSource.State.DOCUMENT_AVAILABLE);
    		assertEquals(r.getCurrentDocument(null),createDocumentN(i));
    	}

    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.NO_MORE_DOCUMENTS);
    	assertEquals(r.getSimpleState(),SmallDocumentSource.State.NO_MORE_DOCUMENTS);
    }
    
    public void testZeroRead()
    {
    	SmallDocumentReader r = new SmallDocumentReader(new StringReader(""));
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.ERROR_ENCOUNTERED);
    }
    
    public void testNonsenseRead()
    {
    	SmallDocumentReader r = new SmallDocumentReader(new StringReader("asdfasdfasdfasdfasdf"));
    	assertEquals(r.readNextDocument(), SmallDocumentSource.State.ERROR_ENCOUNTERED);
    }
    
    private String createNDocumentsUsingWriter(int size)
    {
    	try
    	{
	    	StringWriter string_writer = new StringWriter();
	    	SmallDocumentWriter out = new SmallDocumentWriter(string_writer);
	    	
	    	for ( int i = 0; i < size; i++ )
	    	{
	    		out.writeDocument(createDocumentN(i));
	    	}
	    	
	    	out.close();
	    	
	    	return string_writer.toString();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		assert(false);
    		return null;
    	}
    }
    
    private String createDocumentN(int number)
    {
    	StringBuilder ret = new StringBuilder();
    	
    	while(ret.length() < 2048)
    	{
    		ret.append(String.format("-%d-", number));
    	}
    	
    	return ret.toString();
    }
}

