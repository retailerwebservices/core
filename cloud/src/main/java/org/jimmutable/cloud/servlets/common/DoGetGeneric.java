package org.jimmutable.cloud.servlets.common;

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
 * This Class is used to get StandardImmutableObjects or specific data related
 * to them.
 * 
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		ObjectId id = null;
		try
		{
			id = new ObjectId(request.getParameter(getId()));
		} catch (Exception e)
		{
			getLogger().error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Invalid ObjectId from parameter:%s", getId())), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		StorageKey key = null;
		try
		{
			key = new ObjectIdStorageKey(getKind(), id, getExtension());
		} catch (Exception e)
		{
			getLogger().error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Failed to create valid StorageKey from parameter:%s", getId())), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null);

		if (bytes == null)
		{
			String error = String.format("%s not found in storage", key.getSimpleName());
			getLogger().error(error);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error), GetResponseError.HTTP_STATUS_CODE_ERROR);

			return;
		}

		T object = null;
		try
		{
			object = (T) StandardObject.deserialize(new String(bytes));
		} catch (Exception e)
		{
			String error = String.format("Failed to serialize %s from storage", key.getSimpleName());
			getLogger().error(error);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error), GetResponseError.HTTP_STATUS_CODE_ERROR);

			return;
		}

		Object more_specific_data = null;
		try
		{
			more_specific_data = getMoreSpecificData(object, request, null);
		} catch (Exception e)
		{
			getLogger().error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(e.toString()), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		if (more_specific_data != null)
		{
			ServletUtil.writeSerializedResponse(response, more_specific_data, GetResponseOK.HTTP_STATUS_CODE_OK);
			return;
		} else
		{
			String error = String.format("Failed to create object please contact an admin");
			getLogger().error(error);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

	}

	/**
	 * Used to decorate an existing object into a new one
	 * 
	 * @param obj
	 *            T an Object
	 * @param request
	 *            HttpServletRequest
	 * @param default_value
	 *            Object returned if failure
	 * @return the new Object
	 */
	protected Object getMoreSpecificData(T obj, HttpServletRequest request, Object default_value)
	{
		if (obj == null)
		{
			return default_value;
		}
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
