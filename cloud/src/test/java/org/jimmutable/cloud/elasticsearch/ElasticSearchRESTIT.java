package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for ElasticSearch, using the REST client. (Needed to move to REST client for stability before going into production)
 * @author salvador.salazar
 *
 */
public class ElasticSearchRESTIT extends IntegrationTest
{
	private static ElasticSearch elastic_search;
	
	@BeforeClass
	public static void setup()
	{
		setupEnvironment();
		
		elastic_search = new ElasticSearch.RESTClient(); 
		elastic_search.upsertIndex(MyIndexable.SEARCH_INDEX_DEFINITION);
		
		for (int i = 0; i < 20; i++)
		{
			elastic_search.upsertDocumentAsync(new MyIndexable(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}

		try { Thread.sleep(5_000); } catch (InterruptedException e) { e.printStackTrace(); }
	}

	@Test
	public void testSearchPaginationFirstPage()
	{
		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 0);
		JSONServletResponse r1 = elastic_search.search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

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
			assertEquals(ok.getSimpleStartOfPreviousPageOfResults(), -1);
		}
	}

	@Test
	public void testSearchPaginationSecondPage()
	{
		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 10);
		JSONServletResponse r1 = elastic_search.search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

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
			assertEquals(0, ok.getSimpleStartOfPreviousPageOfResults());
		}
	}

	@Test
	public void testSearchPaginationNone()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 20);
		JSONServletResponse r1 = elastic_search.search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

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
			assertEquals(10, ok.getSimpleStartOfPreviousPageOfResults());
		}

	}

	@Test
	public void testBadQuery()
	{
		StandardSearchRequest request = new StandardSearchRequest("this is a bad query!", 10, 20);
		JSONServletResponse r1 = elastic_search.search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseError);
	}

	@Test
	public void IndexProperlyConfigured()
	{
		assertTrue(elastic_search.indexProperlyConfigured(MyIndexable.SEARCH_INDEX_DEFINITION));
	}

	@Test
	public void SearchIndexDefinitionExists()
	{
		assertTrue(elastic_search.indexExists(MyIndexable.SEARCH_INDEX_DEFINITION));
	}

	@Test
	public void IndexDefinitionExists()
	{
		assertTrue(elastic_search.indexExists(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex()));
	}

	@AfterClass
	public static void shutdown()
	{
		elastic_search.deleteIndex(MyIndexable.SEARCH_INDEX_DEFINITION);
		
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(3);
		elastic_search.shutdownDocumentUpsertThreadPool(3);
	}

}