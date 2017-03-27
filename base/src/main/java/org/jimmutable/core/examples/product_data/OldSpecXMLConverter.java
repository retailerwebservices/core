package org.jimmutable.core.examples.product_data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.Parser;
import org.jimmutable.core.small_document.SmallDocumentBulkLoader;
import org.jimmutable.core.small_document.SmallDocumentReader;
import org.jimmutable.core.small_document.SmallDocumentSource;
import org.jimmutable.core.small_document.SmallDocumentWriter;
import org.jimmutable.core.threading.OperationMonitor;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.threading.OperationRunnable.Result;

import com.fasterxml.jackson.databind.util.TokenBuffer;

public class OldSpecXMLConverter 
{
	static private final File FILE_TEST_SMALL_OBJECTS = new File("c:\\test_small_objects.dat");
	
	public OldSpecXMLConverter(File src, File dest, int max_objects) throws Exception
	{
		SmallDocumentWriter out = new SmallDocumentWriter(new FileWriter(dest));
		
		XMLInputFactory factory = XMLInputFactory.newInstance();

		XMLStreamReader reader = factory.createXMLStreamReader(new BufferedReader(new FileReader(src)));

		StringBuilder char_buffer = new StringBuilder();
		ItemSpecifications.Builder builder = new ItemSpecifications.Builder();
		
		String brand = null, pn = null;
		
		int object_count = 0;
		
		long t1 = System.currentTimeMillis();
		
		outer_loop: while(reader.hasNext())
		{
			int event = reader.next();
			
			switch(event)
			{
			case XMLStreamConstants.START_ELEMENT:
				
				if ( reader.getLocalName().equalsIgnoreCase("product_specs") )
				{
					builder = new ItemSpecifications.Builder();
					brand = null;
					pn = null;
				}

				
				char_buffer = new StringBuilder();
				break;
				
			case XMLStreamConstants.CHARACTERS:
				char_buffer.append(reader.getText().trim());
				break;
				
				
			case XMLStreamConstants.END_ELEMENT:
				switch(reader.getLocalName().toLowerCase())
				{
				case "product_specs":
					try 
					{ 
						out.writeDocument(builder.create().serialize(Format.JSON));
						object_count++;
						
						if ( object_count > max_objects ) break outer_loop;
						
						if (object_count % 1_000 == 0 ) 
							System.out.println(String.format("%,d in %,d ms",object_count, System.currentTimeMillis()-t1));
					} 
					catch(Exception e) 
					{
						
					}
					
					
					break;
				case "product_data": 
					break;
				
				case "brand":
					brand = char_buffer.toString();
					
					if ( brand != null && pn != null ) 
					{
						try { builder.setItemKey(new ItemKey(brand,pn)); } catch(Exception e) {}
					}
					
					break;
				
				case "pn":
					pn = char_buffer.toString();
					
					if ( brand != null && pn != null ) 
					{
						try { builder.setItemKey(new ItemKey(brand,pn)); } catch(Exception e) {}
					}
					
					break;
					
				default:
					builder.putAttribute(new ItemAttribute(reader.getLocalName()), char_buffer.toString());
				}
			}
		}
		
		out.close();
	}
	
	static public void timeReadSmallDocumentOnly() throws Exception
	{
		System.out.println("Timing the reading of the small documents only");
		
		SmallDocumentReader r = new SmallDocumentReader(new FileReader(FILE_TEST_SMALL_OBJECTS));
		
		long t1 = System.currentTimeMillis();
		
		int document_count = 0;
		
		while(r.readNextDocument() != SmallDocumentSource.State.NO_MORE_DOCUMENTS )
		{
			String doc = r.getCurrentDocument(null);
			document_count++;
			
			if ( document_count % 1_000 == 0 )
				System.out.println(String.format("%,d in %,d ms",document_count, System.currentTimeMillis()-t1));
		}
		
		System.out.println();
		
		System.out.println(String.format("Finished! %,d in %,d ms",document_count, System.currentTimeMillis()-t1));
	}
	
	static public void testParseTimeOnly() throws Exception
	{
		System.out.println("Timing the parsing of the small documents into ObjectReader(s)");
		
		SmallDocumentReader r = new SmallDocumentReader(new FileReader(FILE_TEST_SMALL_OBJECTS));
		
		long t1 = System.currentTimeMillis();
		
		int document_count = 0;
		
		while(r.readNextDocument() != SmallDocumentSource.State.NO_MORE_DOCUMENTS )
		{
			String doc = r.getCurrentDocument(null);
			
			ObjectParseTree obj_reader = Parser.parse(doc);
			
			document_count++;
			
			if ( document_count % 1_000 == 0 )
				System.out.println(String.format("%,d in %,d ms",document_count, System.currentTimeMillis()-t1));
		}
		
		System.out.println();
		
		System.out.println(String.format("Finished! %,d in %,d ms",document_count, System.currentTimeMillis()-t1));
	}
	
	static public void testReadObjects() throws Exception
	{
		System.out.println("Timing the reading of objects");
		
		SmallDocumentReader r = new SmallDocumentReader(new FileReader(FILE_TEST_SMALL_OBJECTS));
		
		long t1 = System.currentTimeMillis();
		
		int document_count = 0;
		
		while(r.readNextDocument() != SmallDocumentSource.State.NO_MORE_DOCUMENTS )
		{
			String doc = r.getCurrentDocument(null);
			
			ItemSpecifications specs = (ItemSpecifications)StandardObject.deserialize(doc);
			
			document_count++;
			
			if ( document_count % 1_000 == 0 )
				System.out.println(String.format("%,d in %,d ms",document_count, System.currentTimeMillis()-t1));
		}
		
		System.out.println();
		
		System.out.println(String.format("Finished! %,d in %,d ms",document_count, System.currentTimeMillis()-t1));
	}
	
	static private class MyListener implements SmallDocumentBulkLoader.Listener, OperationMonitor
	{
		private int object_count = 0;
		
		public void onObjectLoaded(StandardObject object) 
		{
			object_count++;
		}

		
		public void onOperationMonitorHeartbeat(OperationRunnable runnable) 
		{
			System.out.println(String.format("Loaded %,d objects in %,d ms",object_count, runnable.getOptionalRunTime(0)));
		}
	}
	
	static public void testMultiThreadLoad() throws Exception
	{
		MyListener listener = new MyListener();
		
		SmallDocumentBulkLoader loader = new SmallDocumentBulkLoader(new FileReader(FILE_TEST_SMALL_OBJECTS),listener);
		
		OperationRunnable.executeWithMonitor(loader, 500, listener, Result.SUCCESS);
		
	}
	
	static public void main(String args[]) throws Exception
	{
		JimmutableTypeNameRegister.registerAllTypes();
		
		testMultiThreadLoad();
	}
	
}
