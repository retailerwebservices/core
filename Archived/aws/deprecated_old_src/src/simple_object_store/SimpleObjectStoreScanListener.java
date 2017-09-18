package org.jimmutable.cloud.simple_object_store;

import org.jimmutable.cloud.s3.S3Path;

public interface SimpleObjectStoreScanListener 
{
	public void onS3ObjectScanned(S3Path path);
}
