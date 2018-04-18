package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticSearchIT extends IntegrationTest
{

	@BeforeClass
	public static void setup()
	{

		setupEnvironment();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(MyIndexable.SEARCH_INDEX_DEFINITION);

		for (int i = 0; i < 20; i++)
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new MyIndexable(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{

		}

	}
	
	@Test
	public void putAllFieldMappings()
	{
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().putAllFieldMappings(MyIndexable.SEARCH_INDEX_DEFINITION));
		
		
		Builder b = new Builder(MyIndexable.SEARCH_INDEX_DEFINITION);
		
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test1"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test2"), SearchIndexFieldType.INSTANT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test3"), SearchIndexFieldType.LONG));
		
		
		SearchIndexDefinition def = (SearchIndexDefinition)b.create(null);
		
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().putAllFieldMappings(def));
		
		
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(def));
		
	}

	@Test
	public void testSearchPaginationFirstPage()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 0);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseOK);
		if (r1 instanceof SearchResponseOK)
		{
			SearchResponseOK ok = (SearchResponseOK) r1;

			assertEquals(ok.getSimpleFirstResultIdx(), 0);
			assertEquals(ok.getSimpleHasMoreResults(), true);
			assertEquals(ok.getSimpleHasPreviousResults(), false);
			assertEquals(ok.getSimpleHTTPResponseCode(), 200);
			assertEquals(ok.getSimpleResults().size(), 10);
			assertEquals(ok.getSimpleStartOfNextPageOfResults(), 10);
		}

	}

	@Test
	public void testSearchPaginationSecondPage()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 10);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseOK);
		if (r1 instanceof SearchResponseOK)
		{
			SearchResponseOK ok = (SearchResponseOK) r1;

			assertEquals(10, ok.getSimpleFirstResultIdx());
			assertEquals(false, ok.getSimpleHasMoreResults());
			assertEquals(true, ok.getSimpleHasPreviousResults());
			assertEquals(200, ok.getSimpleHTTPResponseCode());
			assertEquals(10, ok.getSimpleResults().size());
			assertEquals(20, ok.getSimpleStartOfNextPageOfResults());

		}

	}

	@Test
	public void testSearchPaginationNone()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 20);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseOK);
		if (r1 instanceof SearchResponseOK)
		{
			SearchResponseOK ok = (SearchResponseOK) r1;

			assertEquals(20, ok.getSimpleFirstResultIdx());
			assertEquals(false, ok.getSimpleHasMoreResults());
			assertEquals(true, ok.getSimpleHasPreviousResults());
			assertEquals(200, ok.getSimpleHTTPResponseCode());
			assertEquals(0, ok.getSimpleResults().size());
			assertEquals(30, ok.getSimpleStartOfNextPageOfResults());
		}

	}

	@Test
	public void testBadQuery()
	{

		StandardSearchRequest request = new StandardSearchRequest("this is a bad query!", 10, 20);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseError);

	}

	@Test
	public void IndexProperlyConfigured()
	{
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(MyIndexable.SEARCH_INDEX_DEFINITION));
	}

	@Test
	public void SearchIndexDefinitionExists()
	{
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(MyIndexable.SEARCH_INDEX_DEFINITION));
	}

	@Test
	public void IndexDefinitionExists()
	{
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex()));
	}

	@AfterClass
	public static void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(25);

	}

}
