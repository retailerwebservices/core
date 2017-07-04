package org.jimmutable.blob_store;

import java.io.File;

import org.jimmutable.aws.blob_store.BlobStore;
import org.jimmutable.aws.blob_store.BlobStoreName;
import org.jimmutable.aws.blob_store.BlobStoreUploadRequest;
import org.jimmutable.aws.environment.ApplicationEnvironment;
import org.jimmutable.aws.environment.CloudName;
import org.jimmutable.aws.s3.S3AbsolutePath;
import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.aws.utils.HttpUtils;
import org.jimmutable.aws.utils.PropertiesReader;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.utils.FileUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BlobStoreTest extends TestCase
{
	static private final String test_blob_contents = "<html><body>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</body></html>";
	
	private UnitTestEnvironment env;
	
	
	static private class UnitTestEnvironment extends ApplicationEnvironment
	{ 
		private BlobStore store;
		private BlobStore read_only_store;
	
		public UnitTestEnvironment()
		{
			super();
			
			store = new BlobStore(this,new BlobStoreName("unit-test-blob-store"), false);
			read_only_store = new BlobStore(this,new BlobStoreName("unit-test-blob-store"), true);
		}
		
		
		public CloudName loadLoadNameFromPropertiesFile(PropertiesReader r, CloudName default_value) 
		{
			return CloudName.UNIT_TEST_CLOUD;
		}
		
		public BlobStore getSimpleStore() { return store; } 
		public BlobStore getSimpleReadOnlyStore() { return read_only_store; }
	}
	
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BlobStoreTest( String testName )
    {
        super( testName );
        
        JimmutableTypeNameRegister.registerAllTypes();
        
        env = new UnitTestEnvironment();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BlobStoreTest.class );
    }
    
    public void testBlobStore()
    {
    	try
    	{
	    	File tmp = File.createTempFile("test", "html");
	    	FileUtils.quietWriteFile(tmp, test_blob_contents);
	    	
	    	
	    	BlobStoreUploadRequest request = new BlobStoreUploadRequest(tmp, S3Path.PATH_BUCKET_ROOT, "lorem-ipsum", "html");
	    	
	    	S3AbsolutePath result = env.store.upload(request, null);
	    	

	    	System.out.println(result);
	    	System.out.println(result.createURL(false));
	    	
	    	String content_from_url = HttpUtils.getURLContentsToString(result.createURL(false), null);
	    	
	    	// Download the blob and make sure it matches what we uploaded
	    	assertEquals(test_blob_contents, content_from_url);
	    	
	    	tmp.delete();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		assert(false);
    	}
    }
}
