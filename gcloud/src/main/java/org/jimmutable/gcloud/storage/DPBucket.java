package org.jimmutable.gcloud.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.BucketListOption;

public class DPBucket
{
	private static final String DEFAULT_DEV_PREFIX = "dev.";
	private static final String DEFAULT_PROD_PREFIX = "prod.";
	private static final Storage STORAGE = StorageOptions.getDefaultInstance().getService();

	public static Page<Bucket> listDevBucketsWithPrefix(String prefix)
	{
	    String full_prefix = DEFAULT_DEV_PREFIX + prefix;
		return STORAGE.list(BucketListOption.pageSize(100),
	            BucketListOption.prefix(full_prefix));
	}

	public static Page<Bucket> listDevBuckets()
	{
		return STORAGE.list(BucketListOption.pageSize(100),
	            BucketListOption.prefix(DEFAULT_DEV_PREFIX));
	}
	
	public static Bucket createDevBucket(String bucket_name) throws Exception
	{
		Bucket bucket = DPBucket.createBucket(DEFAULT_DEV_PREFIX, bucket_name);
		System.out.printf("Bucket %s created.%n", bucket.getName());
		return bucket;
	}

	public static Bucket createProdBucket(String bucket_name) throws Exception
	{
		Bucket bucket = DPBucket.createBucket(DEFAULT_PROD_PREFIX, bucket_name);
		System.out.printf("Bucket %s created.%n", bucket.getName());
		return bucket;
	}
	
	private static Bucket createBucket(String prefix, String bucket_name) throws Exception
	{
		return STORAGE.create(BucketInfo.newBuilder(prefix + bucket_name)
				// See here for possible values: http://g.co/cloud/storage/docs/storage-classes
				.setStorageClass(StorageClass.REGIONAL)
				// Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
				.setLocation("us-east1")
				.setVersioningEnabled(true)
				.build());
	}
}
