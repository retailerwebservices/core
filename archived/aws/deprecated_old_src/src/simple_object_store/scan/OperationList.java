package org.jimmutable.cloud.simple_object_store.scan;

import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.utils.Validator;

import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Result;
import software.amazon.awssdk.services.s3.model.S3ObjectSummary;

public class OperationList extends OperationRunnable
{
	private OperationScan scan_operation;
	
	public OperationList(OperationScan scan_operation)
	{
		Validator.notNull(scan_operation);
	
		this.scan_operation = scan_operation;
	}
	
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) return Result.STOPPED;

		ScanRequest request = scan_operation.getSimpleRequest();

		ListObjectsV2Request req = new ListObjectsV2Request();
		req = req.withBucketName(request.getSimpleObjectStore().getSimpleS3BucketNameString());
		req = req.withMaxKeys(1000);

		// If the scan is not of the root path (i.e. a scan of the whole bucket) then set a prefix...
		if ( !request.getSimpleRootPath().equals(S3Path.PATH_BUCKET_ROOT) )
			req = req.withPrefix(request.getSimpleRootPath().getSimpleValue());

		int object_count = 0;


		while(true)
		{
			if ( shouldStop() ) return Result.STOPPED;

			ListObjectsV2Result result = request.getSimpleObjectStore().getSimpleAmazonS3().listObjectsV2(req);

			for ( S3ObjectSummary summary : result.getObjectSummaries() ) 
			{
				if ( shouldStop() ) return Result.STOPPED;

				OperationProcessObject task = new OperationProcessObject(scan_operation, summary);
				
				scan_operation.getSimpleChildOperations().submitOperation(task);
				object_count++;
			}
			
			if ( !result.isTruncated() ) return Result.SUCCESS;

			req.setContinuationToken(result.getNextContinuationToken());
		}
	}

}
