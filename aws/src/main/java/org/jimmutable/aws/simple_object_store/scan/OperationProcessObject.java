package org.jimmutable.aws.simple_object_store.scan;

import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.threading.OperationRunnable.Result;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

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
		
		scan_operation.getSimpleRequest().getSimpleScanListener().processObject(scan_operation, object_summary);
		
		if ( shouldStop() ) 
			return Result.STOPPED;

		return Result.SUCCESS;
	}
}