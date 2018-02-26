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

public abstract class DoGetGenericBytes extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8063078405270553825L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		String id = request.getParameter("id");

		if (id == null)
		{
			ServletUtil.writeSerializedResponse(response, new GetResponseError("Missing required parameter id"), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		try
		{
			StorageKey storage_key = new ObjectIdStorageKey(getKind(), new ObjectId(id), getExtension());
			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);
			response.setContentType(storage_key.getSimpleExtension().getSimpleMimeType());

			if (bytes == null)
			{
				ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Nothing returned from storage for %s", storage_key.toString())), GetResponseError.HTTP_STATUS_CODE_ERROR);
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
	abstract protected Logger getLogger();

	abstract protected Kind getKind();
	
	abstract protected StorageKeyExtension getExtension();
}
