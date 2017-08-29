package org.jimmutable.gcloud;

import junit.framework.TestCase;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
public class StorageKeyTest extends TestCase {
	public void testParsing(){
		StorageKey key = new StorageKey("alpha/1234.txt");
		assertEquals("alpha",key.getSimpleKind());
		assert(key.getSimpleId().contains("1234"));
		assertEquals(StorageKeyExtension.TEXT+StorageKeyExtension.TXT,key.getSimpleExtension());
	}
	public void testFancyParsing(){
		Kind kind = new Kind("alpha");
		ObjectId objectId = new ObjectId("1234");
		String extension = StorageKeyExtension.TXT;
		StorageKey key = new StorageKey(kind, objectId,extension);
		assertEquals("alpha",key.getSimpleKind());
		assert(key.getSimpleId().contains("1234"));
		assertEquals(StorageKeyExtension.TEXT+StorageKeyExtension.TXT,key.getSimpleExtension());
	}
}
