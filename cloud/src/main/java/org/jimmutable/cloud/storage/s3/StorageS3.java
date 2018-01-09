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
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.storage.GenericStorageKey;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.Storage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.cloud.storage.StorageKeyName;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.threading.OperationPool;
import org.jimmutable.core.threading.OperationRunnable;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

public class StorageS3 extends Storage
{
    static private final Logger LOGGER = LogManager.getLogger(StorageS3.class);
    
    static private final String BUCKET_NAME_PREFIX = "jimmutable-app-";
    
    final private AmazonS3Client client;
    final private String bucket_name;
    
    public StorageS3(final AmazonS3ClientFactory client_factory, final boolean is_read_only)
    {
        super(is_read_only);
        
        Validator.notNull(client_factory);
        client = client_factory.create();
        
        bucket_name = BUCKET_NAME_PREFIX + CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId();
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
            e.printStackTrace();
            return default_value;
        }
    }
    
    @Override
    public boolean upsert(final StorageKey key, final byte[] bytes, final boolean hint_content_likely_to_be_compressible)
    {
        // TODO If bytes.length > 25 MB, log a warning. No need to throw error since the damage is done at that point.
        return upsert(key, new ByteArrayInputStream(bytes), hint_content_likely_to_be_compressible);
    }
    
    // TODO Elevate into IStorage
    // TODO Javadoc
    public boolean upsert(final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible)
    {
        Validator.notNull(key, source);

        final String log_prefix = "[upsert(" + key + ")] ";
        
        try
        {
            final File temp = File.createTempFile("storage_s3_", null);
            
            LOGGER.trace(log_prefix + "Writing source to temp file");
            // TODO This should be a lib method somewhere
            try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(temp)))
            {
                byte[] buf = new byte[1024 * 1024];
                int bytes_read = 0;
                
                do
                {
                    bytes_read = source.read(buf);
                    fout.write(buf, 0, bytes_read);
                }
                while (bytes_read > 0);
            }
            
            final String s3_key = key.toString();
            
            Upload upload = null;
            
            try
            {
                TransferManager transfer_manager = TransferManagerBuilder.standard().withS3Client(client).build();
                
                upload = transfer_manager.upload(bucket_name, s3_key, temp);
                
                LOGGER.info(log_prefix + "Upload: " + upload.getDescription());
                
                while (! upload.isDone())
                {
                    LOGGER.trace(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred());
                    try { Thread.sleep(500); } catch (Exception e) { e.printStackTrace(); } // give progress updates every .5 sec
                }
                
                LOGGER.trace(log_prefix + "Progress: " + upload.getProgress().getPercentTransferred()); // give the 100 percent before exiting
                
                // After the upload is complete, call shutdownNow to release the resources.
                transfer_manager.shutdownNow();

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
    public byte[] getCurrentVersion(final StorageKey key, final byte[] default_value)
    {
        // TODO Limit the size of the output stream to 25MB. Log error and return default_value.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final boolean result = getCurrentVersion(key, bytes);
        
        if (result)
        {
            // TODO Not in love with copying the array, but may not be a better choice
            return bytes.toByteArray();
        }
        else
        {
            return default_value;
        }
    }
    
    // TODO Elevate into IStorage
    // sink is flushed but not closed
    // return false on error, true otherwise (like delete)
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
                TransferManager transfer_manager = TransferManagerBuilder.standard().withS3Client(client).build();
                
                download = transfer_manager.download(bucket_name, s3_key, temp);
                
                LOGGER.info(log_prefix + "Download: " + download.getDescription());
                
                while (! download.isDone())
                {
                    LOGGER.trace(log_prefix + "Progress: " + download.getProgress().getPercentTransferred());
                    try { Thread.sleep(500); } catch (Exception e) { e.printStackTrace(); } // give progress updates every .5 sec
                }
                
                LOGGER.trace(log_prefix + "Progress: " + download.getProgress().getPercentTransferred()); // give the 100 percent before exiting
                
                // After the download is complete, call shutdownNow to release the resources.
                transfer_manager.shutdownNow();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                download.abort();
                return false;
            }
            
            LOGGER.trace(log_prefix + "Writing temp file to sink");
            // TODO This should be a lib method somewhere
            try (InputStream fin = new BufferedInputStream(new FileInputStream(temp)))
            {
                byte[] buf = new byte[1024 * 1024];
                int bytes_read = 0;
                
                do
                {
                    bytes_read = fin.read(buf);
                    sink.write(buf, 0, bytes_read);
                }
                while (bytes_read > 0);
            }
            finally
            {
                sink.flush();
            }
            
            return true;
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
        try
        {
            client.deleteObject(new DeleteObjectRequest(bucket_name, key.toString()));
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
            return default_value;
        }
    }
    
    @Override
    public boolean scan(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads)
    {
        return scanImpl(kind, prefix, handler, num_handler_threads, false);
    }
    
    @Override
    public boolean scanForObjectIds(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads)
    {
        return scanImpl(kind, prefix, handler, num_handler_threads, true);
    }
    
    private boolean scanImpl(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads, final boolean only_object_ids)
    {
        // TODO Abstract scanImpl into (Abstract)Storage
        Scanner scanner = new Scanner(kind, prefix, only_object_ids);
        OperationPool pool = new OperationPool(scanner, num_handler_threads);
        
        scanner.setSink((StorageKey key) ->
        {
            pool.submitOperation(new StorageKeyHandlerWorker(handler, key));
        });
        
        OperationRunnable.Result result = OperationRunnable.execute(pool, OperationRunnable.Result.ERROR);
        return OperationRunnable.Result.SUCCESS == result;
    }
    
    static private class StorageKeyHandlerWorker extends OperationRunnable
    {
        private final StorageKeyHandler handler;
        private final StorageKey key;
        
        public StorageKeyHandlerWorker(final StorageKeyHandler handler, final StorageKey key)
        {
            this.handler = handler;
            this.key = key;
        }
        
        @Override
        protected Result performOperation() throws Exception
        {
            if (null == handler) return Result.SUCCESS;
            
            handler.handle(key);
            
            return Result.SUCCESS;
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
    private class Scanner extends OperationRunnable
    {
        private final Kind kind;
        private final StorageKeyName prefix;
        private final boolean only_object_ids;
        
        private Consumer<StorageKey> sink;
        
        public Scanner(final Kind kind, final StorageKeyName prefix, final boolean only_object_ids)
        {
            Validator.notNull(kind);
            
            this.kind = kind;
            this.prefix = prefix;
            this.only_object_ids = only_object_ids;
        }
        
        @Override
        protected Result performOperation() throws Exception
        {
            Validator.notNull(sink);
            
            final String root = kind.getSimpleValue() + (null != prefix ? ("/" + prefix) : "");
            // TODO Normalization for whether or not _prefix_ includes '/'
            
            ListObjectsRequest request = new ListObjectsRequest(bucket_name, root, null, null, -1);
            
            while(true)
            {
                ObjectListing object_listing = client.listObjects(request);
                if (null == object_listing) return Result.ERROR; 
    
                object_listing.getObjectSummaries().forEach((S3ObjectSummary s) ->
                {
                    final String key = s.getKey(); // The full S3 key, also the StorageKey
                    final String key_name = key.substring(root.length()); // The "filename"
                    
                    String[] key_name_and_ext = key_name.split("\\.");
                    StorageKeyName name = new StorageKeyName(key_name_and_ext[0]);

                    if (name.isObjectId())
                    {
                        sink.accept(new ObjectIdStorageKey(key));
                    }
                    else
                    {
                        if (! only_object_ids)
                        {
                            sink.accept(new GenericStorageKey(key));
                        }
                    }
                });
                
                if (! object_listing.isTruncated()) break;
                request.setMarker(object_listing.getNextMarker());
            }
            
            return shouldStop() ? Result.STOPPED : Result.SUCCESS;
        }
        
        /**
         * The sink has to be set after construction to avoid a race condition
         * between construction of the OperationPool and construction the seed
         * OperationRunnable
         * 
         * @param handler
         */
        public void setSink(Consumer<StorageKey> sink)
        {
            this.sink = sink;
        }
    }
}
