package org.jimmutable.cloud.servlets.util;

public class DefaultPageDataHandler implements PageDataHandler
{

	@Override
	public void handle( VisitedPageDataElement page_data )
	{
		// no-op
	}

	@Override
	public void onWarning( String message )
	{
		System.err.println(message);

	}

	@Override
	public void onError( String message )
	{
		System.err.println(message);

	}

}
