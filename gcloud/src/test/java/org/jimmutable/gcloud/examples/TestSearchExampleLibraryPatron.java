package org.jimmutable.gcloud.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Address;
import org.jimmutable.core.objects.common.CountryCode;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.PostalCode;
import org.jimmutable.gcloud.GCloudTypeNameRegister;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.IndexId;
import org.junit.Before;
import org.junit.Test;

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
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, -1);
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

	}

}
