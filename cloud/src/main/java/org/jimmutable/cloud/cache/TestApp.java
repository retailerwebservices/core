package org.jimmutable.cloud.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NOTES: xcode-select --install
 * 
 * Download latest version of redis
 * Then run make test
 * Then run make
 * Inside of the src folder, there is a executable, redis-server
 * 
 * @author kanej
 *
 */

public class TestApp
{
	static public void main(String args[])
	{
		List<SoftReference<byte[]>> my_list = new ArrayList();
		
		for ( int i = 0; i < 1024; i++ )
		{
			byte data[] = createRandomBytes(1024*1024);
			
			my_list.add(new SoftReference(data)); // add 1mb of random data...
			
			printStats(my_list);
		}
	}
	
	static private void printStats(List<SoftReference<byte[]>> my_list)
	{
		int valid = 0;
		long bytes_in_memory = 0;
		
		for ( SoftReference<byte[]> ref : my_list )
		{
			byte arr[] = ref.get();
			
			if ( arr == null ) continue;
			
			valid ++;
			
			bytes_in_memory += arr.length;
		}
		
		float valid_pct = (float)valid / (float)my_list.size();
		valid_pct *= 100;
		 
		long kb_in_memory = bytes_in_memory / 1024;
		
		float mb_in_memory = (float)kb_in_memory / 1024.0f;
		
		System.out.println(String.format("Valid: %.1f%%, MB in RAM: %.1f mb", valid_pct, mb_in_memory));
	}
	
	static private byte[] createRandomBytes(int size)
	{
		byte[] ret = new byte[size];
		
		Random r = new Random(); 
		
		r.nextBytes(ret);
			
		return ret;
	}
}
