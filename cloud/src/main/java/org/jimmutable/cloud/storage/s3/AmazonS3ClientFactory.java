package org.jimmutable.cloud.storage.s3;

import com.amazonaws.services.s3.AmazonS3Client;


public interface AmazonS3ClientFactory
{
    public AmazonS3Client create();
}
