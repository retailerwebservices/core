package org.jimmutable.cloud.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.storage.StorageDevLocalFileSystem;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.objects.common.Kind;
import org.junit.Before;
import org.junit.BeforeClass;

import junit.framework.TestCase;

public class StorageDevLocalFileSystemTest extends TestCase
{
	static ApplicationId appId;
	static StorageDevLocalFileSystem sdlfs;
	
	@BeforeClass
	protected void setUpTest()
	{
		appId = new ApplicationId("Development");
		CloudExecutionEnvironment.startup(appId);
		JimmutableCloudTypeNameRegister.registerAllTypes();
	}

	@Before
	protected void setUp()
	{
		appId = new ApplicationId("Development");
		sdlfs = new StorageDevLocalFileSystem(false);
	}

	public static void testUpsert()
	{
		// test insert
		assertTrue(sdlfs.upsert(new StorageKey("alpha/0000-0000-0000-0123.txt"), "Hello from the other side".getBytes(), false));
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/alpha/0000-0000-0000-0123.txt");// application id needs to go here
		assertTrue(f.exists());
		String result = readFile(f);
		assertEquals("Hello from the other side", result);

		// test update
		assertTrue(sdlfs.upsert(new StorageKey("alpha/0000-0000-0000-0123.txt"), "I wish I called a thousand times".getBytes(), false));
		result = readFile(f);
		assertEquals("I wish I called a thousand times", result);

		// load testing, we are commenting this out so we do not have put in our source control
//		File imgPath = new File(System.getProperty("user.home") + "/src/jimmutable/core/aws/src/test/java/org/jimmutable/storage/TychoSkymapII.t5_16384x08192.jpg");// this file is 25 MB
//		BufferedImage bufferedImage = null;
//		try
//		{
//			bufferedImage = ImageIO.read(imgPath);
//		}
//		catch ( IOException e )
//		{
//			e.printStackTrace();
//		}

		// get DataBufferBytes from Raster
//		WritableRaster raster = bufferedImage.getRaster();
//		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
//		assertTrue(sdlfs.upsert(new StorageKey("alpha/0000-0000-0000-0123.txt"), data.getData(), false));

	}

	private static String readFile( File f )
	{
		FileInputStream fis = null;
		byte[] bytesArray = new byte[(int) f.length()];
		try
		{
			fis = new FileInputStream(f);
			fis.read(bytesArray); // read file into bytes[]

		}
		catch ( Exception e )
		{
			fail("Something went wierd when reading the file");
		}
		finally
		{
			try
			{
				fis.close();
			}
			catch ( IOException e )
			{
				fail("Something went weird when trying to close the file stream");
			}
		}
		return new String(bytesArray);
	}

	public static void testExists()
	{
		StorageKey key = new StorageKey("epsilon/0000-0000-0000-0123.txt");
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		assertTrue(sdlfs.exists(key, false));// true positive
		key = new StorageKey("epsilon/0000-0000-0000-0456.txt");
		assertTrue(sdlfs.exists(key, true));// false positive
		assertFalse(sdlfs.exists(key, false));// true negative
	}

	public static void testGetCurrentVersion()
	{
		// test that it will read from file
		StorageKey key = new StorageKey("zeta/0000-0000-0000-0123.txt");
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		byte[] testvalue = sdlfs.getCurrentVersion(key, null);
		assertEquals("Hello from the other side", new String(testvalue));

		// test that it will return the default if no file is found.
		key = new StorageKey("zeta/0000-0000-0000-0456.txt");
		testvalue = sdlfs.getCurrentVersion(key, "I wish I called a thousand times".getBytes());
		assertEquals("I wish I called a thousand times", new String(testvalue));
	}

	public static void testDelete()
	{
		// test deleting
		StorageKey key = new StorageKey("beta/0000-0000-0000-0123.txt");
		assertTrue(sdlfs.upsert(key, "Hello from the other side".getBytes(), false));
		assertTrue(sdlfs.delete(key));
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/beta/0000-0000-0000-0123.txt");// application id needs to go here
		assertFalse(f.exists());

		// should not return a positive message if no file exists to delete.
		assertFalse(sdlfs.delete(key));
	}

	public static void testList()
	{
		sdlfs.upsert(new StorageKey("gamma/1.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new StorageKey("gamma/2.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new StorageKey("gamma/3.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new StorageKey("gamma/4.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new StorageKey("gamma/5.txt"), "Hello from the other side".getBytes(), false);
		sdlfs.upsert(new StorageKey("gamma/6.txt"), "Hello from the other side".getBytes(), false);
		Iterable<StorageKey> l = sdlfs.listComplex(new Kind("gamma"), null);
		int count = 0;
		for ( StorageKey storageKey : l )
		{
			count++;
		}
		assertEquals(6, count);
	}

	public static void testCantWriteToReadOnly()
	{
		StorageDevLocalFileSystem readonly = new StorageDevLocalFileSystem(true);
		// test that we cannot upsert
		StorageKey key = new StorageKey("eta/0000-0000-0000-0123.txt");
		assertFalse(readonly.upsert(key, "Hello from the other side".getBytes(), false));

		// ensure that file was not actually created
		File f = new File(System.getProperty("user.home") + "/jimmutable_dev/" + ApplicationId.getOptionalDevApplicationId(appId) + "/eta/0000-0000-0000-0123.txt");
		assertFalse(f.exists());

		// create a file to ensure that we have something to delete.
		sdlfs.upsert(key, "Hello from the other side".getBytes(), false);
		assertFalse(readonly.delete(key));
		assertTrue(f.exists());
	}

	@Override
	protected void tearDown()
	{
		String filePathString = System.getProperty("user.home") + "/jimmutable_dev/" + ApplicationId.getOptionalDevApplicationId(appId);// application id needs to go here
		File f = new File(filePathString);
		if ( f.exists() )
		{
			Path rootPath = Paths.get(filePathString);
			try
			{
				Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
}
