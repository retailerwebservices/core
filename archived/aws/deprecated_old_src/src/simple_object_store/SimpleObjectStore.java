package org.jimmutable.cloud.simple_object_store;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.cloud.environment.ApplicationEnvironment;
import org.jimmutable.cloud.environment.CloudResource;
import org.jimmutable.cloud.s3.BucketPuppet;
import org.jimmutable.cloud.s3.S3BucketName;
import org.jimmutable.cloud.s3.S3DefaultClientCreator;
import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.cloud.simple_object_store.scan.OperationScan;
import org.jimmutable.cloud.simple_object_store.scan.ScanListener;
import org.jimmutable.cloud.simple_object_store.scan.ScanRequest;
import org.jimmutable.cloud.utils.CompressionUtils;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.threading.OperationRunnable.Result;
import org.jimmutable.core.utils.Validator;

import software.amazon.awssdk.services.s3.AmazonS3;
import software.amazon.awssdk.services.s3.model.ObjectMetadata;
import software.amazon.awssdk.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

public class SimpleObjectStore extends CloudResource
{
	private AmazonS3 client;
	
	private SimpleStoreName store_name; // required
	private boolean is_read_only; // required
	
	public SimpleObjectStore(ApplicationEnvironment env, SimpleStoreName store_name, boolean is_read_only)
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
			BucketPuppet.BucketConfiguration bucket_config = new BucketPuppet.BucketConfiguration(getSimpleS3BucketName(), 180, false);
			BucketPuppet.execute(client, bucket_config);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error(String.format("Unable to verify the configuration of the bucket %s", getSimpleS3BucketName()), e);
		}
	}
	
	public SimpleStoreName getSimpleStoreName() { return store_name; }
	public boolean isReadOnly() { return is_read_only; }
	public AmazonS3 getSimpleAmazonS3() { return client; }
	
	public S3BucketName getSimpleS3BucketName()
	{
		return new S3BucketName(String.format("%s.%s.simple-object-store", getSimpleCloudName().getSimpleValue(), store_name.getSimpleValue()));
	}

	public String getSimpleS3BucketNameString()
	{
		return getSimpleS3BucketName().toString();
	}
	
	/**
	 * Upload an object into the datastore
	 * 
	 * NOTE: Objects are assumed to be relatively small (less than 10MB
	 * Serialized). As a result, the implementation of this method does
	 * everything in RAM (as opposed to on disk). If you are working with
	 * extremely large objects, this implementation may need to be revisited.
	 * 
	 * @param obj The object to upsert
	 * @return True if the upsert was a success, false otherwise
	 */
	public boolean upsert(SimpleObjectStorable obj)
	{
		if ( obj == null ) 
		{
			LogManager.getRootLogger().error("Failed attempt to write null object to SimpleObjectStore");
			return false;
		}
		
		S3Path dest_path = obj.getStorableS3Path();
		
		if ( !dest_path.getOptionalExtension("").equals("xml") )
		{
			LogManager.getRootLogger().error(String.format("Unable to upsert SimpleObjectStorable because destination path %s does not end in .xml", dest_path.getSimpleValue()));
			return false;
		}
		
		
		if ( isReadOnly() )
		{
			LogManager.getRootLogger().debug("Attempt to write "+obj.getStorableS3Path()+" to a read only SimpleObjectStore");
			return false;
		}
		
		try
		{
			byte compressed_data[] = CompressionUtils.gzipString(obj.serialize(Format.XML_PRETTY_PRINT), null);
			
			if ( compressed_data == null )
			{
				LogManager.getRootLogger().error("Unable to compress the XML serialization of "+obj.getStorableS3Path()+": This is very unusual");
				return false;
			}
			
			ByteArrayInputStream input = new ByteArrayInputStream(compressed_data);
			
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(compressed_data.length);
			meta.setContentEncoding("gzip");
			
			client.putObject(getSimpleS3BucketNameString(), dest_path.getSimpleValue(), input, meta);
			
			return true;
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Failure to write object "+obj.getStorableS3Path(),e);
			return false;
		}
	}
	
	/**
	 * Get an object from the datastore
	 * 
	 * @param path The path to read from
	 * @param default_value The value to return if the object can not be read (for any reason)
	 * 
	 * @return The standard object read from the specified path
	 */
	public StandardObject get(S3Path path, StandardObject default_value)
	{
		if ( path == null ) return default_value;
		
		try
		{
			S3Object obj = client.getObject(getSimpleS3BucketNameString(), path.getSimpleValue());
			
			byte compressed_data[] = IOUtils.toByteArray(obj.getObjectContent());
			
			String uncompressed_string = CompressionUtils.gunzipToString(compressed_data, null);
			
			if ( uncompressed_string == null )
			{
				LogManager.getRootLogger().error("Unable to un-compres the XML serialization of "+path+": This is very unusual");
				return default_value;
			}
			
			return StandardObject.deserialize(uncompressed_string);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Failure to get object "+path,e);
			return default_value;
		}
	}
	
	/**
	 * Check to see if a given object exists in the datastore
	 * 
	 * @param path
	 *            The path of the object
	 * @param default_value
	 *            The value to return if any sort of error occours
	 * @return true if the object exists, false if it does not, and
	 *         default_value if an error occours while checking
	 */
	public boolean objectExists(S3Path path, boolean default_value)
	{
		if ( path == null ) return default_value;
		
		try
		{
			return client.doesObjectExist(getSimpleS3BucketNameString(), path.toString());
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Failure to list object "+path,e);
			return default_value;
		}
	}
	
	/**
	 * Delete an object from the simple object store
	 * 
	 * @param path The path of the object to delete
	 * 
	 * @return true if the object was deleted, false otherwise
	 * 
	 */
	public boolean delete(S3Path path)
	{
		if ( path == null ) return false;
		if ( isReadOnly() ) return false;
		
		try
		{
			client.deleteObject(getSimpleS3BucketNameString(), path.toString());
			return true;
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Failure to delete object "+path,e);
			return false;
		}
	}
	
	
	/**
	 * Scan all objects in a given path
	 * 
	 * This function blocks until all objects are scanned
	 * 
	 * @param path
	 *            The root path of the scan (to scan the whole bucket use
	 *            S3Path.PATH_BUCKET_ROOT, null is not allowed)
	 * @param listener
	 *            The scan listener
	 * @param processing_threads
	 *            The number of object processign threads to use
	 * @return The result of the scan operation
	 */
	public Result scan(S3Path path, ScanListener listener, int processing_threads)
	{
		ScanRequest request = new ScanRequest(this, path, listener, processing_threads);
		
		return OperationRunnable.execute(new OperationScan(request), Result.ERROR);
	}
	
	/**
	 * Scan all objects in a given path
	 * 
	 * This function blocks until all objects are scanned
	 * 
	 * @param path
	 *            The root path of the scan (to scan the whole bucket use
	 *            S3Path.PATH_BUCKET_ROOT, null is not allowed)
	 * @param listener
	 *            The scan listener
	 * @param processing_threads
	 *            The number of object processign threads to use
	 * 
	 */
	public void startScanAsycn(S3Path path, ScanListener listener, int processing_threads)
	{
		ScanRequest request = new ScanRequest(this, path, listener, processing_threads);
		
		new Thread(new OperationScan(request));
	}
}
