package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElasticSearchStressTestIT extends IntegrationTest
{

	static SearchIndexDefinition def;

	@BeforeClass
	public static void setupDocuments()
	{

		setupEnvironment();

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_FIRST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_LAST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_BIRTH_DATE.getSimpleFieldName(), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_EMAIL_ADDRESS.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_SSN.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_NUM_BOOKS.getSimpleFieldName(), SearchIndexFieldType.LONG));

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(TestLibraryPatron.INDEX_MAPPING);

		for (int i = 1; i <= 1000000; i++)
		{
			TestLibraryPatron patron = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(i), "firstname" + i, "lastname" + i, "emailaddress" + i, "ssn" + 1, new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("somekind"), new ObjectId(i), new StorageKeyExtension("storagekeyextension")));
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron);
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{

		}

	}

	@Test
	public void testSearch()
	{

		StandardSearchRequest request = new StandardSearchRequest("email_address:emailaddress9999**", 1, 21);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TestLibraryPatron.INDEX_DEFINITION, request);

		assertTrue(r1 instanceof SearchResponseOK);
		if (r1 instanceof SearchResponseOK)
		{
			SearchResponseOK ok = (SearchResponseOK) r1;

			System.out.println(ok.toString());
		}

	}

	@AfterClass
	public static void shutdown()
	{

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownDocumentUpsertThreadPool(10000);

	}

}
