package org.jimmutable.cloud.servlets.common;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

/**
 * This class is designed to get byte information from our storage
 *<br>
 *I.E. FbSimpleImageAd
 * <b> DO NOT GET STANDARDIMMUTABLE OBJECTS WITH THIS</b>
 * @author andrew.towe
 *
 */
public abstract class DoGetGenericBytes extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8063078405270553825L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		String id = request.getParameter(getId());

		if (id == null)
		{
			idNotFound(request, response);
			return;
		}

		try
		{
			StorageKey storage_key = new ObjectIdStorageKey(getKind(), new ObjectId(id), getExtension());
			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);
			response.setContentType(storage_key.getSimpleExtension().getSimpleMimeType());

			if (bytes == null)
			{
				bytesNotFound(request, response);
				return;
			}

			OutputStream out = null;
			try
			{
				out = response.getOutputStream();
				out.write(bytes);
				out.flush();
			} catch (IOException e)
			{
				getLogger().error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			} finally
			{
				try
				{
					if (out != null)
					{
						out.close();
					}
				} catch (IOException e)
				{
					getLogger().error(e);
				}
			}

			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e)
		{
			getLogger().error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(e.getMessage()), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

	}
	
	protected String getId()
	{
		return "id";
	}

	abstract protected Logger getLogger();

	abstract protected Kind getKind();
	
	abstract protected StorageKeyExtension getExtension();
	
	protected void idNotFound(HttpServletRequest request,HttpServletResponse response) {
		ServletUtil.writeSerializedResponse(response, new GetResponseError("Missing required parameter id"), GetResponseError.HTTP_STATUS_CODE_ERROR);
	}
	
	protected void bytesNotFound( HttpServletRequest request, HttpServletResponse response )
	{
		ServletUtil.writeSerializedResponse(response, new GetResponseError("Nothing returned from storage"), GetResponseError.HTTP_STATUS_CODE_ERROR);	
	}
}
