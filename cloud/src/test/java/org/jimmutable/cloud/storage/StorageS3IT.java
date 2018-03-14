package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.storage.s3.RegionSpecificAmazonS3ClientFactory;
import org.jimmutable.cloud.storage.s3.StorageS3;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.utils.FileUtils;
import org.jimmutable.core.utils.Validator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;


public class StorageS3IT extends IntegrationTest
{
    // We need a flag to prevent deleteBucket() from running when an exception is thrown from setUp()
    static private boolean bucket_created = false;
    
    static private StorageS3 storage;
    
    
    /**********************
     *   Setup/Teardown   *
     *********************/
    
    @BeforeClass
    static public void setup()
    {
        setupEnvironment();
        
        checkCredentials();
        
        // Okay, we're good. Proceed.
        storage = new StorageS3(RegionSpecificAmazonS3ClientFactory.defaultFactory(), CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), false);
        
        createBucket();
    }
    
    static private void checkCredentials()
    {
        try
        {
            DefaultAWSCredentialsProviderChain.getInstance().getCredentials();
        }
        catch (com.amazonaws.SdkClientException e)
        {
            String message = "Must provide AWS credentials, as per DefaultAWSCredentialsProviderChain.\n"
                           + "For EC2 instances, this will be provided by the Role assigned when the instance launched.\n"
                           + "For dev environments, this should be provided by -Daws.accessKeyId and -Daws.secretKey. These values can be found in your IAM account.";
            System.err.println(message);
            
            throw e;
        }
    }
    
    static private void createBucket()
    {
        AmazonS3Client client = RegionSpecificAmazonS3ClientFactory.defaultFactory().create();
        if (client.doesBucketExist(storage.getSimpleBucketName()))
        {
            String message = "The unit test bucket - " + storage.getSimpleBucketName() + " - must NOT already exist.\n"
                           + "This is to provide a clean environment for the integration test.\n"
                           + "It might also be an indication that this test is running on another instance somewhere.\n"
                           + "Please ensure that the test bucket is deleted and try running this test again.";

            System.err.println(message);
            throw new ValidationException();
        }
        
        CreateBucketRequest request = new CreateBucketRequest(storage.getSimpleBucketName(), RegionSpecificAmazonS3ClientFactory.DEFAULT_REGION);
        client.createBucket(request);
        bucket_created = true;
    }
    
    @AfterClass
    static public void tearDown()
    {
        if (bucket_created)
        {
            deleteBucket();
        }
    }
    
    static private void deleteAllObjects()
    {
        AmazonS3Client client = RegionSpecificAmazonS3ClientFactory.defaultFactory().create();
        
        ObjectListing listing = client.listObjects(storage.getSimpleBucketName());
        for (S3ObjectSummary object : listing.getObjectSummaries())
        {
            try
            {
                client.deleteObject(object.getBucketName(), object.getKey());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    static private void deleteAllObjectVersions()
    {
        AmazonS3Client client = RegionSpecificAmazonS3ClientFactory.defaultFactory().create();
        
        VersionListing versions = client.listVersions(new ListVersionsRequest().withBucketName(storage.getSimpleBucketName()));
        for (S3VersionSummary version : versions.getVersionSummaries())
        {
            try
            {
                client.deleteVersion(version.getBucketName(), version.getKey(), version.getVersionId());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    static private void deleteBucket()
    {
        // First, delete all objects
        deleteAllObjects();
        
        // Second, delete all object versions
        deleteAllObjectVersions();
        
        // Finally, delete the bucket itself
        AmazonS3Client client = RegionSpecificAmazonS3ClientFactory.defaultFactory().create();
        client.deleteBucket(storage.getSimpleBucketName());
    }
    
    
    /*************
     *   Tests   
     ************/
    
    @Test
    public void testUpsertAndGet() throws IOException
    {
        // Basic put
        {
            StorageKey test_key = new GenericStorageKey("s3-test-upsert/put.txt");
            
            // Put
            assertTrue(storage.upsert(test_key, "arstarstarst".getBytes(), false));
            
            byte[] test_value = storage.getCurrentVersion(test_key, new byte[0]);
            assertArrayEquals(test_value, "arstarstarst".getBytes());
            
            // Update
            assertTrue(storage.upsert(test_key, "oineoienoienoien".getBytes(), false));
            
            test_value = storage.getCurrentVersion(test_key, new byte[0]);
            assertArrayEquals(test_value, "oineoienoienoien".getBytes());
        }
        
        // Fail if byte[] > 25 MB
        {
            byte[] large_value = new byte[StorageS3.MAX_TRANSFER_BYTES_IN_BYTES + 1];
            Arrays.fill(large_value, (byte) 3);
            
            try
            {
                storage.upsert(new GenericStorageKey("s3-test-upsert/too_big.txt"), large_value, false);
                fail("Expected ValidationException");
            }
            catch (ValidationException e)
            {
                // Expected
            }
        }
        
        // Upsert w/ stream
        {
            File temp_source = File.createTempFile("s3_test_upsert_source_", ".txt");
            File temp_dest = File.createTempFile("s3_test_upsert_dest_", ".txt");
            
            temp_source.deleteOnExit();
            temp_dest.deleteOnExit();
            
            // Put
            FileUtils.quietWriteFile(temp_source, "Over the river and through the woods");
            
            StorageKey stream_key = new GenericStorageKey("s3-test-upsert/stream.txt");
            try (InputStream fin = new FileInputStream(temp_source))
            {
                assertTrue(storage.upsertStreaming(stream_key, fin, false));
            }
            
            try (OutputStream fout = new FileOutputStream(temp_dest))
            {
                assertTrue(storage.getCurrentVersionStreaming(stream_key, fout));
            }
            
            String test_value = FileUtils.getComplexFileContentsAsString(temp_dest, null);
            assertEquals(test_value, "Over the river and through the woods");
            
            // Update
            FileUtils.quietWriteFile(temp_source, "Victoria Falls is really, really high");
            
            try (InputStream fin = new FileInputStream(temp_source))
            {
                assertTrue(storage.upsertStreaming(stream_key, fin, false));
            }
            
            try (OutputStream fout = new FileOutputStream(temp_dest))
            {
                assertTrue(storage.getCurrentVersionStreaming(stream_key, fout));
            }
            
            test_value = FileUtils.getComplexFileContentsAsString(temp_dest, null);
            assertEquals(test_value, "Victoria Falls is really, really high");
        }
    }
    
    @Test
    public void testExists()
    {
        // Generic keys
        {
            StorageKey generic_not_exist = new GenericStorageKey("s3-test-exists/imaginary.txt");
            assertFalse(storage.exists(generic_not_exist, false));
            
            StorageKey generic_exists = new GenericStorageKey("s3-test-exists/exists.txt");
            assertTrue(storage.upsert(generic_exists, "I'm a real boy!".getBytes(), false));
            assertTrue(storage.exists(generic_exists, false));
        }
        
        // ObjectId's
        {
            StorageKey object_id_not_exist = new ObjectIdStorageKey("s3-test-exists/0x7fff-ffff-ffff-ffff.txt");
            assertFalse(storage.exists(object_id_not_exist, false));
            
            StorageKey object_id_exists = new ObjectIdStorageKey("s3-test-exists/1111-1111-1111-1111.txt");
            assertTrue(storage.upsert(object_id_exists, "I think I can... I think I can... I think I can...".getBytes(), false));
            assertTrue(storage.exists(object_id_exists, false));
        }
    }
    
    @Test
    public void testDelete()
    {
        StorageKey test_key = new GenericStorageKey("s3-test-delete/delete_me.txt");
        
        assertFalse(storage.exists(test_key, false));
        
        assertTrue(storage.upsert(test_key, "qwfpqwfpqwfpqwpf".getBytes(), false));
        assertTrue(storage.exists(test_key, false));
        
        assertTrue(storage.delete(test_key));
        assertFalse(storage.exists(test_key, false));
    }
    
    
    @Test
    public void testDeleteAll()
    {
    		//TODO
    }
    
    
    
    @Test
    public void testGetMetadata()
    {
        StorageKey test_key = new GenericStorageKey("s3-test-metadata/metadata.txt");
        assertNull(storage.getObjectMetadata(test_key, null));
        
        assertTrue(storage.upsert(test_key, ".,mka.srntkoiylakrvoscyuanvo".getBytes(), false));
        long after_upsert = System.currentTimeMillis();
        
        StorageMetadata metadata = storage.getObjectMetadata(test_key, null);
        assertNotNull(metadata);
        
        assertEquals(".,mka.srntkoiylakrvoscyuanvo".length(), metadata.getSimpleSize());
        assertTrue(Math.abs(metadata.getSimpleLastModified() - after_upsert) < 1000); // Within 1 sec of after_upsert, since S3 can record the "modified" time slightly differently than us
        assertNotNull(metadata.getSimpleEtag()); // No way to know what this will be
        
        assertNull(storage.getObjectMetadata(new GenericStorageKey("s3-test-metadata/does_not_exist.txt"), null));
    }
    
    private class ConcurrencyTest implements Runnable
    {
        private final StorageKey key;
        
        public ConcurrencyTest(final StorageKey key)
        {
            Validator.notNull(key);
            this.key = key;
        }
        
        @Override
        public void run()
        {
            try
            {
                final String contents1 = String.valueOf(key.hashCode());
                final String contents2 = String.valueOf(3.14 * key.hashCode());
                
                long after_upsert = 0L;
                
                assertFalse(storage.exists(key, false));
                
                // Basic put
                {
                    // Put
                    assertTrue(storage.upsert(key, contents1.getBytes(), false));
                    after_upsert = System.currentTimeMillis();
                    
                    assertTrue(storage.exists(key, false));
                    
                    byte[] test_value = storage.getCurrentVersion(key, new byte[0]);
                    assertArrayEquals(test_value, contents1.getBytes());
                    
                    // Update
                    assertTrue(storage.upsert(key, contents2.getBytes(), false));
                    after_upsert = System.currentTimeMillis();
                    
                    assertTrue(storage.exists(key, false));
                    
                    test_value = storage.getCurrentVersion(key, new byte[0]);
                    assertArrayEquals(test_value, contents2.getBytes());
                }
                
                // Upsert w/ stream
                {
                    File temp_source = File.createTempFile("s3_test_concurrency_source_", ".txt");
                    File temp_dest = File.createTempFile("s3_test_concurrency_dest_", ".txt");
                    
                    temp_source.deleteOnExit();
                    temp_dest.deleteOnExit();
                    
                    // Put
                    FileUtils.quietWriteFile(temp_source, contents1);
                    
                    try (InputStream fin = new FileInputStream(temp_source))
                    {
                        assertTrue(storage.upsertStreaming(key, fin, false));
                        after_upsert = System.currentTimeMillis();
                    }
                    
                    try (OutputStream fout = new FileOutputStream(temp_dest))
                    {
                        assertTrue(storage.getCurrentVersionStreaming(key, fout));
                    }
                    
                    String test_value = FileUtils.getComplexFileContentsAsString(temp_dest, null);
                    assertEquals(test_value, contents1);
                    
                    // Update
                    FileUtils.quietWriteFile(temp_source, contents2);
                    
                    try (InputStream fin = new FileInputStream(temp_source))
                    {
                        assertTrue(storage.upsertStreaming(key, fin, false));
                        after_upsert = System.currentTimeMillis();
                    }
                    
                    try (OutputStream fout = new FileOutputStream(temp_dest))
                    {
                        assertTrue(storage.getCurrentVersionStreaming(key, fout));
                    }
                    
                    test_value = FileUtils.getComplexFileContentsAsString(temp_dest, null);
                    assertEquals(test_value, contents2);
                }
                
                // Metadata
                {
                    StorageMetadata metadata = storage.getObjectMetadata(key, null);
                    assertNotNull(metadata);
                    
                    assertEquals(contents2.length(), metadata.getSimpleSize());
                    assertTrue(Math.abs(metadata.getSimpleLastModified() - after_upsert) < 1000); // Within 1 sec of after_upsert, since S3 can record the "modified" time slightly differently than us
                    assertNotNull(metadata.getSimpleEtag()); // No way to know what this will be
                }
                
                // Delete
                {
                    assertTrue(storage.exists(key, false));
                    assertTrue(storage.delete(key));
                    assertFalse(storage.exists(key, false));
                    
                    assertNull(storage.getObjectMetadata(key, null));
                }
            }
            catch (Exception e)
            {
                // Fail for any exception
                fail(e.getMessage());
            }
        }
    }
    
    @Test
    public void testMultithreadedUsage() throws Throwable
    {
        ExecutorService worker_pool = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();
        
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/one.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x1111-1111-1111-1111.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/two.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x2222-2222-2222-2222.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/three.txt"))));
        
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x3333-3333-3333-3333.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/four.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x4444-4444-4444-4444.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/five.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x5555-5555-5555-5555.txt"))));
        
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/six.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x6666-6666-6666-6666.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/seven.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new ObjectIdStorageKey("s3-test-concurrency/0x7777-7777-7777-7777.txt"))));
        futures.add(worker_pool.submit(new ConcurrencyTest(new GenericStorageKey("s3-test-concurrency/eight.txt"))));
        
        worker_pool.shutdown();
        
        // Wait for all futures to finish
        for (Future<?> future : futures)
        {
            try
            {
                future.get();
            }
            catch (ExecutionException e)
            {
                // If the Future threw an exception, so do we
                throw e.getCause();
            }
        }
    }
}
