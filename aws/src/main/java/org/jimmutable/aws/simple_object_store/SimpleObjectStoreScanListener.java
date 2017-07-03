package org.jimmutable.aws.simple_object_store;

import org.jimmutable.aws.s3.S3Path;

public interface SimpleObjectStoreScanListener 
{
	public void onS3ObjectScanned(S3Path path);
}
