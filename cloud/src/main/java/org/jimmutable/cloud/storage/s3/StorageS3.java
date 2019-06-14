package org.jimmutable.cloud.storage.s3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.cache.CacheKey;
import org.jimmutable.cloud.storage.GenericStorageKey;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StandardImmutableObjectCache;
import org.jimmutable.cloud.storage.Storage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.cloud.storage.StorageKeyName;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.IOUtils;
//import org.jimmutable.core.utils.IOUtils;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

public class StorageS3 extends Storage
{
	static private final Logger LOGGER = LogManager.getLogger(StorageS3.class);

	static private final String BUCKET_NAME_PREFIX = "jimmutable-app-";

	static private final long TRANSFER_MANAGER_POLLING_INTERVAL_MS = 500L;

	final private String bucket_name;
	final private AmazonS3Client client;
	final private TransferManager transfer_manager;

	// Since this will be init'd in CEE.startup, we can't rely on the singleton for
	// access to the ApplicationId
	public StorageS3( final AmazonS3ClientFactory client_factory, final ApplicationId application_id, final boolean is_read_only )
	{
		super(is_read_only);

		bucket_name = BUCKET_NAME_PREFIX + application_id;

		Validator.notNull(client_factory);
		client = client_factory.create();

		transfer_manager = TransferManagerBuilder.standard().withS3Client(client).build();
	}

	public StorageS3( final AmazonS3ClientFactory client_factory, final ApplicationId application_id, StandardImmutableObjectCache cache, final boolean is_read_only )
	{
		super(is_read_only, cache);

		bucket_name = BUCKET_NAME_PREFIX + application_id;

		Validator.notNull(client_factory);
		client = client_factory.create();

		transfer_manager = TransferManagerBuilder.standard().withS3Client(client).build();
	}

	public void upsertBucketIfNeeded()
	{
		if ( !client.doesBucketExist(bucket_name) )
		{
			LOGGER.info("creating bucket: " + bucket_name);

			CreateBucketRequest request = new CreateBucketRequest(bucket_name, RegionSpecificAmazonS3ClientFactory.DEFAULT_REGION);
			client.createBucket(request);
		}
		else
		{
			LOGGER.info("using storage bucket: " + bucket_name);
		}
	}

	public String getSimpleBucketName()
	{
		return bucket_name;
	}

	@Override
	public boolean exists( final StorageKey key, final boolean default_value )
	{
		try
		{
			return client.doesObjectExist(bucket_name, key.toString());
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
			return default_value;
		}
	}

	// TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must
	// be able to detect dynamically on read.
	@Override
	public boolean upsert( StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible )
	{
		Validator.max(bytes.length, MAX_TRANSFER_BYTES_IN_BYTES);

		if ( isReadOnly() )
			return false;

		try
		{
			InputStream bin = new ByteArrayInputStream(bytes);

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(bytes.length);

			client.putObject(bucket_name, key.toString(), bin, metadata);
			if ( isCacheEnabled() )
			{
				removeFromCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()));
			}
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
			return false;
		}
	}

	// TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must
	// be able to detect dynamically on read.
	@Override
	public boolean upsertStreaming( final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible )
	{
		Validator.notNull(key, source);

		if ( isReadOnly() )
			return false;

		final String log_prefix = "[upsert(" + key + ")] ";
		File temp = null;
		try
		{
			temp = File.createTempFile("storage_s3_", null);

			System.out.println("Temp file On Default Location: " + temp.getAbsolutePath());

			LOGGER.debug(log_prefix + "Writing source to temp file");
			try ( OutputStream fout = new BufferedOutputStream(new FileOutputStream(temp)) )
			{
				IOUtils.transferAllBytes(source, fout);
			}

			final String s3_key = key.toString();

			Upload upload = null;

			try
			{
				upload = transfer_manager.upload(bucket_name, s3_key, temp);

				LOGGER.info(log_prefix + "Upload: " + upload.getDescription());

				while ( !upload.isDone() )
				{
					LOGGER.debug(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred());
					try
					{
						Thread.sleep(TRANSFER_MANAGER_POLLING_INTERVAL_MS);
					}
					catch ( Exception e )
					{
					} // give progress updates every .5 sec
				}

				LOGGER.debug(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred()); // give the 100
																										// percent
																										// before
																										// exiting

				boolean result = TransferState.Completed == upload.getState();
				if ( result && isCacheEnabled() )
				{
					removeFromCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()));
				}

				deleteTempFile(temp);

				return result;
			}
			catch ( Exception e )
			{
				LOGGER.catching(e);
				upload.abort();
			}
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
		}

		deleteTempFile(temp);

		return false;
	}

	/**
	 * Similar to getCurrentVerionStreaming, except there is a maximum byte range.
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	@Override
	public byte[] getCurrentVersion( final StorageKey key, byte[] default_value )
	{
		byte[] object = getComplexCurrentVersionFromCache(key, null);
		if ( object != null )
		{
			return object;
		}

		Validator.notNull(key, "StorageKey");
		S3Object s3_obj = null;
		try
		{
			s3_obj = client.getObject(new GetObjectRequest(bucket_name, key.toString()).withRange(0, MAX_TRANSFER_BYTES_IN_BYTES));
			byte[] obj = org.apache.commons.io.IOUtils.toByteArray(s3_obj.getObjectContent());

			if ( isCacheEnabled() )
			{
				try
				{
					if ( key.getSimpleExtension().equals(StorageKeyExtension.XML) || key.getSimpleExtension().equals(StorageKeyExtension.JSON) )
					{
						StandardObject standard_obj = StandardObject.deserialize(new String(obj));
						if ( standard_obj instanceof StandardImmutableObject )
						{
							addToStandardImmutableObjectCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()), (StandardImmutableObject) standard_obj);
						}
					}
				}
				catch ( Exception e )
				{
					LogManager.getRootLogger().error("Failure to make into a StandardImmutableObject " + key.toString() + ". This object is not in the cache.", e);
				}
			}

			return obj;
		}
		catch ( Exception e )
		{
			LOGGER.error(String.format("Failed to retrieve %s from S3!", key.toString()), e);
		}
		finally
		{
			if ( s3_obj != null )
			{
				try
				{
					s3_obj.close();
				}
				catch ( IOException e )
				{
					LOGGER.error(String.format("Failed to close S3Object when reading %s!", key.toString()), e);
				}
			}
		}
		return default_value;
	}

	/**
	 * Note: calling getObject always returns a stream anyways
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	@Override
	public boolean getCurrentVersionStreaming( final StorageKey key, final OutputStream sink )
	{

		long start = System.currentTimeMillis();

		Validator.notNull(key, "StorageKey");

		byte[] object = getComplexCurrentVersionFromCache(key, null);
		if ( object != null )
		{
			try
			{
				IOUtils.transferAllBytes(new ByteArrayInputStream(object), sink);
				return true;
			}
			catch ( IOException e )
			{
				// we do not return false here because we want to try to get it from storage.
			}
		}

		S3Object s3_obj = null;
		try
		{
			s3_obj = client.getObject(new GetObjectRequest(bucket_name, key.toString()));
			org.apache.commons.io.IOUtils.copy(s3_obj.getObjectContent(), sink);
			LOGGER.debug(String.format("Took %d millis", System.currentTimeMillis() - start));
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.error(String.format("Failed to retrieve %s from S3!", key.toString()), e);
		}
		finally
		{
			if ( s3_obj != null )
			{
				try
				{
					s3_obj.close();
				}
				catch ( IOException e )
				{
					LOGGER.error(String.format("Failed to close S3Object when reading %s!", key.toString()), e);
				}
			}
		}
		return false;
	}

	/**
	 * old getCurrentVersionStreaming method. This method takes longer and may not
	 * be needed for now. We may need to
	 * 
	 * @param key
	 * @param sink
	 * @return
	 */
	@Override
	public boolean getThreadedCurrentVersionStreaming( final StorageKey key, final OutputStream sink )
	{

		long start = System.currentTimeMillis();
		Validator.notNull(key, sink);

		final String log_prefix = "[getCurrentVersion(" + key + ")] ";

		File temp = null;
		try
		{
			final String s3_key = key.toString();
			temp = File.createTempFile("storage_s3_", null);
			Download download = null;

			try
			{
				download = transfer_manager.download(bucket_name, s3_key, temp);

				LOGGER.debug(log_prefix + "Download: " + download.getDescription());

				while ( !download.isDone() )
				{
					LOGGER.debug(log_prefix + "Progress: " + download.getProgress().getPercentTransferred());
					try
					{
						Thread.sleep(TRANSFER_MANAGER_POLLING_INTERVAL_MS);
					}
					catch ( Exception e )
					{
					} // give progress updates every .5 sec
				}

				/*
				 * give the 100 percent before exiting
				 */
				LOGGER.info(log_prefix + "Progress: " + download.getProgress().getPercentTransferred());
			}
			catch ( Exception e )
			{
				deleteTempFile(temp);
				LOGGER.catching(e);
				download.abort();
				return false;
			}

			LOGGER.debug(log_prefix + "Writing temp file to sink");
			try ( InputStream fin = new BufferedInputStream(new FileInputStream(temp)) )
			{
				IOUtils.transferAllBytes(fin, sink);
			}
			boolean completed = TransferState.Completed == download.getState();

			LOGGER.debug(String.format("Took %d millis", System.currentTimeMillis() - start));
			deleteTempFile(temp);
			return completed;
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
		}

		deleteTempFile(temp);

		return false;
	}

	private void deleteTempFile( File temp )
	{
		if(temp == null || !temp.exists())
		{
			return;
		}
		
		try
		{
			if ( !temp.delete() )
			{
				LOGGER.error("Unable to delete temp file. Ensure this isn't happening consistently");
			}
		}
		catch ( Exception e )
		{
			LOGGER.error("Exception thrown deleting temp file. Ensure this isn't happening consistently", e);
		}
	}

	@Override
	public boolean delete( final StorageKey key )
	{
		if ( isReadOnly() )
			return false;

		try
		{
			client.deleteObject(new DeleteObjectRequest(bucket_name, key.toString()));
			if ( isCacheEnabled() )
			{
				removeFromCache(key);
			}
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
			return false;
		}
	}

	@Override
	public StorageMetadata getObjectMetadata( final StorageKey key, final StorageMetadata default_value )
	{
		try
		{
			ObjectMetadata s3_metadata = client.getObjectMetadata(bucket_name, key.toString());

			long last_modified = s3_metadata.getLastModified().getTime();
			long size = s3_metadata.getContentLength();
			String etag = s3_metadata.getETag();

			return new StorageMetadata(last_modified, size, etag);
		}
		catch ( AmazonS3Exception e )
		{
			// We get a 404 Not Found for any object that doesn't exist.
			// A separate doesObjectExist call would be an entire extra
			// network round trip... so just special case it.
			if ( 404 == e.getStatusCode() )
			{
				return default_value;
			}

			throw e;
		}
		catch ( Exception e )
		{
			LOGGER.catching(e);
			return default_value;
		}
	}

	/**
	 * This class does the main listing operation for scan*. It runs in it's own
	 * thread and throws each StorageKey it finds into another OperationRunnable
	 * running in a common pool.
	 *
	 * @author Jeff Dezso
	 */
	private class Scanner extends Storage.Scanner
	{
		public Scanner( final Kind kind, final StorageKeyName prefix, final boolean only_object_ids )
		{
			super(kind, prefix, only_object_ids);
		}

		@Override
		protected Result performOperation() throws Exception
		{
			String root = getSimpleKind().getSimpleValue();

			if ( hasPrefix() )
			{
				root += "/" + getOptionalPrefix(null);
			}

			ListObjectsRequest request = new ListObjectsRequest(bucket_name, root + "/", null, null, -1);

			while ( true )
			{
				ObjectListing object_listing = client.listObjects(request);
				if ( null == object_listing )
					return Result.ERROR;

				for ( S3ObjectSummary summary : object_listing.getObjectSummaries() )
				{
					final String key = summary.getKey(); // The full S3 key, also the StorageKey
					final String full_key_name = key.substring(root.length() + 1); // The "filename" without the
																					// backslash
					// This would be the folder of the Kind we are looking at
					if ( full_key_name.isEmpty() )
						continue;

					String[] key_name_and_ext = full_key_name.split("\\.");
					String key_name = key_name_and_ext[0];

					StorageKeyName name = null;
					try
					{
						name = new StorageKeyName(key_name);
					}
					catch ( Exception e )
					{
						LOGGER.error(String.format("[StorageS3.performOperation] could not create StorageKeyName for key:%s root:%s bucket:%s request:%s", key, root, bucket_name, request), e);
						continue;
					}

					if ( name.isObjectId() )
					{
						emit(new ObjectIdStorageKey(key));
					}
					else
					{
						if ( !onlyObjectIds() )
						{
							emit(new GenericStorageKey(key));
						}
					}
				}

				if ( !object_listing.isTruncated() )
					break;
				request.setMarker(object_listing.getNextMarker());
			}

			return shouldStop() ? Result.STOPPED : Result.SUCCESS;
		}
	}

	@Override
	protected Storage.Scanner createScanner( Kind kind, StorageKeyName prefix, boolean only_object_ids )
	{
		return new Scanner(kind, prefix, only_object_ids);
	}

}
