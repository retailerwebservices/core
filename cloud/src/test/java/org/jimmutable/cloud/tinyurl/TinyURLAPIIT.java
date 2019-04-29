package org.jimmutable.cloud.tinyurl;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class TinyURLAPIIT extends IntegrationTest
{
	@Test
	public void testAPI()
	{
		setupEnvironment();

		TinyUrlResult result = TinyURLApi.tinyURLComplex("hello", new TinyUrlResult(new ObjectId(123), "url", "tiny_url"));
		assertEquals("url", result.getSimpleUrl());
		assertEquals("tiny_url", result.getSimpleTinyUrl());
	}

	@Test
	public void testAPIFromStorage()
	{
		setupEnvironment();

		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(TinyUrlResult.INDEX_MAPPING) )
		{

			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TinyUrlResult.INDEX_MAPPING);
			try
			{
				Thread.sleep(2000);
			}
			catch ( InterruptedException e )
			{
			}
		}

		TinyUrlResult stored_value = new TinyUrlResult(new ObjectId(321), "stored_value", "the thing that we stored");
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(stored_value, Format.JSON_PRETTY_PRINT);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(stored_value);

		TinyUrlResult result = TinyURLApi.tinyURLComplex("stored_value", new TinyUrlResult(new ObjectId(123), "url", "tiny_url"));
		assertEquals("stored_value", result.getSimpleUrl());
		assertEquals("the thing that we stored", result.getSimpleTinyUrl());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TinyUrlResult.INDEX_DEFINITION, stored_value.getSimpleSearchDocumentId());
	}

	@Test
	public void testAPIErrorFromStorage()
	{
		setupEnvironment();
		TinyUrlResult stored_value = new TinyUrlResult(new ObjectId(456), "Not_Error", "Not_Error_one");
		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(TinyUrlResult.INDEX_MAPPING) )
		{

			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TinyUrlResult.INDEX_MAPPING);
			try
			{
				Thread.sleep(2000);
			}
			catch ( InterruptedException e )
			{
			}
		}
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(stored_value);
		TinyUrlResult result = TinyURLApi.tinyURLComplex("Not_Error", new TinyUrlResult(new ObjectId(789), "Error", "Error_one"));
		assertEquals("Error", result.getSimpleUrl());
		assertEquals("Error_one", result.getSimpleTinyUrl());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TinyUrlResult.INDEX_DEFINITION, stored_value.getSimpleSearchDocumentId());
	}
}
