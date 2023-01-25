package org.jimmutable.cloud.storage;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.StandardObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StorageMetadataTest extends TestCase
{
	private static final long LAST_MODIFIED = 1514405865987l;
	private static final long SIZE = 100L;
	private static final String ETAG = "33a64df551425fcc55e4d42a148795d9f25f89d4";
	
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public StorageMetadataTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		try
		{
			CloudExecutionEnvironment.startupStubTest(new ApplicationId("storage-metadata-test"));
		} catch (Exception e)
		{

		}
		JimmutableCloudTypeNameRegister.registerAllTypes();

		return new TestSuite(StorageMetadataTest.class);
	}

	public void testBuilder()
	{
		JimmutableBuilder builder = null;
		
		// quick failure test
		{
			builder = new JimmutableBuilder(StorageMetadata.TYPE_NAME);
			try
			{
				builder.create();
				fail();
			}
			catch(Exception e)
			{
				// expect this, required fields not set
			}
		}
		
		// failure for negative nums
		{
			builder = new JimmutableBuilder(StorageMetadata.TYPE_NAME);
			builder.set(StorageMetadata.FIELD_LAST_MODIFIED, 0);
			builder.set(StorageMetadata.FIELD_SIZE, -1);
			
			try
			{
				builder.create();
				fail();
			}
			catch(Exception e)
			{
				//expected to fail
			}
		}
		
		// test not setting etag
		{
			builder = new JimmutableBuilder(StorageMetadata.TYPE_NAME);
			builder.set(StorageMetadata.FIELD_LAST_MODIFIED, LAST_MODIFIED);
			builder.set(StorageMetadata.FIELD_SIZE, SIZE);
			
			StorageMetadata only_required = (StorageMetadata) builder.create();
			
			assertTrue(only_required.getSimpleLastModified() > 0);
			assertTrue(only_required.getSimpleSize() >= 0);
			assertNotNull(only_required.getSimpleEtag());
			assertEquals(only_required.getSimpleEtag(), String.valueOf(LAST_MODIFIED));
		}
		
		// test setting etag
		{
			builder = new JimmutableBuilder(StorageMetadata.TYPE_NAME);
			builder.set(StorageMetadata.FIELD_LAST_MODIFIED, LAST_MODIFIED);
			builder.set(StorageMetadata.FIELD_SIZE, SIZE);
			builder.set(StorageMetadata.FIELD_ETAG, ETAG);
			
			StorageMetadata set_etag = (StorageMetadata) builder.create();
			
			assertTrue(set_etag.getSimpleLastModified() > 0);
			assertTrue(set_etag.getSimpleSize() >= 0);
			assertEquals(set_etag.getSimpleEtag(), ETAG);
		}
	}
	
	public void testSerialization()
	{
		String storage_metadata_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
			     , "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
			     , "  <type_hint>org.jimmutable.cloud.storage.StorageMetadata</type_hint>"
			     , "  <last_modified>1514405865987</last_modified>"
			     , "  <size>100</size>"
			     , "  <etag>33a64df551425fcc55e4d42a148795d9f25f89d4</etag>"
			     , "</object>"
			);

		StorageMetadata storage_metadata = (StorageMetadata)StandardObject.deserialize(storage_metadata_string);

		assertEquals(storage_metadata.getSimpleLastModified(), LAST_MODIFIED);
		assertEquals(storage_metadata.getSimpleSize(), SIZE);
		assertEquals(storage_metadata.getSimpleEtag(), ETAG);
	}
}
