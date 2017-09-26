package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticSearchTest
{

	static SearchIndexDefinition def;

	// Uncomment to run test
	// @BeforeClass
	public static void setup()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);

		CloudExecutionEnvironment.startup(new ApplicationId("trevor"));

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("text"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("objectid"), SearchIndexFieldType.OBJECTID));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("trevor:isawesome:v1"));

		def = (SearchIndexDefinition) b.create(null);

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(def);

		for (int i = 0; i < 20; i++)
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new MyIndexable(def.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{

		}

	}

	// Uncomment to run test
	// @Test
	public void testSearchPaginationFirstPage()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 0);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(def.getSimpleIndex(), request);

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
			// TODO what is the point of this??
			assertEquals(ok.getSimpleStartOfPreviousPageOfResults(), -1);

		}

	}

	// Uncomment to run test
	// @Test
	public void testSearchPaginationSecondPage()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 10);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(def.getSimpleIndex(), request);

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
			// TODO what is the point of this??
			assertEquals(10, ok.getSimpleStartOfPreviousPageOfResults());

		}

	}

	/// Uncomment to run test
	// @Test
	public void testSearchPaginationNone()
	{

		StandardSearchRequest request = new StandardSearchRequest("day:>1970-01-01", 10, 20);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(def.getSimpleIndex(), request);

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
			// TODO what is the point of this??
			assertEquals(20, ok.getSimpleStartOfPreviousPageOfResults());
		}

	}

	// Uncomment to run test
	// @Test
	public void testBadQuery()
	{

		StandardSearchRequest request = new StandardSearchRequest("this is a bad query!", 10, 20);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(def.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseError);

	}

	// Uncomment to run test
	// @AfterClass
	public static void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(25);

	}

}
