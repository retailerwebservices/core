package org.jimmutable.gcloud.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Address;
import org.jimmutable.core.objects.common.CountryCode;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.PostalCode;
import org.jimmutable.gcloud.GCloudTypeNameRegister;
import org.jimmutable.gcloud.pubsub.messages.StandardMessageOnUpsert;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.DocumentWriter;
import org.jimmutable.gcloud.search.IndexId;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.search.Document;

public class TestSearchExampleLibraryPatron {

	@Before
	public void setup() {
		GCloudTypeNameRegister.registerAllTypes();
	}

	@Test
	public void testCompareToEquals() {

		Builder builder = new Builder(SearchExampleLibraryPatron.TYPE_NAME);

		/* required */
		builder.set(SearchExampleLibraryPatron.FIELD_FIRST_NAME, "First");
		builder.set(SearchExampleLibraryPatron.FIELD_LAST_NAME, "Last");
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, 0);
		builder.set(SearchExampleLibraryPatron.FIELD_DOCUMENT_ID, new DocumentId("TestLibraryPatron"));
		builder.set(SearchExampleLibraryPatron.FIELD_INDEX_ID, new IndexId("TestIndexId"));
		builder.set(SearchExampleLibraryPatron.FIELD_OBJECT_ID, new ObjectId(000000001));

		/* optional */
		builder.set(SearchExampleLibraryPatron.FIELD_EMAIL_ADDRESS, "email@address.com");
		builder.set(SearchExampleLibraryPatron.FIELD_SSN, "123121234");

		builder.set(SearchExampleLibraryPatron.FIELD_BIRTH_DATE, new Day("01/24/1990"));

		SearchExampleLibraryPatron patron1 = (SearchExampleLibraryPatron) builder.create(null);

		System.out.println(patron1);

		SearchExampleLibraryPatron patron2 = (SearchExampleLibraryPatron) builder.create(null);

		assertEquals(patron1, patron2);

		builder.set(SearchExampleLibraryPatron.FIELD_SSN, null);

		SearchExampleLibraryPatron patron3 = (SearchExampleLibraryPatron) builder.create(null);

		assertFalse(patron1.equals(patron3));

	}

	@Test
	public void testSerialization() {

		String json = "{\n" + "  \"type_hint\" : \"org.jimmutable.gcloud.examples.SearchExampleLibraryPatron\",\n"
				+ "  \"first_name\" : \"First\",\n" + "  \"last_name\" : \"Last\",\n"
				+ "  \"email_address\" : \"email@address.com\",\n" + "  \"ssn\" : \"123121234\",\n"
				+ "  \"number_of_books_checked_out\" : 0,\n" + "  \"birth_date\" : \"01/24/1990\",\n"
				+ "  \"id\" : \"0000-0000-0000-0001\",\n" + "  \"document_id\" : \"testlibrarypatron\",\n"
				+ "  \"index_id\" : \"testindexid\"\n" + "}";
		Builder builder = new Builder(SearchExampleLibraryPatron.TYPE_NAME);

		/* required */
		builder.set(SearchExampleLibraryPatron.FIELD_FIRST_NAME, "First");
		builder.set(SearchExampleLibraryPatron.FIELD_LAST_NAME, "Last");
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, 0);
		builder.set(SearchExampleLibraryPatron.FIELD_DOCUMENT_ID, new DocumentId("TestLibraryPatron"));
		builder.set(SearchExampleLibraryPatron.FIELD_INDEX_ID, new IndexId("TestIndexId"));
		builder.set(SearchExampleLibraryPatron.FIELD_OBJECT_ID, new ObjectId(000000001));

		/* optional */
		builder.set(SearchExampleLibraryPatron.FIELD_EMAIL_ADDRESS, "email@address.com");
		builder.set(SearchExampleLibraryPatron.FIELD_SSN, "123121234");

		builder.set(SearchExampleLibraryPatron.FIELD_BIRTH_DATE, new Day("01/24/1990"));

		SearchExampleLibraryPatron patron1 = (SearchExampleLibraryPatron) builder.create(null);

		SearchExampleLibraryPatron obj = (SearchExampleLibraryPatron) StandardObject.deserialize(json);
		assertEquals(patron1, obj);

	}

	@Test(expected = SerializeException.class)
	public void TestInvalid() {

		Builder builder = new Builder(SearchExampleLibraryPatron.TYPE_NAME);

		/* required */
		builder.set(SearchExampleLibraryPatron.FIELD_FIRST_NAME, "First");
		builder.set(SearchExampleLibraryPatron.FIELD_LAST_NAME, "Last");
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, 0);
		builder.set(SearchExampleLibraryPatron.FIELD_DOCUMENT_ID, new DocumentId("TestLibraryPatron"));
		builder.set(SearchExampleLibraryPatron.FIELD_INDEX_ID, new IndexId("TestIndexId"));

		// missing ObjectId
		// builder.set(SearchExampleLibraryPatron.FIELD_OBJECT_ID, new
		// ObjectId(000000001));

		SearchExampleLibraryPatron patron1 = (SearchExampleLibraryPatron) builder.create(null);

	}

	@Test
	public void TestDocumentWriter() {
		Builder builder = new Builder(SearchExampleLibraryPatron.TYPE_NAME);

		/* required */
		builder.set(SearchExampleLibraryPatron.FIELD_FIRST_NAME, "First");
		builder.set(SearchExampleLibraryPatron.FIELD_LAST_NAME, "Last");
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, 0);
		builder.set(SearchExampleLibraryPatron.FIELD_DOCUMENT_ID, new DocumentId("TestLibraryPatron"));
		builder.set(SearchExampleLibraryPatron.FIELD_INDEX_ID, new IndexId("TestIndexId"));

		builder.set(SearchExampleLibraryPatron.FIELD_OBJECT_ID, new ObjectId(000000001));

		/* optional */
		builder.set(SearchExampleLibraryPatron.FIELD_EMAIL_ADDRESS, "email@address.com");
		builder.set(SearchExampleLibraryPatron.FIELD_SSN, "123121234");

		builder.set(SearchExampleLibraryPatron.FIELD_BIRTH_DATE, new Day("01/24/1990"));

		SearchExampleLibraryPatron patron1 = (SearchExampleLibraryPatron) builder.create(null);

		DocumentWriter writer = new DocumentWriter(patron1.getSimpleSearchDocumentId());
		patron1.writeSearchDocument(writer);

		Document document = writer.createDocument();

		System.out.println(document.getFieldNames());
		System.out.println(SearchExampleLibraryPatron.FIELD_NUM_BOOKS.getSimpleFieldName().getSimpleName());

		System.out.println(
				document.getOnlyField(SearchExampleLibraryPatron.FIELD_SSN.getSimpleFieldName().getSimpleName()));

		assertFalse(document.getFieldNames().contains(SearchExampleLibraryPatron.FIELD_BIRTH_DATE.getSimpleFieldName().getSimpleName()));

		assertTrue(document.getFieldNames()
				.contains(SearchExampleLibraryPatron.FIELD_NUM_BOOKS.getSimpleFieldName().getSimpleName()));

	}

}
