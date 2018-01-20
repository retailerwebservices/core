package org.jimmutable.cloud.servlets.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestPageData
{
	private static final Logger logger = LogManager.getLogger(RequestPageData.class);
	
	public static String DEFAULT_JSON_ELEMENT = "json";
	public static String DEFAULT_FILE_ELEMENT = "file";
	
	private List<PageDataElement> page_elements = new ArrayList<>();
	
	/**
	 * Add a PageDataElement to this object
	 * 
	 * @param element
	 * 		PageDataElement object. Element name must be unique or add operation will fail.
	 */
	public void addElement(PageDataElement element)
	{
		PageDataElement existing_element = getOptionalElementByName(element.getElementName(), null);
		if (existing_element != null) {
			logger.warn("Duplicate element name " + element.getElementName() + " ! PageDataElement add operation failed.");
			return;
		}
		page_elements.add(element);
	}
	
	public PageDataElement getOptionalElementByName(String name, PageDataElement default_value)
	{
		for ( PageDataElement element : page_elements ) {
			if (element.element_name.equals(name))
			{
				return element;
			}
		}
		
		return default_value;
	}
	
	public String getOptionalDefaultJSONData(String default_value)
	{
		PageDataElement element = getOptionalElementByName(DEFAULT_JSON_ELEMENT, null);
		
		return element != null ? element.getOptionalJSONData("") : default_value;
	}
	
	/**
	 * Return all page elements
	 * 
	 * @return 	Collection of page_elements or an empty collection if no elements are present
	 */
	public Collection<PageDataElement> getAllElements()
	{
		return page_elements;
	}
	
	public boolean isEmpty()
	{
		return page_elements.isEmpty();
	}
}
