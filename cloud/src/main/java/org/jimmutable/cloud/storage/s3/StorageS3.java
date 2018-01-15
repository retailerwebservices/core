package org.jimmutable.cloud.storage.s3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.storage.GenericStorageKey;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.Storage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyName;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.IOUtils;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
    
    // Since this will be init'd in CEE.startup, we can't rely on the singleton for access to the ApplicationId
    public StorageS3(final AmazonS3ClientFactory client_factory, final ApplicationId application_id, final boolean is_read_only)
    {
        super(is_read_only);
        
        bucket_name = BUCKET_NAME_PREFIX + application_id;
        
        Validator.notNull(client_factory);
        client = client_factory.create();
        transfer_manager = TransferManagerBuilder.standard().withS3Client(client).build();
    }
    
    public String getSimpleBucketName()
    {
        return bucket_name;
    }
    
    @Override
    public boolean exists(final StorageKey key, final boolean default_value)
    {
        try
        {
            return client.doesObjectExist(bucket_name, key.toString());
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
            return default_value;
        }
    }
    
    // TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must be able to detect dynamically on read.
    @Override
    public boolean upsert(StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible)
    {
        Validator.max(bytes.length, MAX_TRANSFER_BYTES_IN_BYTES);
        
        if (isReadOnly()) return false;

        try
        {
            InputStream bin = new ByteArrayInputStream(bytes);
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            
            client.putObject(bucket_name, key.toString(), bin, metadata);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
            return false;
        }
    }

    // TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must be able to detect dynamically on read.
    @Override
    public boolean upsert(final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible)
    {
        Validator.notNull(key, source);
        
        if (isReadOnly()) return false;

        final String log_prefix = "[upsert(" + key + ")] ";
        
        try
        {
            final File temp = File.createTempFile("storage_s3_", null);
            
            LOGGER.debug(log_prefix + "Writing source to temp file");
            try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(temp)))
            {
                IOUtils.transferAllBytes(source, fout);
            }
            
            final String s3_key = key.toString();
            
            Upload upload = null;
            
            try
            {
                upload = transfer_manager.upload(bucket_name, s3_key, temp);
                
                LOGGER.info(log_prefix + "Upload: " + upload.getDescription());
                
                while (! upload.isDone())
                {
                    LOGGER.debug(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred());
                    try { Thread.sleep(TRANSFER_MANAGER_POLLING_INTERVAL_MS); } catch (Exception e) {} // give progress updates every .5 sec
                }
                
                LOGGER.debug(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred()); // give the 100 percent before exiting

                return TransferState.Completed == upload.getState();
            }
            catch (Exception e)
            {
                LOGGER.catching(e);
                upload.abort();
            }
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
        }
        
        return false;
    }

    @Override
    public byte[] getCurrentVersion(StorageKey key, byte[] default_value)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (OutputStream bout = new IOUtils.LimitBytesOutputStream(bytes, MAX_TRANSFER_BYTES_IN_BYTES))
        {
            try (S3Object s3_obj = client.getObject(bucket_name, key.toString()))
            {
                try (InputStream s3in = s3_obj.getObjectContent())
                {
                    IOUtils.transferAllBytes(s3in, bout);
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
            return default_value;
        }
        
        return bytes.toByteArray();
    }
    
    @Override
    public boolean getCurrentVersion(final StorageKey key, final OutputStream sink)
    {
        Validator.notNull(key, sink);
        
        final String log_prefix = "[getCurrentVersion(" + key + ")] ";
        
        try
        {
            final String s3_key = key.toString();
            final File temp = File.createTempFile("storage_s3_", null);
            
            Download download = null;
            
            try
            {
                download = transfer_manager.download(bucket_name, s3_key, temp);
                
                LOGGER.info(log_prefix + "Download: " + download.getDescription());
                
                while (! download.isDone())
                {
                    LOGGER.debug(log_prefix + "Progress: " + download.getProgress().getPercentTransferred());
                    try { Thread.sleep(TRANSFER_MANAGER_POLLING_INTERVAL_MS); } catch (Exception e) {} // give progress updates every .5 sec
                }
                
                LOGGER.debug(log_prefix + "Progress: " + download.getProgress().getPercentTransferred()); // give the 100 percent before exiting
            }
            catch (Exception e)
            {
                LOGGER.catching(e);
                download.abort();
                return false;
            }
            
            LOGGER.debug(log_prefix + "Writing temp file to sink");
            try (InputStream fin = new BufferedInputStream(new FileInputStream(temp)))
            {
                IOUtils.transferAllBytes(fin, sink);
            }
            
            return TransferState.Completed == download.getState();
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
        }

        return false;
    }
    
    @Override
    public boolean delete(final StorageKey key)
    {
        if (isReadOnly()) return false;

        try
        {
            client.deleteObject(new DeleteObjectRequest(bucket_name, key.toString()));
            return true;
        }
        catch(Exception e)
        {
            LOGGER.catching(e);
            return false;
        }
    }
    
    @Override
    public StorageMetadata getObjectMetadata(final StorageKey key, final StorageMetadata default_value)
    {
        try
        {
            ObjectMetadata s3_metadata = client.getObjectMetadata(bucket_name, key.toString());
            
            long last_modified = s3_metadata.getLastModified().getTime();
            long size = s3_metadata.getContentLength();
            String etag = s3_metadata.getETag();
            
            return new StorageMetadata(last_modified, size, etag);
        }
        catch (AmazonS3Exception e)
        {
            // We get a 404 Not Found for any object that doesn't exist.
            // A separate doesObjectExist call would be an entire extra
            // network round trip... so just special case it.
            if (404 == e.getStatusCode())
            {
                return default_value;
            }
            
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
            return default_value;
        }
    }

    /**
     * This class does the main listing operation for scan*.
     * It runs in it's own thread and throws each StorageKey it
     * finds into another OperationRunnable running in a common
     * pool.
     *
     * @author Jeff Dezso
     */
    private class Scanner extends Storage.Scanner
    {
        public Scanner(final Kind kind, final StorageKeyName prefix, final boolean only_object_ids)
        {
            super(kind, prefix, only_object_ids);
        }
        
        @Override
        protected Result performOperation() throws Exception
        {
            String root = getSimpleKind().getSimpleValue();
            if (hasPrefix())
            {
                root += "/" + getOptionalPrefix(null);
            }
            
            ListObjectsRequest request = new ListObjectsRequest(bucket_name, root, null, null, -1);
            
            while(true)
            {
                ObjectListing object_listing = client.listObjects(request);
                if (null == object_listing) return Result.ERROR; 
    
                for (S3ObjectSummary summary : object_listing.getObjectSummaries())
                {
                    final String key = summary.getKey(); // The full S3 key, also the StorageKey
                    final String key_name = key.substring(root.length()); // The "filename"
                    
                    String[] key_name_and_ext = key_name.split("\\.");
                    StorageKeyName name = new StorageKeyName(key_name_and_ext[0]);

                    if (name.isObjectId())
                    {
                        emit(new ObjectIdStorageKey(key));
                    }
                    else
                    {
                        if (! onlyObjectIds())
                        {
                            emit(new GenericStorageKey(key));
                        }
                    }
                }
                
                if (! object_listing.isTruncated()) break;
                request.setMarker(object_listing.getNextMarker());
            }
            
            return shouldStop() ? Result.STOPPED : Result.SUCCESS;
        }
    }

    @Override
    protected Storage.Scanner createScanner(Kind kind, StorageKeyName prefix, boolean only_object_ids)
    {
        return new Scanner(kind, prefix, only_object_ids);
    }
}
