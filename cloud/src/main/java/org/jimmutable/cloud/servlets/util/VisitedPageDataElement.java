package org.jimmutable.cloud.servlets.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class VisitedPageDataElement
{

	protected String element_name = null;  // Required
	private String element_json = null; // Optional
	private InputStream element_data = null; // Optional
	private String filename = null; // Optional

	public VisitedPageDataElement( String name)
	{
		Validator.notNull(name);
		this.element_name = name;
	};

	public VisitedPageDataElement( String name, String json_data )
	{
		Validator.notNull(name);
		this.element_name = name;
		this.element_json = json_data;
	}

	public VisitedPageDataElement( String name, String json_data, InputStream is, String filename )
	{
		Validator.notNull(name);
		this.element_name = name;
		this.element_json = json_data;
		this.element_data = is;
		this.filename = filename;
	}
	
	public byte[] getAllElementDataBytesSlow() {
		try
		{
			return IOUtils.toByteArray(element_data);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		return null;
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

	public boolean hasInputStream()
	{
		return element_data != null;
	}
	
	public InputStream getOptionalInputStream( InputStream default_value)
	{
		return Optional.getOptional(element_data, null, default_value);
	}
	
	public String getOptionalFilename( String default_value )
	{
		return Optional.getOptional(filename, null, default_value);
	}

}
