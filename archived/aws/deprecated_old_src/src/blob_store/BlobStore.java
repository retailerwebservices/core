package org.jimmutable.cloud.blob_store;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.cloud.environment.ApplicationEnvironment;
import org.jimmutable.cloud.environment.CloudResource;
import org.jimmutable.cloud.s3.BucketPuppet;
import org.jimmutable.cloud.s3.S3AbsolutePath;
import org.jimmutable.cloud.s3.S3BucketName;
import org.jimmutable.cloud.s3.S3DefaultClientCreator;
import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.core.utils.Validator;

import software.amazon.awssdk.services.s3.AmazonS3;
import software.amazon.awssdk.services.s3.model.ObjectMetadata;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.transfer.TransferManager;
import software.amazon.awssdk.services.s3.transfer.TransferManagerBuilder;
import software.amazon.awssdk.services.s3.transfer.Upload;

public class BlobStore extends CloudResource
{
	private AmazonS3 client;
	
	private BlobStoreName store_name; // required
	private boolean is_read_only; // required
	
	public BlobStore(ApplicationEnvironment env, BlobStoreName store_name, boolean is_read_only)
	{
		// Setup the parent environment
		super(env.getSimpleCloudName());
		
		// Set variables, validation, etc.
		{
			Validator.notNull(store_name);
			this.store_name = store_name;
			
			this.is_read_only = is_read_only;
		}
		
		// connect to s3
		client = S3DefaultClientCreator.createDefaultAmazonS3Client(env);
		
		// Make sure that the bucket has been created and is configured the proper way
		try
		{
			BucketPuppet.BucketConfiguration bucket_config = new BucketPuppet.BucketConfiguration(getSimpleS3BucketName(), 180, true);
			BucketPuppet.execute(client, bucket_config);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error(String.format("Unable to verify the configuration of the bucket %s", getSimpleS3BucketName()), e);
		}
	}
	
	public BlobStoreName getSimpleStoreName() { return store_name; }
	public boolean isReadOnly() { return is_read_only; }
	public AmazonS3 getSimpleAmazonS3() { return client; }
	
	public S3BucketName getSimpleS3BucketName()
	{
		return new S3BucketName(String.format("%s.%s.blob-store", getSimpleCloudName().getSimpleValue(), store_name.getSimpleValue()));
	}

	public String getSimpleS3BucketNameString()
	{
		return getSimpleS3BucketName().toString();
	}
	
	/**
	 * Find a unused blob path (matching the parameters of a give request)
	 * 
	 * @param request A valid upload request
	 * @param default_value The value to return in the even of an error
	 * 
	 * @return An unused object path
	 */
	private S3Path getUnusedPath(BlobStoreUploadRequest request, S3Path default_value)
	{ 
		Validator.notNull(request);
		try
		{
			while(true)
			{
				S3Path test = request.createRandomPath();
				
				if ( !client.doesObjectExist(getSimpleS3BucketNameString(), test.getSimpleValue()) )
					return test;
			}
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Upload a file to the blob store
	 * 
	 * @param request
	 *            The upload request
	 * 
	 * @param default_value
	 *            The value to return in the event of an error
	 * 
	 * @return The absolute path of the uploaded object
	 */
	public S3AbsolutePath upload(BlobStoreUploadRequest request, S3AbsolutePath default_value)
	{
		if ( isReadOnly() ) return default_value;
		
		Validator.notNull(request);
		if ( !request.getSimpleSourceFile().exists() ) return default_value;
		
		try
		{
			S3Path dest_path = getUnusedPath(request, null);
			if ( dest_path == null ) return default_value;

			TransferManager manager =  TransferManagerBuilder.standard().withS3Client(client).build();
			
			
			PutObjectRequest put_request = new PutObjectRequest(getSimpleS3BucketNameString(), dest_path.getSimpleValue(), request.getSimpleSourceFile());

			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(request.getSimpleSourceFile().length());
			meta.setContentType(request.getSimpleMimeType());
			
			put_request.setMetadata(meta);
			
			Upload transfer = manager.upload(put_request);
			
			transfer.waitForCompletion();
			
			return new S3AbsolutePath(getSimpleS3BucketName(),dest_path);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Error executing "+request, e);
			return default_value;
		}
	}
}
