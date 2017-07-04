package org.jimmutable.utils;

import java.util.Random;

import org.jimmutable.aws.utils.CompressionUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CompressionUtilsTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public CompressionUtilsTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( CompressionUtilsTest.class );
	}

	
	public void testByteArrays()
	{
		assertEquals(null, CompressionUtils.gzip(null, null));
		
		assertCompressionWorks(new byte[]{});
		assertCompressionWorks(new byte[]{12,9,100,8,100,2,3,10,2,97});
		
		for ( int i = 0; i < 100; i++ )
		{
			assertCompressionWorks(randomBytes());
		}
	}
	
	public void testStrings()
	{
		assertEquals(null, CompressionUtils.gzipString(null, null));
		
		assertCompressionWorksString("");
		assertCompressionWorksString("Hello World");
		
		assertCompressionWorksString(createAcidString());
		assertCompressionWorksString(createNonBase64AcidString());
	}
	
	static public String createAcidString()
	{
		StringBuilder ret = new StringBuilder();

		ret.append("\n");

		for ( int i = 0; i < 10000; i++ )
		{
			ret.append((char)i);
		}

		return ret.toString();
	}

	static public String createNonBase64AcidString()
	{
		StringBuilder ret = new StringBuilder();

		ret.append("\n");

		for ( int i = 0; i < 10000; i++ )
		{
			char ch = (char)i;
			if ( i > 32 ) { ret.append(ch); }
			if ( ch == '\t' ) { ret.append(ch); }
			if ( ch == '\n' ) { ret.append(ch); }
			if ( ch == '\r' ) { ret.append(ch); }
		}

		return ret.toString();
	}
	
	static private byte[] randomBytes()
	{
		Random r = new Random();
		
		byte ret[] = new byte[r.nextInt(10*1024)];
		r.nextBytes(ret);
		
		return ret;
	}
	
	private void assertCompressionWorks(byte original_data[])
	{
		byte compressed_data[] = CompressionUtils.gzip(original_data, null);
		byte uncompressed_data[] = CompressionUtils.gunzip(compressed_data, null);
		
		assertEquals(original_data.length,uncompressed_data.length);
		
		for ( int i = 0; i < original_data.length; i++ )
		{
			assertEquals(original_data[i], uncompressed_data[i]);
		}
	}
	
	private void assertCompressionWorksString(String oringal_string)
	{
		byte compressed_data[] = CompressionUtils.gzipString(oringal_string, null);
		
		String uncompressed_string = CompressionUtils.gunzipToString(compressed_data, null);
		
		assertEquals(oringal_string, uncompressed_string);
	}
}
