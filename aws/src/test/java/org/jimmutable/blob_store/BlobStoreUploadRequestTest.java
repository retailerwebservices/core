package org.jimmutable.blob_store;

import java.io.File;

import org.jimmutable.aws.blob_store.BlobStoreUploadRequest;
import org.jimmutable.aws.s3.S3Path;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BlobStoreUploadRequestTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BlobStoreUploadRequestTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BlobStoreUploadRequestTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	// public BlobStoreUploadRequest(File src, S3Path base_path, String fixed_portion_of_name, String extension)
       try {  new BlobStoreUploadRequest(null, new S3Path("foo"), "bar", "jpg"); assert(false);   } catch(Exception e ) { assert(true); } 
       try {  new BlobStoreUploadRequest(new File("foo.jpg"), null, "bar", "jpg"); assert(false);   } catch(Exception e ) { assert(true); } 
       try {  new BlobStoreUploadRequest(new File("foo.jpg"), new S3Path("foo"), null, "jpg"); assert(false);   } catch(Exception e ) { assert(true); } 
       try {  new BlobStoreUploadRequest(new File("foo.jpg"), new S3Path("foo"), "foo", null); assert(false);   } catch(Exception e ) { assert(true); } 
       
       
       BlobStoreUploadRequest one = new BlobStoreUploadRequest(new File("foo.jpg"), new S3Path("foo"), "foo", "jpg");
       BlobStoreUploadRequest two = new BlobStoreUploadRequest(new File("foo.jpg"), new S3Path("foo"), "FOO", "JPG");
       
       assertEquals(one,two);
       
       two = new BlobStoreUploadRequest(new File("foo.jpg"), new S3Path("foo"), " FOO ", " JPG ");
       
       assertEquals(one,two);
       
       
       two = new BlobStoreUploadRequest(new File("foo2.jpg"), new S3Path("foo"), " FOO ", " JPG ");
       
       assert(!one.equals(two));
    }
}

