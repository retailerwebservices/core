package org.jimmutable.cloud.servlets.util;

/**
 * An interface that clients need to implement in order to process web page data.
 * 
 * @author Preston McCumber
 */
public interface PageDataHandler
{
	void handle(VisitedPageDataElement page_data);
	void onWarning(String message);
	void onError(String message);
}
