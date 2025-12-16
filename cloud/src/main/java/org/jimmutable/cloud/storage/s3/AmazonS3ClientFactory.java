package org.jimmutable.cloud.storage.s3;

import software.amazon.awssdk.services.s3.S3AsyncClient;


public interface AmazonS3ClientFactory
{
    public S3AsyncClient create();
}
