package org.jimmutable.gcloud.storage;

import org.jimmutable.gcloud.pubsub.TopicIdTest;

//import static org.junit.Assert.*;

import org.jimmutable.gcloud.storage.ObjectStore;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageException;

public class ObjectStoreTest extends TestCase
{

	public ObjectStoreTest(String testName)
	{
		super(testName);
	}

	public static Test suite()
	{
		return new TestSuite(ObjectStoreTest.class);
	}

	public void testObjectStore()
	{
		// Verify that the credentials are setup
		String credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
		String test_object_name = "Test PDF File";
		String test_object_name_comp = "Test Compressed PDF file";

		if (credentials == null)
		{
			System.err.println(
					"You need to set the GOOGLE_APPLICATION_CREDENTIALS environment variable to be the path of a JSON credentials file.");
			System.err.println("For example: /Users/preston/platform-test-cred.json");
			System.err.println(
					"To do this in eclipse, go to Run > Run Configurations..., switch to the Environment tab and add the variable there");
			System.exit(1);
		}

		String JSON_data = "{\n" + "  \"type\": \"service_account\",\n"
				+ "  \"project_id\": \"platform-test-174921\",\n"
				+ "  \"client_email\": \"platform-test-174921@appspot.gserviceaccount.com\",\n"
				+ "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
				+ "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n"
				+ "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
				+ "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/platform-test-174921%40appspot.gserviceaccount.com\"\n"
				+ "}";

		String HTML_data = "<!DOCTYPE html>\n" + "<html>\n" + "<body>\n" + "\n" + "<h1>My First Heading</h1>\n" + "\n"
				+ "<p>My first paragraph.</p>\n" + "\n" + "</body>\n" + "</html>";

		try
		{
			ObjectStore objStore = new ObjectStore("throw-away-test");
			Bucket bucket = objStore.getBucket();
			if (bucket == null)
			{
				objStore.createBucket("throw-away-test");
			}
			bucket = objStore.getBucket();
			if (bucket != null)
			{
				objStore.printBucketInfo();
			}
			// objStore.createBlob("MyBlobTextName","This is my test blob data of type
			// text.");
			// objStore.deleteBlob("MyBlobTextName");
			// objStore.uploadFileToBlob("/Users/preston/SampleDataForGAE.pdf",
			// test_object_name, "application/pdf");
			// objStore.uploadAndCompressFileToBlob("/Users/preston/SampleDataForGAE.pdf",
			// test_object_name_comp, "application/pdf");
			// objStore.uploadAndCompressStringToBlob("Test Compressed Text file",
			// "text/plain","This is test data. It could be XML, JSON, etc.");
			// objStore.uploadStringToBlob("Test uncompressed text file", "This is test
			// data. It could be XML, JSON, etc.");
			// objStore.uploadJSONToBlob("Test JSON File", JSON_data);
//			 objStore.uploadHTMLToBlob("Test HTML data", HTML_data);
			// Created additional generation
//			 objStore.uploadHTMLToBlob("Test HTML data", HTML_data);
			// assert(objStore.objectExists(test_object_name));
			// objStore.printBlobInfo(objStore.getBlob(test_object_name));
			// objStore.makeObjectPublic(test_object_name);
			// objStore.downloadFileFromBlob(test_object_name, "/Users/preston/Documents/");
			// assert(objStore.deleteBucket());
			// objStore.listBlobsInBucket(true);
//			objStore.listBlobsAllVersions("Test HTML");
//			Blob blob = objStore.getBlob("Test HTML data");
//			System.out.println(blob);
			Page<Blob> blobs = objStore.getBlobs("Test HTML data");
			long gen = 0;
			for (Blob loopBlob : blobs.iterateAll())
			{
				objStore.printBlobInfo(loopBlob);
				if (loopBlob.getDeleteTime() != null)
				{
					gen = loopBlob.getGeneration();
					break;
				}
			}
			Blob blob = objStore.getBlobWithGeneration("Test HTML data", gen);
			objStore.printBlobInfo(blob);
			assert (true);
		} catch (StorageException se)
		{
			System.out.println("Read of object failed");
		} catch (Exception e)
		{
			assert (false);
		}

	}
}
