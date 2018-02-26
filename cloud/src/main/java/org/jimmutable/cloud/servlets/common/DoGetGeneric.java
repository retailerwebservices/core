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

public abstract class DoGetGeneric<T extends Storable> extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8993193171889935131L;

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) {
		try
		{
			StorageKey key = new ObjectIdStorageKey(getKind(), new ObjectId(request.getParameter("id")),  getExtension());
			T object= (T) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null)));

			ServletUtil.writeSerializedResponse(response, getMoreSpeciificData(object), GetResponseOK.HTTP_STATUS_CODE_OK);
		} catch (Exception e)
		{
			getLogger().error("Invalid id", e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError("Invalid storage key"), GetResponseError.HTTP_STATUS_CODE_ERROR);
		}
	}
	
	protected Object getMoreSpeciificData(T obj) {
		//to be overriddden if more specific data is needed. 
		return obj;
	}
	
	protected StorageKeyExtension getExtension() {
		//to be overriddden if more specific data is needed. 
		return Storable.STORABLE_EXTENSION;
	}
	abstract protected Logger getLogger();
	
	abstract protected Kind getKind();
}
