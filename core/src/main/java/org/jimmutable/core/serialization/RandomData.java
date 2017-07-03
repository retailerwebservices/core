package org.jimmutable.core.serialization;

import java.util.Random;

/**
 * This class contains a (hopefully) useful set of a utilities used to generate
 * random data.
 * 
 * The generation of random data frequently comes up as one works with
 * serialization, particular testing and benchmarking.
 * 
 * @author jim.kane
 *
 */
public class RandomData
{
	static public final char ALPHABET_ALPHA_NUMERIC[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	static public final char ALPHABET_ALPHA_NUMERIC_UPPER_CASE[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	static public final char ALPHABET_DIGITS[] = "0123456789".toCharArray();
	static public final char ALPHABET_ALPHA[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	static public final char ALPHABET_ALPHA_UPPER_CASE[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	private Random r = new Random();
	
	/**
	 * Create a new random data object.
	 */
	public RandomData()
	{
		
	}
	
	/**
	 * Generate a random string of a specified length containing characters
	 * chosen from a provided alphabet.
	 * 
	 * @param alphabet
	 *            The characters available to choose from. Empty or null
	 *            alphabets will result in the empty string being returned.
	 * 
	 * @param length
	 *            The length of the string to generate. Negative lenths will
	 *            return the empty string
	 * 
	 * @return A random string of the length specified, containing only
	 *         characters from alphabet.
	 */
	public String randomStringOfLength(char alphabet[], int length)
	{
		if ( alphabet == null || alphabet.length == 0 ) return "";
		
		StringBuilder ret = new StringBuilder();
		
		while(length > 0)
		{
			ret.append(alphabet[r.nextInt(alphabet.length)]);
			length--;
		}
		
		return ret.toString();
	}
	
	/**
	 * Get a random integer greater than or equal to min, but less than or equal
	 * to max
	 * 
	 * @param min
	 *            The minimum integer that may possibly be returned
	 * @param max
	 *            The maximum integer that may possibly be returned
	 * @return A random integer between min and max inclusive
	 */
	public int randomInt(int min, int max)
	{
		if ( max < min )
			return randomInt(max,min);
		
		return min + r.nextInt(max-min);
	}
	
	
	/**
	 * Generate a random string of a length in the specified length range
	 * containing characters chosen from a provided alphabet.
	 * 
	 * @param alphabet
	 *            The characters available to choose from. Empty or null
	 *            alphabets will result in the empty string being returned.
	 * 
	 * @param min_length
	 *            The minimum possible length of the string
	 * 
	 * @param max_length
	 *            The maximum possible length of the string
	 * 
	 * @return A random string of the length specified, containing only
	 *         characters from alphabet.
	 */
	public String randomStringOfLength(char alphabet[], int min_length, int max_length)
	{
		return randomStringOfLength(alphabet, randomInt(min_length,max_length));
	}
	
}
