package org.jimmutable.cloud.servlets.common;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.cloud.storage.StorageUtils;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public abstract class DoDeleteStorageKey extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8209943369568153719L;

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response )
	{
		StorageKey key = null;
		try
		{

			ObjectId id = new ObjectId(request.getParameter("id"));
			key = new ObjectIdStorageKey(getKind(), id, getStorageKeyExtension());

			if ( StorageUtils.doesExist(key, false) )
			{

				if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(key) )
				{
					GeneralResponseOK ok = new GeneralResponseOK(String.format("Deleted key: %s", key.toString()));
					ServletUtil.writeSerializedResponse(response, ok, GeneralResponseOK.HTTP_STATUS_CODE_OK);
				}
				else
				{
					GeneralResponseError error = new GeneralResponseError(String.format("Failed to delete key %s from storage", key.toString()));
					ServletUtil.writeSerializedResponse(response, error, GeneralResponseError.HTTP_STATUS_CODE_ERROR);
				}
			}
			else
			{
				GeneralResponseOK ok = new GeneralResponseOK(String.format("The key %s is already deleted", key.toString()));
				ServletUtil.writeSerializedResponse(response, ok, GeneralResponseOK.HTTP_STATUS_CODE_OK);
			}

		}
		catch ( Exception e )
		{
			GeneralResponseError error = new GeneralResponseError(e.getMessage());
			ServletUtil.writeSerializedResponse(response, error, GeneralResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

	public abstract Kind getKind();

	public abstract StorageKeyExtension getStorageKeyExtension();

}
