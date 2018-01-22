package org.jimmutable.cloud.servlets.util;

import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class PageDataElement
{

	protected String element_name = null;  // Required
	private byte[] element_bytes = null;
	private String element_json = null;
	private String filename = null;
	private long file_size = 0;

	public PageDataElement()
	{
		// Empty constructor
	};

	public PageDataElement( String name, String json_data )
	{
		Validator.notNull(name);
		this.element_name = name;
		this.element_json = json_data;
	}

	public PageDataElement( String name, String json_data, byte[] bytes, String filename )
	{
		Validator.notNull(name);
		this.element_name = name;
		this.element_json = json_data;
		this.element_bytes = bytes;
		this.filename = filename;
		this.file_size = bytes.length;
	}

	public String getElementName()
	{
		return element_name;
	};

	public boolean hasJSONData()
	{
		return element_json != null;
	}
	
	public String getOptionalJSONData( String default_value )
	{
		return Optional.getOptional(element_json, null, default_value);
	}

	public boolean hasFileData()
	{
		return element_bytes != null;
	}

	public String getOptionalFilename( String default_value )
	{
		return Optional.getOptional(filename, null, default_value);
	}

	public byte[] getOptionalFileData( byte[] default_value )
	{
		if ( hasFileData() )
		{
			return element_bytes;
		}
		else
		{
			return default_value;
		}
	}

	public long getOptionalFileSize( long default_value )
	{
		return file_size > 0 ? file_size : default_value;
	}
}
