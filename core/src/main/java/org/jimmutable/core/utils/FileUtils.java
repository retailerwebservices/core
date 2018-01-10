package org.jimmutable.core.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;

public class FileUtils 
{
	private static final Logger logger = Logger.getLogger(FileUtils.class.getName()); 
	
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
	 * Write a string to a file
	 * 
	 * @param dest
	 *            The file to write to
	 * @param value
	 *            The string to write
	 */
	static public void writeFile(File dest, String value) throws IOException
	{
		Validator.notNull(dest, value);

		try (FileWriter w = new FileWriter(dest))
		{
	        w.write(value);
		}
	}
	
	/**
	 * Serialize and object to a file in the specified format
	 * 
	 * @param dest
	 *            The file to write
	 * @param object
	 *            The object to write
	 * @param format
	 *            The format to serialize in
	 * @throws IOException
	 */
	static public void writeFile(File dest, StandardObject object, Format format) throws IOException
	{
		Validator.notNull(dest, object, format);
		
		writeFile(dest, object.serialize(format));
	}
	
	/**
	 * Quiet (no chance of throwing an exception) write of an object to a file
	 * 
	 * @param dest
	 *            The file to write
	 * @param object
	 *            The object to write
	 * @param format
	 *            The format to write the object in
	 * @return true if the object was written successfully, false otherwise
	 */
	static public boolean quietWriteFile(File dest, StandardObject object, Format format)
	{
		try
		{
			writeFile(dest,object,format);
			return true;
		}
		catch(Exception e)
		{
			logger.log(Level.WARNING, "error writing file", e);
			return false;
		}
	}
	
	/**
	 * Attempts to read a StandardObject from a file
	 * 
	 * @param src
	 *            The file to read from
	 * @param default_value
	 *            The value to return if the read fails (for any reason)
	 * @return The deserialzied object from the file, or default_value if the object
	 *         could not be read
	 */
	static public StandardObject readObjectFromFile(File src, StandardObject default_value)
	{
		try
		{
			String str = getComplexFileContentsAsString(src, null);
			if ( str == null ) return default_value;
			
			return StandardObject.deserialize(str);
		}
		catch(Exception e)
		{
			logger.log(Level.WARNING, "error reading object from file", e);
			return default_value;
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
