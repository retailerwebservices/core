package org.jimmutable.core.small_document;

import java.io.Reader;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.reader.Parser;
import org.jimmutable.core.threading.OperationPool;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.utils.Validator;

public class SmallDocumentBulkLoader extends OperationRunnable
{
	private SmallDocumentReader reader;
	private Listener listener;
	private OperationPool pool;
	
	public SmallDocumentBulkLoader(Reader src, Listener listener)
	{
		Validator.notNull(src,listener);
		
		this.reader = new SmallDocumentReader(src);
		this.listener = listener;
	}
	

	protected Result performOperation() throws Exception 
	{
		pool = new OperationPool(new OperationReadDocuments(),64);  // TODO: Implement some kind of blocking queue on the input
		
		return OperationRunnable.execute(pool, Result.SUCCESS);
	}

	static public interface Listener
	{
		public void onObjectLoaded(StandardObject object);
	}
	
	private class OperationReadDocuments extends OperationRunnable
	{
		public Result performOperation() throws Exception 
		{
			if ( shouldStop() ) return Result.STOPPED;
			
			try
			{
				while(reader.readNextDocument() != SmallDocumentSource.State.NO_MORE_DOCUMENTS )
				{
					pool.submitOperation(new OperationReadObject(reader.getCurrentDocument(null)));
					if ( shouldStop() ) return Result.STOPPED;
				}
				
				return Result.SUCCESS;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return Result.ERROR;
			}
		}
	}
	
	private class OperationReadObject extends OperationRunnable
	{
		private String doc;
		
		public OperationReadObject(String doc)
		{
			this.doc = doc;
		}
		
		
		public Result performOperation() throws Exception 
		{
			if ( shouldStop() ) return Result.STOPPED;
			if ( doc == null ) return Result.SUCCESS;
			
			try
			{
				listener.onObjectLoaded(StandardObject.deserialize(doc));
				
				
				
				
				return Result.SUCCESS;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return Result.SUCCESS;
			}
		}
	}
}
