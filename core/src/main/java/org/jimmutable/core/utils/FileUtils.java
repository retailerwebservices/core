package org.jimmutable.core.utils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class FileUtils 
{
	/**
	 * Get the current user's home directory
	 * 
	 * @return The home directory of the current user
	 */
	static public File getSimpleHomeDirectory()
	{
		return new File(System.getProperty("user.home"));
	}
	
	/**
	 * Get a file in the home directory
	 * 
	 * @param file_name
	 *            The name of the file
	 * @return A File object from the current user's home directory
	 */
	static public File getSimpleFileInHomeDirectory(String file_name)
	{
		return new File(getSimpleHomeDirectory(),file_name);
	}
	
	
	/**
	 * Write a string to a file
	 * 
	 * @param dest
	 *            The file to write to
	 * @param value
	 *            The string to write
	 * @return true if the write worked, false otherwise
	 */
	static public boolean quietWriteFile(File dest, String value)
	{
		if ( value == null ) value = "";
		
		try
		{
			FileWriter w = new FileWriter(dest);
			w.write(value);
			w.close();
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the contents of a file as a string
	 *
	 */
	
	static public String getComplexFileContentsAsString(File src, String default_value)
	{
		try
		{
			byte[] encoded = Files.readAllBytes(src.toPath());
			return new String(encoded);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
}
