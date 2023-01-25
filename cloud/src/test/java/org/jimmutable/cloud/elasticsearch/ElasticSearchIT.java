package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
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

		for ( int i = 0; i < 20; i++ )
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new MyIndexable(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}

		try
		{
			Thread.sleep(3000);
		}
		catch ( InterruptedException e )
		{

		}
	}

	@Test
	public void putAllFieldMappings()
	{
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().putAllFieldMappings(MyIndexable.SEARCH_INDEX_DEFINITION));

		JimmutableBuilder b = new JimmutableBuilder(MyIndexable.SEARCH_INDEX_DEFINITION);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test1"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test2"), SearchIndexFieldType.INSTANT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("test3"), SearchIndexFieldType.LONG));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create();

		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().putAllFieldMappings(def));

		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(def));

	}

	@Test
	public void testBadQuery()
	{

		StandardSearchRequest request = new StandardSearchRequest("this is a bad query!", 10, 20);
		List<OneSearchResultWithTyping> r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), request, null);

		assertNull(r1);

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

	private static TestLibraryPatron patron_in_storage_and_search;
	private static TestLibraryPatron patron_in_only_search;
	private static TestLibraryPatron patron_in_only_storage;

	@Test
	public void IndexProperlyConfigured()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TestLibraryPatron.INDEX_MAPPING);
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(TestLibraryPatron.INDEX_MAPPING));
	}

	@Test
	public void testReindex()
	{
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);
		SearchSync.registerIndexableKind(TestLibraryPatron.class);

		// Setup new index if needed
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TestLibraryPatron.INDEX_MAPPING);

		patron_in_storage_and_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(0), "firstname1", "lastname1", "emailaddress1", "ssn1", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(23), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron_in_storage_and_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_storage_and_search, Format.JSON_PRETTY_PRINT);

		patron_in_only_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(1), "firstname2", "lastname2", "emailaddress2", "ssn2", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(231), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron_in_storage_and_search);

		patron_in_only_storage = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(2), "firstname3", "lastname3", "emailaddress3", "ssn3", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(3211), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_only_storage, Format.JSON_PRETTY_PRINT);

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().reindex(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage(), TestLibraryPatron.KIND);

		try
		{
			Thread.sleep(1000);
		}
		catch ( InterruptedException e1 )
		{
			e1.printStackTrace();
		}

		StandardSearchRequest search_request = new StandardSearchRequest("*", 10000, 0);
		boolean has_only_storage_result = false;
		boolean has_search_only_result = false;
		List<OneSearchResultWithTyping> response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TestLibraryPatron.INDEX_DEFINITION, search_request, null);
		try
		{
			for ( OneSearchResultWithTyping entry : response )
			{
				// FieldMap<FieldName, String> map = entry.getSimpleContents();

				ObjectId cur_id = new ObjectId(entry.readAsAtom(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName(), null));

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
	}

	@AfterClass
	public static void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(25);
		for ( int i = 0; i < 20; i++ )
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i)));
		}

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_only_storage.getSimpleSearchDocumentId());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_only_search.getSimpleSearchDocumentId());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_storage_and_search.getSimpleSearchDocumentId());

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_only_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_storage_and_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_only_storage);
	}

}
