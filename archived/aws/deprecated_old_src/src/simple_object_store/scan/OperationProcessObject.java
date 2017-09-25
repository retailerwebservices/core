package org.jimmutable.cloud.simple_object_store.scan;

import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class OperationProcessObject extends OperationRunnable
{
	private S3ObjectSummary object_summary;
	private OperationScan scan_operation;
	
	public OperationProcessObject(OperationScan scan_operation, S3ObjectSummary object_summary)
	{
		Validator.notNull(scan_operation, object_summary);
		 
		this.scan_operation = scan_operation;
		this.object_summary = object_summary;
	}
	
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) 
			return Result.STOPPED;
		
		S3Path path = new S3Path(object_summary.getKey());
		
		boolean include_object = scan_operation.getSimpleRequest().getSimpleScanListener().shouldLoadObject(scan_operation, path, object_summary);
		
		if ( include_object )
		{
			StandardObject obj = scan_operation.getSimpleRequest().getSimpleObjectStore().get(path, null);
			
			if ( obj != null )  
				scan_operation.getSimpleRequest().getSimpleScanListener().onLoadObject(scan_operation, path, object_summary, obj);
		}
		
		if ( shouldStop() ) 
			return Result.STOPPED;

		return Result.SUCCESS;
	}
}