package org.jimmutable.cloud.servlets.common;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

/**
 * This Class is used to get StandardImmutableObjects or specific data related to them. 
 * @author andrew.towe
 *
 * @param <T>
 */
public abstract class DoGetGeneric<T extends Storable> extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8993193171889935131L;

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
	{
		try
		{
			String id = request.getParameter(getId()) == null ? "" : request.getParameter(getId());
			StorageKey key = new ObjectIdStorageKey(getKind(), new ObjectId(id), getExtension());
			T object = (T) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null)));

			Object more_specific_data = null;
			try
			{
				more_specific_data = getMoreSpeciificData(object, request);
			}
			catch ( Exception e )
			{
				handleError(response,e);
			}
			if ( more_specific_data != null )
			{
				ServletUtil.writeSerializedResponse(response, more_specific_data, GetResponseOK.HTTP_STATUS_CODE_OK);
			}
			else
			{
				objectNotFoundFunction(response);
			}
		}
		catch ( Exception e )
		{
			handleError(response, e);
			getLogger().error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(e.getMessage()), GetResponseError.HTTP_STATUS_CODE_ERROR);
		}
	}

	protected void objectNotFoundFunction(HttpServletResponse response)
	{
		try
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			getLogger().error(e);
		}
	}

	protected void handleError( HttpServletResponse response, Exception e )
	{
		getLogger().error(e);
		ServletUtil.writeSerializedResponse(response, new GetResponseError(e.toString()), GetResponseError.HTTP_STATUS_CODE_ERROR);
		
	}

	protected Object getMoreSpeciificData( T obj, HttpServletRequest request )
	{
		// to be overriddden if more specific data is needed.
		return obj;
	}

	protected StorageKeyExtension getExtension()
	{
		// to be overriddden if more specific data is needed.
		return Storable.STORABLE_EXTENSION;
	}

	protected String getId()
	{
		// to be overriddden if more specific data is needed.
		return "id";
	}

	abstract protected Logger getLogger();

	abstract protected Kind getKind();
}
