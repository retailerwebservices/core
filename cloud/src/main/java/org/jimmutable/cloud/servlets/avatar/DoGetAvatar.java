package org.jimmutable.cloud.servlets.avatar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlets.common.DoGetGenericBytes;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

/**
 * Gets the avatar if available, else gets the default image
 * 
 * @author trevorbox
 *
 */
public class DoGetAvatar extends DoGetGenericBytes
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2620487493656375394L;
	public static final Kind KIND = new Kind("avatars");
	public static final StorageKeyExtension EXTENSION = StorageKeyExtension.PNG;
	static private final Logger logger = LogManager.getLogger(DoGetAvatar.class);

	@Override
	protected Logger getLogger()
	{
		return logger;
	}

	@Override
	protected Kind getKind()
	{
		return KIND;
	}

	@Override
	protected StorageKeyExtension getExtension()
	{
		return EXTENSION;
	}

	@Override
	protected void idNotFound( HttpServletRequest request, HttpServletResponse response )
	{
		getDefaultImage(request, response);
	}
	
	@Override
	protected void bytesNotFound( HttpServletRequest request, HttpServletResponse response )
	{
		getDefaultImage(request, response);
	}
	
	private void getDefaultImage(HttpServletRequest request, HttpServletResponse response) {
		String default_image = request.getParameter("default-image");
		if ( default_image == null ||default_image.equals(""))
		{
			logger.error("Missing required parameter default_image");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		OutputStream os = null;

		InputStream is = null;

		try
		{
			os = response.getOutputStream();

			URL url = new URL(default_image);

			is = url.openStream();

			String mime = URLConnection.guessContentTypeFromStream(is);

			response.setContentType(mime);

			byte[] buffer = new byte[4096];
			int n;

			while ( (n = is.read(buffer)) > 0 )
			{
				os.write(buffer, 0, n);
			}

			os.flush();

		}
		catch ( Exception e )
		{
			logger.error(e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		finally
		{

			try
			{
				if ( is != null )
				{
					is.close();
				}
				if ( os != null )
				{
					os.close();
				}
			}
			catch ( IOException e )
			{
				logger.error(e);
			}

		}
		return;

	}

}
