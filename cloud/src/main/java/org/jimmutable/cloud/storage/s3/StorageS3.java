package org.jimmutable.cloud.storage.s3;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.storage.Storage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.cloud.storage.StorageKeyName;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class StorageS3 extends Storage
{
    static private final String BUCKET_NAME_PREFIX = "jimmutable-app-";
    
    final private AmazonS3Client client;
    final private String bucket_name;
    
    public StorageS3(AmazonS3ClientFactory client_factory, boolean is_read_only)
    {
        super(is_read_only);
        
        Validator.notNull(client_factory);
        client = client_factory.create();
        
        bucket_name = BUCKET_NAME_PREFIX + CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId();
    }
    
    @Override
    public boolean exists(StorageKey key, boolean default_value)
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
    public boolean upsert(StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public byte[] getCurrentVersion(StorageKey key, byte[] default_value)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean delete(StorageKey key)
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
    public boolean scan(Kind kind, StorageKeyName prefix, StorageKeyHandler handler, int num_handler_threads)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean scanForObjectIds(Kind kind, StorageKeyName prefix, StorageKeyHandler handler, int num_handler_threads)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value)
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
    
    public String getSimpleBucketName()
    {
        return bucket_name;
    }
}
