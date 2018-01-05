package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StorageDevLocalFileSystemTest extends IntegrationTest
{

	private static StorageDevLocalFileSystem sdlfs;

	private static ApplicationId applicationId;

	@BeforeClass
	public static void setUpTest()
	{

		IntegrationTest.setupEnvironment();

		applicationId = CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId();
		sdlfs = new StorageDevLocalFileSystem(false, applicationId);
	}

	@Test
	public void testUpsert()
	{
		// test insert
		assertTrue(sdlfs.upsert(new ObjectIdStorageKey("alpha/0000-0000-0000-0123.txt"), "Hello from the other side".getBytes(), false));
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + applicationId + "/alpha/0000-0000-0000-0123.txt");// application
		// id
		// needs
		// to
		// go
		// here
		assertTrue(f.exists());
		String result = readFile(f);
		assertEquals("Hello from the other side", result);

		// test update
		assertTrue(sdlfs.upsert(new ObjectIdStorageKey("alpha/0000-0000-0000-0123.txt"), "I wish I called a thousand times".getBytes(), false));
		result = readFile(f);
		assertEquals("I wish I called a thousand times", result);

		// load testing, we are commenting this out so we do not have put in our source
		// control
		// File imgPath = new File(System.getProperty("user.home") +
		// "/src/jimmutable/core/aws/src/test/java/org/jimmutable/storage/TychoSkymapII.t5_16384x08192.jpg");//
		// this file is 25 MB
		// BufferedImage bufferedImage = null;
		// try
		// {
		// bufferedImage = ImageIO.read(imgPath);
		// }
		// catch ( IOException e )
		// {
		// e.printStackTrace();
		// }

		// get DataBufferBytes from Raster
		// WritableRaster raster = bufferedImage.getRaster();
		// DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
		// assertTrue(sdlfs.upsert(new StorageKey("alpha/0000-0000-0000-0123.txt"),
		// data.getData(), false));

	}
	
	@Test
	public void testGetMetadata()
	{
		ObjectIdStorageKey storage_key = new ObjectIdStorageKey("metadata-test/42fb-e16d-95ac-8274.txt");
		String test_message = "This is a metadata test xD";
		
		sdlfs.upsert(storage_key, test_message.getBytes(), false);
		StorageMetadata metadata = sdlfs.getObjectMetadata(storage_key, null);

		// only check against the length of bytes, as the file will get wiped after the test		
		assertNotNull(metadata);
		assertEquals(metadata.getSimpleSize(), test_message.length());
	}

	private static String readFile(File f)
	{
        byte[] bytesArray = new byte[(int) f.length()];
        
        try (FileInputStream fis = new FileInputStream(f))
        {
            fis.read(bytesArray); // read file into bytes[]
        }
        catch (Exception e)
        {
            throw new RuntimeException("Something went wierd when reading the file");
        }
        
        return new String(bytesArray);
    }
    
	@Test
	public void testExists()
	{
		ObjectIdStorageKey key = new ObjectIdStorageKey("epsilon/0000-0000-0000-0123.txt");
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		assertTrue(sdlfs.exists(key, false));// true positive
		key = new ObjectIdStorageKey("epsilon/0000-0000-0000-0456.txt");
		assertTrue(sdlfs.exists(key, true));// false positive
		assertFalse(sdlfs.exists(key, false));// true negative
	}

	@Test
	public void testGetCurrentVersion()
	{
		// test that it will read from file
		ObjectIdStorageKey key = new ObjectIdStorageKey("zeta/0000-0000-0000-0123.txt");
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		byte[] testvalue = sdlfs.getCurrentVersion(key, null);
		assertEquals("Hello from the other side", new String(testvalue));

		// test that it will return the default if no file is found.
		key = new ObjectIdStorageKey("zeta/0000-0000-0000-0456.txt");
		testvalue = sdlfs.getCurrentVersion(key, "I wish I called a thousand times".getBytes());
		assertEquals("I wish I called a thousand times", new String(testvalue));
	}

	@Test
	public void testDelete()
	{
		// test deleting
		ObjectIdStorageKey key = new ObjectIdStorageKey("beta/0000-0000-0000-0123.txt");
		assertTrue(sdlfs.upsert(key, "Hello from the other side".getBytes(), false));
		assertTrue(sdlfs.delete(key));
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + applicationId + "/beta/0000-0000-0000-0123.txt");// application
		// id
		// needs
		// to
		// go
		// here
		assertFalse(f.exists());

		// should not return a positive message if no file exists to delete.
		assertFalse(sdlfs.delete(key));
	}

	static private class ScanCounter implements StorageKeyHandler
	{
	    // By definition, this will be used in a multithreaded environment
	    private final AtomicInteger count;
	    
	    public int getCount() { return count.get(); }
	    
	    public ScanCounter()
        {
	        count = new AtomicInteger(0);
        }
	    
        @Override
        public void handle(StorageKey key)
        {
            count.incrementAndGet();
        }
	}
	
	@Test
	public void testObjectIdScan()
	{
		Kind gamma = new Kind("gamma");
		
		sdlfs.upsert(new ObjectIdStorageKey("gamma/1.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/2.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/3.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/4.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/5.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/6.txt"), "Hello from the other side".getBytes(), false);
		
        ScanCounter count = new ScanCounter();
		assertTrue(sdlfs.scan(gamma, count, 2));
		assertEquals(6, count.getCount());

		sdlfs.upsert(new ObjectIdStorageKey("gamma/" + new ObjectId(Long.MAX_VALUE) + ".txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/" + new ObjectId(Long.MAX_VALUE-1) + ".txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new ObjectIdStorageKey("gamma/" + new ObjectId(Long.MAX_VALUE-16) + ".txt"), "Hello from the other side".getBytes(), false);
		
        ScanCounter filtered_count = new ScanCounter();
        assertTrue(sdlfs.scan(gamma, new StorageKeyName("7fff"), filtered_count, 2));
		assertEquals(filtered_count.getCount(), 3);
		
        ScanCounter object_id_count = new ScanCounter();
        assertTrue(sdlfs.scanForObjectIds(gamma, object_id_count, 2));
		assertEquals(object_id_count.getCount(), 9);
	}

	@Test
	public void testGenericScan()
	{
		Kind delta = new Kind("delta");

		sdlfs.upsert(new GenericStorageKey("delta/this-is_a_test.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new GenericStorageKey("delta/this-is_not_a-pipe.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new GenericStorageKey("delta/t4i5-is_no7_a-p1pe.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new GenericStorageKey("delta/where-areyou_going-where-have_you_been.txt"), "Hello from the other side".getBytes(), false);
		
        ScanCounter count = new ScanCounter();
        assertTrue(sdlfs.scan(delta, count, 2));
		assertEquals(4, count.getCount());

        ScanCounter object_id_count = new ScanCounter();
        assertTrue(sdlfs.scanForObjectIds(delta, object_id_count, 2));
		assertEquals(0, object_id_count.getCount());
		
        ScanCounter prefix_count = new ScanCounter();
        assertTrue(sdlfs.scan(delta, new StorageKeyName("this"), prefix_count, 2));
		assertEquals(2, prefix_count.getCount());
		
        ScanCounter full_name_search = new ScanCounter();
        assertTrue(sdlfs.scan(delta, new StorageKeyName("where-areyou_going-where-have_you_been"), full_name_search, 2));
		assertEquals(1, full_name_search.getCount());
	}

	
	@Test
	public void testCantWriteToReadOnly()
	{
		StorageDevLocalFileSystem readonly = new StorageDevLocalFileSystem(true, applicationId);
		// test that we cannot upsert
		ObjectIdStorageKey key = new ObjectIdStorageKey("eta/0000-0000-0000-0123.txt");
		assertFalse(readonly.upsert(key, "Hello from the other side".getBytes(), false));

		// ensure that file was not actually created
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + applicationId + "/eta/0000-0000-0000-0123.txt");
		assertFalse(f.exists());

		// create a file to ensure that we have something to delete.
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		assertFalse(readonly.delete(key));
		assertTrue(f.exists());
	}

	@AfterClass
	public static void tearDown()
	{
		String filePathString = System.getProperty("user.home") + "/jimmutable_dev/" + applicationId;// application
		// id
		// needs
		// to
		// go
		// here
		File f = new File(filePathString);
		if (f.exists())
		{
			Path rootPath = Paths.get(filePathString);
			try
			{
				Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
