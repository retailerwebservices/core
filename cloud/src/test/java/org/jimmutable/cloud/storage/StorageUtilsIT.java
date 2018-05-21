package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.elasticsearch.TestLibraryPatron;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.BeforeClass;
import org.junit.Test;

public class StorageUtilsIT extends IntegrationTest
{
	@BeforeClass
	public static void setup()
	{
		setupEnvironment();
	}
	
	@Test
	public void testGetOptionalFromStorage()
	{
		ObjectParseTree.registerTypeName(TestLibraryPatron.class);
		
		//Simple, upsert the object, attempt to pull it down
		TestLibraryPatron patron_in_storage = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(2), "firstname3", "lastname3", "emailaddress3", "ssn3", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(3211), StorageKeyExtension.JSON));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(patron_in_storage, Format.JSON_PRETTY_PRINT);
		TestLibraryPatron from_storage = (TestLibraryPatron) StorageUtils.getOptionalFromStorage(TestLibraryPatron.KIND, patron_in_storage.getSimpleObjectId(), null);
		assertTrue(patron_in_storage.equals(from_storage));
		
		//Remove the entry ensure that are method gets the update and we no longer can grab the object, it's only in RAM now
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(patron_in_storage);
		from_storage = (TestLibraryPatron) StorageUtils.getOptionalFromStorage(TestLibraryPatron.KIND, patron_in_storage.getSimpleObjectId(), null);
		assertTrue(!patron_in_storage.equals(from_storage));
		
		//Ensure that we can create an object, not put in Storage, and then be unable to get it
		TestLibraryPatron patron_not_in_storage = new TestLibraryPatron(TestLibraryPatron.INDEX_DEFINITION, new ObjectId(23), "firstname3", "lastname3", "emailaddress3", "ssn3", new Day(1, 24, 1990), 2, new ObjectIdStorageKey(new Kind("testss"), new ObjectId(32211), StorageKeyExtension.JSON));
		from_storage = (TestLibraryPatron) StorageUtils.getOptionalFromStorage(TestLibraryPatron.KIND, patron_not_in_storage.getSimpleObjectId(), null);
		assertTrue(from_storage == null);
	}
}
