package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for ElasticSearch, using the REST client. (Needed to move to
 * REST client for stability before going into production)
 * 
 * @author salvador.salazar
 *
 */
public class ElasticSearchRESTIT extends IntegrationTest
{
	private static ElasticSearchRESTClient elastic_search;

	@BeforeClass
	public static void setup()
	{
		setupEnvironment();
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);
		SearchSync.registerIndexableKind(TestLibraryPatron.class);
	}

	@Before
	public void seedTestData()
	{
		elastic_search = new ElasticSearchRESTClient();
		elastic_search.upsertIndex(MyIndexable.SEARCH_INDEX_DEFINITION);

		for ( int i = 0; i < 20; i++ )
		{
			elastic_search.upsertDocumentAsync(new MyIndexable(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}
	}

	@After
	public void removeTestData()
	{
		for ( int i = 0; i < 20; i++ )
		{
			elastic_search.deleteDocument(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i)));
		}
	}

	@AfterClass
	public static void shutdown()
	{

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(3);
		elastic_search.shutdownDocumentUpsertThreadPool(3);
	}

	private static TestLibraryPatron patron_in_storage_and_search;
	private static TestLibraryPatron patron_in_only_search;
	private static TestLibraryPatron patron_in_only_storage;

	@Test
	public void testReindex()
	{
		// Setup new index if needed
		elastic_search.upsertIndex(TestLibraryPatron.INDEX_MAPPING);

		patron_in_storage_and_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(0), "firstname1", "lastname1", "emailaddress1", "ssn1", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(23), StorageKeyExtension.JSON));
		elastic_search.upsertDocumentAsync(patron_in_storage_and_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_storage_and_search, Format.JSON_PRETTY_PRINT);

		patron_in_only_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(1), "firstname2", "lastname2", "emailaddress2", "ssn2", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(231), StorageKeyExtension.JSON));
		elastic_search.upsertDocumentAsync(patron_in_storage_and_search);

		patron_in_only_storage = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(2), "firstname3", "lastname3", "emailaddress3", "ssn3", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(3211), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_only_storage, Format.JSON_PRETTY_PRINT);

		elastic_search.reindex(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage(), TestLibraryPatron.KIND);

		try
		{
			Thread.sleep(1000);
		}
		catch ( InterruptedException e1 )
		{
			e1.printStackTrace();
		}

		StandardSearchRequest search_request = new StandardSearchRequest("*", 10000, 0);
		List<OneSearchResultWithTyping> response = elastic_search.search(TestLibraryPatron.INDEX_DEFINITION, search_request, new FieldArrayList<>());
		boolean has_only_storage_result = false;
		boolean has_search_only_result = false;

		try
		{
			for ( OneSearchResultWithTyping entry : response )
			{
				FieldMap<FieldName, FieldArrayList<String>> map = entry.getSimpleContents();

				ObjectId cur_id = new ObjectId(map.get(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName()).get(0));

				if ( patron_in_only_storage.getSimpleObjectId().equals(cur_id) )
				{
					has_only_storage_result = true;
				}

				if ( patron_in_only_search.getSimpleObjectId().equals(cur_id) )
				{
					// We don't want this to happen since this should have been deleted since we
					// don't have it in storage
					has_search_only_result = true;
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			fail();
		}

		// This says our entry that was only in storage made it to search with the
		// script
		assertTrue(has_only_storage_result);

		// This says our entry that was only in search was deleted by our script when it
		// was not found in storage
		assertFalse(has_search_only_result);

		assertTrue(elastic_search.deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_only_storage.getSimpleSearchDocumentId()));
		assertTrue(elastic_search.deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_storage_and_search.getSimpleSearchDocumentId()));
	}

	@Test
	public void putAllFieldMappings()
	{
		assertTrue(elastic_search.putAllFieldMappings(MyIndexable.SEARCH_INDEX_DEFINITION));

		Builder b = new Builder(MyIndexable.SEARCH_INDEX_DEFINITION);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test1"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test2"), SearchIndexFieldType.INSTANT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test3"), SearchIndexFieldType.LONG));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create();

		assertTrue(elastic_search.putAllFieldMappings(def));

		assertTrue(elastic_search.indexProperlyConfigured(def));

	}

	@Test
	public void testBadQuery()
	{
		StandardSearchRequest request = new StandardSearchRequest("this is a bad query!", 10, 20);
		List<OneSearchResultWithTyping> r1 = elastic_search.search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request, null);

		assertTrue(r1 == null);
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

}
