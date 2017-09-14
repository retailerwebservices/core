package org.jimmutable.cloud.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.core.utils.FileUtils;
import org.jimmutable.core.utils.Validator;

/**
 * A utility class for reading properties files in the home directory
 * 
 * @author jim.kane
 *
 */
public class PropertiesReader 
{
	private File src;
	
	private Properties properties = new Properties();
	
	/**
	 * Construct a property reader for a specified file (in the home directory)
	 * 
	 * @param file_name_in_home_directory The name of the file to read (e.g. panda.properties)
	 */
	public PropertiesReader(String file_name_in_home_directory)
	{
		Validator.notNull(file_name_in_home_directory);
		
		src = new File(FileUtils.getSimpleHomeDirectory(), file_name_in_home_directory);
		
		try
		{
			if ( src.exists() ) 
				properties.load(new FileReader(src));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a string from the properties file
	 * 
	 * @param property_name The name of the property (properties are normalized to lower case)
	 * @param default_value The value to return if the property can not be read or is the empty string
	 * @return The value of the property specified
	 */
	public String readString(String property_name, String default_value)
	{ 
		if ( property_name == null ) return default_value;
		
		property_name = normalizePropertyName(property_name);
		
		String ret = properties.getProperty(property_name);
		if ( ret != null ) ret = ret.trim();
		if ( ret == null || ret.length() == 0 ) return default_value;
		
		return ret;
	}
	
	/**
	 * Read a boolean property 
	 * @param property_name The name of the property
	 * @param default_value The value to return if the property can not be read
	 * @return The value of the property
	 */
	public boolean readBoolean(String property_name, boolean default_value)
	{
		String str = readString(property_name, null);
		if ( str == null ) return default_value;
		
		if ( str.equalsIgnoreCase("true") ) return true;
		if ( str.equalsIgnoreCase("t") ) return true;
		if ( str.equalsIgnoreCase("yes") ) return true;
		if ( str.equalsIgnoreCase("y") ) return true;
		
		if ( str.equalsIgnoreCase("false") ) return false;
		if ( str.equalsIgnoreCase("f") ) return false;
		if ( str.equalsIgnoreCase("no") ) return false;
		if ( str.equalsIgnoreCase("n") ) return false;
		
		return default_value;
	}
	
	/**
	 * Read an integer property 
	 * @param property_name he name of the property
	 * @param default_value The value to return if the property can not be read
	 * @return The value of the property
	 */
	public int readInt(String property_name, int default_value)
	{
		String str = readString(property_name, null);
		if ( str == null ) return default_value;
		
		try
		{
			return Integer.parseInt(str);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Error parsing property: "+property_name, e);
			return default_value;
		}
	}
	
	/**
	 * Read an long property 
	 * @param property_name he name of the property
	 * @param default_value The value to return if the property can not be read
	 * @return The value of the property
	 */
	
	public long readLong(String property_name, long default_value)
	{
		String str = readString(property_name, null);
		if ( str == null ) return default_value;
		
		try
		{
			return Long.parseLong(str);
		}
		catch(Exception e)
		{
			LogManager.getRootLogger().error("Error parsing property: "+property_name, e);
			e.printStackTrace();
			return default_value;
		}
	}
	
	private String normalizePropertyName(String src)
	{
		if ( src == null ) return src;
		return src.trim().toLowerCase();
	}
}
