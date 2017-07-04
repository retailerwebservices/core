package org.jimmutable.core.utils;

import java.util.Random;

/**
 * A class containing frequently used utility functions (for testing)
 * 
 * @author jim.kane
 *
 */
public class TestingUtils
{
	/**
	 * Creates the "acid string" -- a.k.a. a string that is good of stress
	 * testing various forms of character encoding. It starts with a newline,
	 * and then contains every character between 0 and 10,000
	 * 
	 * @return A good string for testing character encoding
	 */
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

	/**
	 * Creates a second version of the acid string, one that does not force the
	 * XML serializater to use base 64 encoding
	 * 
	 * @return A good string for testing character encoding
	 */
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
	
	/**
	 * Generate a random array of bytes
	 * 
	 * @param maximum_array_size
	 *            The maximum size array to create (sizes will be randomly
	 *            chosen between 0 and maximum_array_size)
	 * @return A byte array containing random data
	 */
	static public byte[] createRandomByteArray(int maximum_array_size)
	{
		Random r = new Random();
		
		byte ret[] = new byte[r.nextInt(maximum_array_size)];
		r.nextBytes(ret);
		
		return ret;
	}
}
