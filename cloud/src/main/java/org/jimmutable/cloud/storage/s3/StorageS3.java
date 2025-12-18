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
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.storage.GenericStorageKey;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StandardImmutableObjectCache;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.Storage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.cloud.storage.StorageKeyName;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.IOUtils;
import org.jimmutable.core.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;
import software.amazon.awssdk.transfer.s3.progress.TransferProgressSnapshot;

public class StorageS3 extends Storage
{
	static private final Logger LOGGER = LoggerFactory.getLogger(StorageS3.class);

	static private final String BUCKET_NAME_PREFIX = "jimmutable-app-";

	static private final long TRANSFER_MANAGER_POLLING_INTERVAL_MS = 500L;

	final private String bucket_name;
	final private S3AsyncClient client;
	final private S3TransferManager transfer_manager;

	// Since this will be init'd in CEE.startup, we can't rely on the singleton for
	// access to the ApplicationId
	public StorageS3( final AmazonS3ClientFactory client_factory, final ApplicationId application_id, final boolean is_read_only )
	{
		super(is_read_only);

		bucket_name = BUCKET_NAME_PREFIX
				+ application_id;

		Validator.notNull(client_factory);
		client = client_factory.create();

		transfer_manager = S3TransferManager.builder().s3Client(client).build();
	}

	public StorageS3( final AmazonS3ClientFactory client_factory, final ApplicationId application_id, StandardImmutableObjectCache cache, final boolean is_read_only )
	{
		super(is_read_only, cache);

		bucket_name = BUCKET_NAME_PREFIX
				+ application_id;

		Validator.notNull(client_factory);
		client = client_factory.create();

		transfer_manager = S3TransferManager.builder().s3Client(client).build();
	}

	public S3AsyncClient getSimpleClient()
	{
		return client;
	}

	public void upsertBucketIfNeeded()
	{
		upsertBucketIfNeeded(bucket_name);
	}

