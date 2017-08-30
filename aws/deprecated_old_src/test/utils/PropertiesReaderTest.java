package org.jimmutable.utils;

import java.io.File;

import org.jimmutable.aws.utils.PropertiesReader;
import org.jimmutable.core.utils.FileUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PropertiesReaderTest  extends TestCase 
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public PropertiesReaderTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( PropertiesReaderTest.class );
	}

	public void testPropertiesReader()
	{
		File properties_file = FileUtils.getSimpleFileInHomeDirectory("properties-reader-test.properties");
		properties_file.deleteOnExit();
		
		// Write out the test properties file
		{
			StringBuilder out = new StringBuilder();
			
			out.append("p1=true\n");
			out.append("p2=t\n");
			out.append("p3=yes\n");
			out.append("p4=y\n");
			
			out.append("p5=false\n");
			out.append("p6=f\n");
			out.append("p7=no\n");
			out.append("p8=n\n");
			out.append("p9=\n");
			
			out.append("p10=2176\n");
			out.append("p11=Hello World\n");
			out.append("p12= Hello World \n");
			
			FileUtils.quietWriteFile(properties_file, out.toString());
		}
		
		PropertiesReader r = new PropertiesReader("properties-reader-test.properties");
		
		assertEquals("Hello World", r.readString("p11", null));
		assertEquals("Hello World", r.readString("   \tP11\n", null)); // normalization
		
		// Definitely true values
		{
			assertEquals(true, r.readBoolean("P1", false));
			assertEquals(true, r.readBoolean("p2", false));
			assertEquals(true, r.readBoolean("p3", false));
			assertEquals(true, r.readBoolean("p4", false));
			
			assertEquals(true, r.readBoolean("P1", true));
			assertEquals(true, r.readBoolean("p2", true));
			assertEquals(true, r.readBoolean("p3", true));
			assertEquals(true, r.readBoolean("p4", true));
		}
		
		// Definitely false values
		{
			assertEquals(false, r.readBoolean("P5", false));
			assertEquals(false, r.readBoolean("p6", false));
			assertEquals(false, r.readBoolean("p7", false));
			assertEquals(false, r.readBoolean("p8", false));
			
			assertEquals(false, r.readBoolean("P5", true));
			assertEquals(false, r.readBoolean("p6", true));
			assertEquals(false, r.readBoolean("p7", true));
			assertEquals(false, r.readBoolean("p8", true));
		}
		
		// Values that are not booleans (default value will be returned)
		{
			assertEquals(false, r.readBoolean("P9", false));
			assertEquals(true, r.readBoolean("P9", true));
			
			assertEquals(false, r.readBoolean("P10", false));
			assertEquals(true, r.readBoolean("P10", true));
			
			assertEquals(false, r.readBoolean("P11", false));
			assertEquals(true, r.readBoolean("P11", true));
			
			assertEquals(false, r.readBoolean("undefined", false));
			assertEquals(true, r.readBoolean("undefined", true));
		}
		
		
		// Test strings
		{
			assertEquals(null, r.readString("undefined", null));
			assertEquals("foo", r.readString("undefined", "foo"));
			
			assertEquals(null, r.readString("p9", null));
			
			assertEquals("Hello World", r.readString("p11", null));
			assertEquals("Hello World", r.readString("p12", null));
		}
		
		// Test Int
		{
			assertEquals(-1, r.readInt("undefined", -1));
			assertEquals(911, r.readInt("undefined", 911));
			
			assertEquals(-1, r.readInt("p9", -1));
			
			assertEquals(2176, r.readInt("p10", -1));
		}
		
		// Test Long
		{
			assertEquals(-1, r.readLong("undefined", -1));
			assertEquals(911, r.readLong("undefined", 911));

			assertEquals(-1, r.readLong("p9", -1));

			assertEquals(2176, r.readLong("p10", -1));
		}
	}
}
