package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class SearchSyncIT extends IntegrationTest
{
	private static SearchIndexDefinition def;
	private static TestLibraryPatron patron_in_storage_and_search;
	private static TestLibraryPatron patron_in_only_search;
	private static TestLibraryPatron patron_in_only_storage;

	@BeforeClass
	public static void setupClass()
	{
		setupEnvironment();
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);
		SearchSync.registerIndexableKind(TestLibraryPatron.class);
	}
	
	@Before
	public void setupTest()
	{

		//Wipe the index before each test
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TestLibraryPatron.INDEX_MAPPING);

		patron_in_storage_and_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(0), "firstname1", "lastname1", "emailaddress1", "ssn1", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(23), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron_in_storage_and_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_storage_and_search, Format.JSON_PRETTY_PRINT);

		patron_in_only_search = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(1), "firstname2", "lastname2", "emailaddress2", "ssn2", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(231), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron_in_storage_and_search);

		patron_in_only_storage = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(2), "firstname3", "lastname3", "emailaddress3", "ssn3", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(3211), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_only_storage, Format.JSON_PRETTY_PRINT);

		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{

		}
	}
	

	@Test
	public void testObjectInStorageNotSearch()
	{
		//Test to ensure that our object that is only in storage to start ends up in search as well
		try
		{
			ReindexITKinds reindexer = new ReindexITKinds(new HashSet<Kind>(Arrays.asList(TestLibraryPatron.KIND)), false);
			reindexer.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
		
		StandardSearchRequest search_request = new StandardSearchRequest("*", 10000, 0);
		JSONServletResponse response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TestLibraryPatron.INDEX_DEFINITION, search_request);
		boolean has_only_storage_result = false;
		if(response instanceof SearchResponseOK)
		{
			try
			{
				for(OneSearchResult entry : ((SearchResponseOK) response).getSimpleResults())
				{
					FieldMap<FieldName, String> map = entry.getSimpleContents();
					
					ObjectId cur_id = new ObjectId(map.get(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName()));
					
					if(patron_in_only_storage.getSimpleObjectId().equals(cur_id))
					{
						has_only_storage_result = true;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail();
			}
		}
		else
		{
			fail();
		}
		
		//This says our entry that was only in storage made it to search with the script
		assertTrue(has_only_storage_result);
	}
	
	@Test
	public void testObjectInSearchNotInStorage()
	{
		//Test to ensure that our object that is only in storage to start ends up in search as well
		try
		{
			ReindexITKinds reindexer = new ReindexITKinds(new HashSet<Kind>(Arrays.asList(TestLibraryPatron.KIND)), false);
			reindexer.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail();
		}
		
		StandardSearchRequest search_request = new StandardSearchRequest("*", 10000, 0);
		JSONServletResponse response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TestLibraryPatron.INDEX_DEFINITION, search_request);
		boolean has_search_only_result = false;
		if(response instanceof SearchResponseOK)
		{
			try
			{
				for(OneSearchResult entry : ((SearchResponseOK) response).getSimpleResults())
				{
					FieldMap<FieldName, String> map = entry.getSimpleContents();
					
					ObjectId cur_id = new ObjectId(map.get(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName()));
					
					if(patron_in_only_search.getSimpleObjectId().equals(cur_id))
					{
						//We don't want this to happen since this should have been deleted since we don't have it in storage
						has_search_only_result = true;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail();
			}
		}
		else
		{
			fail();
		}
		
		//This says our entry that was only in search was deleted by our script when it was not found in storage
		assertFalse(has_search_only_result);
	}

	@After
	public void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_only_storage.getSimpleSearchDocumentId());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_only_search.getSimpleSearchDocumentId());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(TestLibraryPatron.INDEX_DEFINITION, patron_in_storage_and_search.getSimpleSearchDocumentId());

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_only_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_storage_and_search);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_only_storage);

	}
	
	public class ReindexITKinds extends SearchSync
	{
		public ReindexITKinds(Set<Kind> kinds, boolean should_setup_environment)
		{
			super(kinds, should_setup_environment);
		}

		@Override
		public void setupRegisters()
		{
			ObjectParseTree.registerTypeName(TestLibraryPatron.class);
			SearchSync.registerIndexableKind(TestLibraryPatron.class);
		}

		@Override
		public ApplicationId getSimpleApplicationID()
		{
			return new ApplicationId("integration");
		}
	}

}