	public void upsertBucketIfNeeded( String bucket_name )
	{
		try {
			HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
					.bucket(bucket_name)
					.build();

			client.headBucket(headBucketRequest).join();

			LOGGER.info("using storage bucket: "
					+ bucket_name);
		} catch (Exception e) {
			LOGGER.info("creating bucket: "
					+ bucket_name);

			CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucket_name)
					.createBucketConfiguration(CreateBucketConfiguration.builder().locationConstraint(RegionSpecificAmazonS3ClientFactory.DEFAULT_REGION.id()).build())
					.build();
			client.createBucket(request).join();
		}
	}

	public String getSimpleBucketName()
	{
		return bucket_name;
	}

	@Override
	public boolean exists( final StorageKey key, final boolean default_value )
	{
		return exists(bucket_name, key, default_value);
	}

	public boolean exists( String bucket_name, final StorageKey key, final boolean default_value )
	{
		boolean doesExist;
		try
		{
			HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.build();

			client.headObject(headObjectRequest).join();
			doesExist = true;
		}
		catch ( Exception e )
		{
			doesExist = false;
		}
		return doesExist;
	}

	public boolean upsert( Storable obj, Format format )
	{
		Validator.notNull(obj);

		if ( isReadOnly() )
			return false;

		return upsert(obj.createStorageKey(), ObjectWriter.serialize(format, obj).getBytes(), true);
	}

	public boolean upsert( String bucket_name, Storable obj, Format format )
	{
		Validator.notNull(obj);

		if ( isReadOnly() )
			return false;

		return upsert(bucket_name, obj.createStorageKey(), ObjectWriter.serialize(format, obj).getBytes(), true);
	}

	public boolean upsert( String bucket_name, StorageKey key, Storable obj, Format format )
	{
		Validator.notNull(obj);

		if ( isReadOnly() )
			return false;

		return upsert(bucket_name, key, ObjectWriter.serialize(format, obj).getBytes(), true);
	}

	// TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must
	// be able to detect dynamically on read.
	@Override
	public boolean upsert( StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible )
	{
		return upsert(bucket_name, key, bytes, hint_content_likely_to_be_compressible);
	}

	public boolean upsert( String bucket_name, StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible )

	{
		Validator.max(bytes.length, MAX_TRANSFER_BYTES_IN_BYTES);

		if ( isReadOnly() )
			return false;

		try
		{
			InputStream bin = new ByteArrayInputStream(bytes);

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.contentLength((long) bytes.length)
					.build();
			client.putObject(putObjectRequest, (AsyncRequestBody) RequestBody.fromInputStream(bin, bytes.length)).join();
			if ( isCacheEnabled() )
			{
				removeFromCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()));
			}
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.error("Exception on upsert", e);
			return false;
		}
	}

	// TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must
	// be able to detect dynamically on read.
	@Override
	public boolean upsertStreaming( final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible )
	{
		return upsertStreaming(bucket_name, key, source, hint_content_likely_to_be_compressible);
	}

	public boolean upsertStreaming( String bucket_name, final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible )
	{
		Validator.notNull(key, source);

		if ( isReadOnly() )
			return false;

		final String log_prefix = "[upsert("
				+ key
				+ ")] ";
		File temp = null;
		try
		{
			temp = File.createTempFile("storage_s3_", null);

			LOGGER.debug(log_prefix
					+ "Writing source to temp file");
			try ( OutputStream fout = new BufferedOutputStream(new FileOutputStream(temp)) )
			{
				IOUtils.transferAllBytes(source, fout);
			}

			final String s3_key = key.toString();

			UploadFileRequest uploadRequest = UploadFileRequest.builder()
					.putObjectRequest(r -> r.bucket(bucket_name).key(key.toString()))
					.source(temp)
					.build();

			FileUpload upload = transfer_manager.uploadFile(uploadRequest);
			CompletableFuture<CompletedFileUpload> uploadFuture = upload.completionFuture();

			String uploadDescription = String.format(
					"Uploading %s to s3://%s/%s",
					uploadRequest.source(),
					uploadRequest.putObjectRequest().bucket(),
					uploadRequest.putObjectRequest().key());

			try
			{
				LOGGER.info(log_prefix
						+ "Upload: "
						+ uploadDescription);

				while ( !uploadFuture.isDone() )
				{
					TransferProgressSnapshot snapshot = upload.progress().snapshot();
					long bytesTransferred = snapshot.transferredBytes();
					long bytesTotal = 0;
					if (snapshot.totalBytes().isPresent()) {
						bytesTotal = snapshot.totalBytes().getAsLong();
					}
					double percentTransferred = (bytesTotal > 0) ? (bytesTransferred * 100.0 / bytesTotal) : 0.0;
					LOGGER.debug(log_prefix
							+ "Progress: "
							+ percentTransferred);
					try
					{
						Thread.sleep(TRANSFER_MANAGER_POLLING_INTERVAL_MS);
					}
					catch ( Exception e )
					{
					} // give progress updates every .5 sec
				}

				TransferProgressSnapshot finalSnapshot = upload.progress().snapshot();
				long bytesTransferred = finalSnapshot.transferredBytes();
				long bytesTotal = 0;
				if (finalSnapshot.totalBytes().isPresent()) {
					bytesTotal = finalSnapshot.totalBytes().getAsLong();
				}
				double finalPercentTransferred = (bytesTotal > 0) ? (bytesTransferred * 100.0 / bytesTotal) : 0.0;
				LOGGER.debug(log_prefix
						+ "Progress: "
						+ finalPercentTransferred); // give the 100
				// percent
				// before
				// exiting

				boolean result = uploadFuture.isDone();
				if ( result && isCacheEnabled() )
				{
					removeFromCache(key.getSimpleKind(), new ObjectId(key.getSimpleName().getSimpleValue()));
				}

				deleteTempFile(temp);

				return result;
			}
			catch ( Exception e )
			{
				LOGGER.error("Exception on streaming upsert", e);
				uploadFuture.cancel(true);
			}
		}
		catch ( Exception e )
		{
			LOGGER.error("Exception on temp file streaming", e);
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
		return getCurrentVersion(bucket_name, key, default_value);
	}

	public byte[] getCurrentVersion( String bucket_name, final StorageKey key, byte[] default_value )
	{
		byte[] object = getComplexCurrentVersionFromCache(key, null);
		if ( object != null )
		{
			return object;
		}

		Validator.notNull(key, "StorageKey");
		CompletableFuture<ResponseBytes<GetObjectResponse>> s3_obj = null;
		try
		{
			GetObjectRequest request = GetObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.range("bytes=0-" + (MAX_TRANSFER_BYTES_IN_BYTES - 1)) // inclusive range
					.build();

			s3_obj = client.getObject(request, AsyncResponseTransformer.toBytes());
			byte[] obj = s3_obj.join().asByteArray();

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
					LOGGER.trace("Exception retrieving object", e);
				}
			}

			return obj;
		}
		catch ( Exception e )
		{
			LOGGER.error(String.format("Failed to retrieve %s from S3!", key.toString()), e);
		}
		return default_value;
	}

	/**
	 * Note: calling getObject always returns a stream anyway
	 *
	 * @param key
	 * @return
	 */
	@Override
	public boolean getCurrentVersionStreaming( final StorageKey key, final OutputStream sink )
	{
		return getCurrentVersionStreaming(bucket_name, key, sink);
	}

	public boolean getCurrentVersionStreaming( String bucket_name, final StorageKey key, final OutputStream sink )
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

		CompletableFuture<ResponseBytes<GetObjectResponse>> s3_obj = null;
		try
		{
			GetObjectRequest request = GetObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.build();
			s3_obj = client.getObject(request, AsyncResponseTransformer.toBytes());
			ResponseBytes<GetObjectResponse> responseBytes = s3_obj.join();
			org.apache.commons.io.IOUtils.copy(responseBytes.asInputStream(), sink);
			LOGGER.debug(String.format("Took %d millis", System.currentTimeMillis()
					- start));
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.error(String.format("Failed to retrieve %s from S3!", key.toString()));
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
		return getThreadedCurrentVersionStreaming(bucket_name, key, sink);
	}

	public boolean getThreadedCurrentVersionStreaming( String bucket_name, final StorageKey key, final OutputStream sink )
	{

		long start = System.currentTimeMillis();
		Validator.notNull(key, sink);

		final String log_prefix = "[getCurrentVersion("
				+ key
				+ ")] ";

		File temp = null;
		try
		{
			final String s3_key = key.toString();
			temp = File.createTempFile("storage_s3_", null);

			DownloadFileRequest downloadRequest = DownloadFileRequest.builder()
					.getObjectRequest(r -> r.bucket(bucket_name).key(key.toString()))
					.destination(temp)
					.build();
			FileDownload download = transfer_manager.downloadFile(downloadRequest);
			CompletableFuture<CompletedFileDownload> downloadFuture = download.completionFuture();

			try
			{
				String description = String.format("Downloading s3://%s/%s to %s", bucket_name, key.toString(), temp.getAbsolutePath());

				LOGGER.debug(log_prefix
						+ "Download: "
						+ description);

				while ( !downloadFuture.isDone() )
				{
					TransferProgressSnapshot snapshot = download.progress().snapshot();
					long bytesTransferred = snapshot.transferredBytes();
					long bytesTotal = 0;
					if (snapshot.totalBytes().isPresent()) {
						bytesTotal = snapshot.totalBytes().getAsLong();
					}
					double percentTransferred = (bytesTotal > 0) ? (bytesTransferred * 100.0 / bytesTotal) : 0.0;
					LOGGER.debug(log_prefix
							+ "Progress: "
							+ percentTransferred);
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
				TransferProgressSnapshot finalSnapshot = download.progress().snapshot();
				long bytesTransferred = finalSnapshot.transferredBytes();
				long bytesTotal = 0;
				if (finalSnapshot.totalBytes().isPresent()) {
					bytesTotal = finalSnapshot.totalBytes().getAsLong();
				}
				double finalPercent = (bytesTotal > 0) ? (bytesTransferred * 100.0 / bytesTotal) : 0.0;
				LOGGER.info(log_prefix
						+ "Progress: "
						+ finalPercent);
			}
			catch ( Exception e )
			{
				deleteTempFile(temp);
				LOGGER.info("Exception on streaming retrieval from S3", e);
				downloadFuture.cancel(true);
				return false;
			}

			LOGGER.debug(log_prefix
					+ "Writing temp file to sink");
			try ( InputStream fin = new BufferedInputStream(new FileInputStream(temp)) )
			{
				IOUtils.transferAllBytes(fin, sink);
			}
			boolean completed = downloadFuture.isDone();

			LOGGER.debug(String.format("Took %d millis", System.currentTimeMillis()
					- start));
			deleteTempFile(temp);
			return completed;
		}
		catch ( Exception e )
		{
			LOGGER.error("Exception on streaming retrieval from S3", e);
		}

		deleteTempFile(temp);

		return false;
	}

	private void deleteTempFile( File temp )
	{
		if ( temp == null || !temp.exists() )
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
		return delete(bucket_name, key);
	}

	public boolean delete( String bucket_name, final StorageKey key )
	{
		if ( isReadOnly() )
			return false;

		try
		{
			DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.build();

			CompletableFuture<DeleteObjectResponse> response_future = client.deleteObject(request);
			DeleteObjectResponse response = response_future.join();
			if ( isCacheEnabled() )
			{
				removeFromCache(key);
			}
			return true;
		}
		catch ( Exception e )
		{
			LOGGER.error("Deletion exception", e);
			return false;
		}
	}

	@Override
	public StorageMetadata getObjectMetadata( final StorageKey key, final StorageMetadata default_value )
	{
		return getObjectMetadata(bucket_name, key, default_value);
	}

	public StorageMetadata getObjectMetadata( String bucket_name, final StorageKey key, final StorageMetadata default_value )
	{
		try
		{
			HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
					.bucket(bucket_name)
					.key(key.toString())
					.build();

			CompletableFuture<HeadObjectResponse> s3MetadataFuture = client.headObject(headObjectRequest);
			HeadObjectResponse s3_metadata = s3MetadataFuture.join();

			long last_modified = Date.from(s3_metadata.lastModified()).getTime();
			long size = s3_metadata.contentLength();
			String etag = s3_metadata.eTag();

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
			LOGGER.error("Meta data retrieval exception", e);
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
				root += "/"
						+ getOptionalPrefix(null);
			}

			ListObjectsV2Request request = ListObjectsV2Request.builder()
					.bucket(bucket_name)
					.prefix(root + "/")
					.build();

			while ( true )
			{
				CompletableFuture<ListObjectsV2Response> object_listing_future = client.listObjectsV2(request);
				ListObjectsV2Response object_listing = object_listing_future.join();

				if ( null == object_listing )
					return Result.ERROR;

				for ( S3Object summary : object_listing.contents() )
				{
					final String key = summary.key(); // The full S3 key, also the StorageKey
					final String full_key_name = key.substring(root.length()
							+ 1); // The "filename" without the
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

				request = ListObjectsV2Request.builder()
						.bucket(bucket_name)
						.prefix(root + "/")
						.continuationToken(object_listing.nextContinuationToken())
						.build();
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