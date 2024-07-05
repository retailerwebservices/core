package org.jimmutable.cloud.servlets.common;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.io.EofException;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.slf4j.Logger;

/**
 * This class is designed to get byte information from our storage <br>
 * I.E. FbSimpleImageAd <b> DO NOT GET STANDARDIMMUTABLE OBJECTS WITH THIS</b>
 * 
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
		String extension_str = request.getParameter(getExtensionParamName());
		StorageKeyExtension extension = getExtension();

		if (id == null || id.equals("") || id.equals("undefined"))
		{
			idNotFound(request, response);
			return;
		}

		if (extension_str != null && !extension_str.isEmpty() && !extension_str.equals("undefined"))
		{
			extension = new StorageKeyExtension(extension_str);
		}

		try
		{
			StorageKey storage_key = new ObjectIdStorageKey(getKind(), new ObjectId(id), extension);

			OutputStream out = null;
			try
			{
				response = setHeader(request, response);// to set filename, content type, etc.
				out = response.getOutputStream();
				boolean data_was_retrieved = false;

				if (isLarge())
				{
					data_was_retrieved = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getThreadedCurrentVersionStreaming(storage_key, out);
				} else
				{
					data_was_retrieved = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersionStreaming(storage_key, out);
				}

				out.flush();

				if (!data_was_retrieved) {
					bytesNotFound(request, response);
				}
			} catch (EofException e)
			{
				// making this quiet since it causes mass fear and panic among devs
				getLogger().trace("This is thrown by Jetty to distinguish between EOF received from the connection, vs and EOF thrown by some application talking to some other file/socket etc. The only difference in handling is that Jetty EOFs are logged less verbosely.", e);
				// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (IOException e)
			{
				getLogger().error("Unexpected IO Exception when retreiving bytes!", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
					getLogger().error("Unexpected IO Exception when closing output stream!", e);
				}
			}

			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e)
		{
			getLogger().error("Unexpected exception thrown while retreiving bytes!", e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(e.getMessage()), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

	}

	/**
	 * If a file is expected to large, getThreadedCurrentVersionStreaming will be
	 * called instead of getCurrentVersionStreaming
	 * 
	 * @return if the file is expected to be large or not
	 */
	abstract protected boolean isLarge();

	protected String getId()
	{
		return "id";
	}

	protected String getExtensionParamName()
	{
		return "extension";
	}

	/**
	 * Allows you to set the header to control the content type, length, filename in
	 * the response header.
	 * 
	 * response.setContentType("video/mp4"); String fileName="myVideo.mp4";
	 * response.setHeader("Content-Disposition", "inline; filename="+ fileName
	 * +";");
	 * 
	 * @param response
	 * @return
	 */
	abstract protected HttpServletResponse setHeader(HttpServletRequest request, HttpServletResponse response);

	abstract protected Logger getLogger();

	abstract protected Kind getKind();

	abstract protected StorageKeyExtension getExtension();

	protected void idNotFound(HttpServletRequest request, HttpServletResponse response)
	{
		ServletUtil.writeSerializedResponse(response, new GetResponseError("Missing required parameter id"), GetResponseError.HTTP_STATUS_CODE_ERROR);
	}

	protected void bytesNotFound(HttpServletRequest request, HttpServletResponse response)
	{
		ServletUtil.writeSerializedResponse(response, new GetResponseError("Nothing returned from storage"), GetResponseError.HTTP_STATUS_CODE_ERROR);
	}
}
