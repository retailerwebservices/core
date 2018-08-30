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
			String error_message = String.format("Invalid ObjectId from parameter:%s", getId());
			getLogger().error(error_message, e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error_message), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		StorageKey key = null;
		try
		{
			key = new ObjectIdStorageKey(getKind(), id, getExtension());
		} catch (Exception e)
		{
			String error_message = String.format("Failed to create valid StorageKey from Kind:%s Parameter:%s Extension:%s", getKind().getSimpleValue(), getId(), getExtension());
			getLogger().error(error_message, e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error_message), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null);

		if (bytes == null)
		{
			String error_message = String.format("%s/%s.%s not found in storage!", key.getSimpleKind().getSimpleValue(), key.getSimpleName().getSimpleValue(), key.getSimpleExtension().getSimpleValue());
			getLogger().error(error_message);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error_message), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		Object more_specific_data = null;
		try
		{
			more_specific_data = getMoreSpecificData((T) StandardObject.deserialize(new String(bytes)), request, null);
		} catch (Exception e)
		{
			String error_message = String.format("Failed to deserialize %s/%s.%s from storage!", key.getSimpleKind().getSimpleValue(), key.getSimpleName().getSimpleValue(), key.getSimpleExtension().getSimpleValue());
			getLogger().error(error_message, e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error_message), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		if (more_specific_data != null)
		{
			ServletUtil.writeSerializedResponse(response, more_specific_data, GetResponseOK.HTTP_STATUS_CODE_OK);
			return;
		} else
		{
			String error_message = String.format("Failed to create object please contact an admin");
			getLogger().error(error_message);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(error_message), GetResponseError.HTTP_STATUS_CODE_ERROR);
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
