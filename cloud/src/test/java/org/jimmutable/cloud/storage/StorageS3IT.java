package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.storage.s3.RegionSpecificAmazonS3ClientFactory;
import org.jimmutable.cloud.storage.s3.StorageS3;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;


public class StorageS3IT extends IntegrationTest
{
    static private StorageS3 client;
    
    @BeforeClass
    public static void setUpTest()
    {
        setupEnvironment();
        
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
        
        client = new StorageS3(RegionSpecificAmazonS3ClientFactory.defaultFactory(), CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), false);
        
        // TODO Check that bucket exists
        // TODO Tear down bucket / contents after test
    }
    
    @Test
    public void testExists()
    {
        // Generic keys
        {
            StorageKey generic_exists = new GenericStorageKey("s3-test/exists.txt");
            StorageKey generic_not_exist = new GenericStorageKey("s3-test/imaginary.txt");
            
            client.upsert(generic_exists, "I'm a real boy!".getBytes(), false);
            
            assertTrue(client.exists(generic_exists, false));
            assertFalse(client.exists(generic_not_exist, false));
        }
        
        // ObjectId's
        {
            StorageKey object_id_exists = new GenericStorageKey("s3-test/1111-1111-1111-1111.txt");
            StorageKey object_id_not_exist = new GenericStorageKey("s3-test/FFFF-FFFF-FFFF-FFFF.txt");
            
            client.upsert(object_id_exists, "I think I can... I think I can... I think I can...".getBytes(), false);
            
            assertTrue(client.exists(object_id_exists, false));
            assertFalse(client.exists(object_id_not_exist, false));
        }
    }
}
