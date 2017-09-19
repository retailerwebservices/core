package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StressTestSearch
{

	static SearchIndexDefinition def;

	@BeforeClass
	public static void setup2()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);
		CloudExecutionEnvironment.startup(new ApplicationId("trevor"));
		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName(), SearchIndexFieldType.OBJECTID));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_FIRST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_LAST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_BIRTH_DATE.getSimpleFieldName(), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_EMAIL_ADDRESS.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_SSN.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_NUM_BOOKS.getSimpleFieldName(), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_PICTURE.getSimpleFieldName(), SearchIndexFieldType.ATOM));

		IndexDefinition index = new IndexDefinition("trevor:stressed:v1");

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, index);

		def = (SearchIndexDefinition) b.create(null);
	}

	// @BeforeClass
	public static void setupDocuments()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);

		CloudExecutionEnvironment.startup(new ApplicationId("trevor"));

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_OBJECT_ID.getSimpleFieldName(), SearchIndexFieldType.OBJECTID));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_FIRST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_LAST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_BIRTH_DATE.getSimpleFieldName(), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_EMAIL_ADDRESS.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_SSN.getSimpleFieldName(), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_NUM_BOOKS.getSimpleFieldName(), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(TestLibraryPatron.FIELD_PICTURE.getSimpleFieldName(), SearchIndexFieldType.ATOM));

		IndexDefinition index = new IndexDefinition("trevor:stressed:v1");

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, index);

		def = (SearchIndexDefinition) b.create(null);

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearchIndexConfigurationUtils().upsertIndex(def);

		for (int i = 1; i <= 1000000; i++) {
			TestLibraryPatron patron = new TestLibraryPatron(index, new ObjectId(i), "firstname" + i, "lastname" + i, "emailaddress" + i, "ssn" + 1, new Day(1, 24, 1990), 2, new StorageKey(new Kind("somekind"), new ObjectId(i), new StorageKeyExtension("storagekeyextension")));
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(patron);
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}

	}

	@Test
	public void testSearch()
	{

		StandardSearchRequest request = new StandardSearchRequest("email_address:emailaddress9999**", 1, 21);
		JSONServletResponse r1 = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(def.getSimpleIndex(), request);

		assertTrue(r1 instanceof SearchResponseOK);
		if (r1 instanceof SearchResponseOK) {
			SearchResponseOK ok = (SearchResponseOK) r1;

			System.out.println(ok.toString());
		}

	}

	@AfterClass
	public static void shutdown()
	{

		CloudExecutionEnvironment.getSimpleCurrent();

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownThreadPool(10000);

	}

}
