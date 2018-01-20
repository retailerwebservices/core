package org.jimmutable.cloud.servlets.avatar;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.objects.common.ObjectId;

/**
 * Writes a png, jpeg or gif to the avatars folder from multipart/form-data.
 * Only one file is written from the form field named "file".
 * 
 * @author trevorbox
 *
 */
public class DoUpsertAvatar extends HttpServlet
{

	static private final Set<String> ALLOWED_IMG_EXTENSIONS = new HashSet<>();

	static
	{
		ALLOWED_IMG_EXTENSIONS.add("image/gif");
		ALLOWED_IMG_EXTENSIONS.add("image/jpeg");
		ALLOWED_IMG_EXTENSIONS.add("image/jpg");
		ALLOWED_IMG_EXTENSIONS.add("image/png");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7176239844643933368L;

	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
	static private final Logger logger = LogManager.getLogger(DoUpsertAvatar.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{

		// Required for multipart form data
		if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data"))
		{
			request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		}

		Part part = null;
		try
		{
			part = request.getPart("file");
		} catch (IOException | ServletException e)
		{
			logger.error(e);
		}

		if (part != null)
		{
			String file_name = part.getSubmittedFileName();

			if (file_name != null)
			{
				// StorageKey key = upsertAvatar(getBytesFromPart(part, null), null);

				StorageKey key = null;
				try
				{
					key = upsertAvatar(part.getInputStream(), null);
				} catch (IOException e)
				{
					logger.error(e);
				}

				if (key != null)
				{
					ServletUtil.writeSerializedResponse(response, new UpsertResponseOK(String.format("Upserted %s", file_name), key.getSimpleName()), UpsertResponseOK.HTTP_STATUS_CODE_OK);
					return;
				}
			}
		}

		ServletUtil.writeSerializedResponse(response, new UpsertResponseValidationError(String.format("The file failed to upsert. Ensure it is an allowed extension %s and check the system logs.", ALLOWED_IMG_EXTENSIONS.toString()), "file"), UpsertResponseValidationError.HTTP_STATUS_CODE_ERROR);
		return;

	}

	static public StorageKey upsertAvatar(byte[] image_data, StorageKey default_value)
	{

		if (image_data == null)
		{
			return default_value;
		}

		return upsertAvatar(new ByteArrayInputStream(image_data), default_value);
	}

	static public StorageKey upsertAvatar(InputStream is, StorageKey default_value)
	{

		if (is == null)
		{
			return default_value;
		}

		ByteArrayOutputStream image_data_out = new ByteArrayOutputStream();

		StorageKey key = default_value;

		try
		{
			String type = URLConnection.guessContentTypeFromStream(is);

			if (ALLOWED_IMG_EXTENSIONS.contains(type.toLowerCase()))
			{

				BufferedImage image = ImageIO.read(is);

				if (ImageIO.write(image, DoGetAvatar.EXTENSION.getSimpleValue().toLowerCase(), image_data_out))
				{
					image_data_out.flush();

					StorageKey new_png_key = new ObjectIdStorageKey(DoGetAvatar.KIND, ObjectId.createRandomId(), DoGetAvatar.EXTENSION);
					if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(new_png_key, image_data_out.toByteArray(), false))
					{
						key = new_png_key;
					}

				}
			} else
			{
				logger.error(String.format("Unsupported image extension %s", type));
			}
		} catch (IOException e)
		{
			logger.error(e);
		} finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
				if (image_data_out != null)
				{
					image_data_out.close();
				}
			} catch (IOException e)
			{
				logger.error(e);
			}
		}

		return key;
	}

}
