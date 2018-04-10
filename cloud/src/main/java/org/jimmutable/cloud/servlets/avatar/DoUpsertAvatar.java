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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.cloud.servlets.util.PageDataHandler;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.servlets.util.VisitedPageDataElement;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Optional;

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

	static private final Logger logger = LogManager.getLogger(DoUpsertAvatar.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{

		ServletUtil.handlePageDataFromPost(request, new PageDataHandler()
		{
			@Override
			public void handle(VisitedPageDataElement element)
			{

				if (element.hasInputStream())
				{
					StorageKey key = upsertAvatar(element.getOptionalInputStream(null), null);

					if (key != null)
					{
						ServletUtil.writeSerializedResponse(response, new UpsertResponseOK(String.format("Upserted %s", element.getOptionalFilename("?")), key.getSimpleName()), UpsertResponseOK.HTTP_STATUS_CODE_OK);
						return;
					}
				}
				ServletUtil.writeSerializedResponse(response, new UpsertResponseValidationError(String.format("The file failed to upsert. Ensure it is an allowed extension %s and check the system logs.", ALLOWED_IMG_EXTENSIONS.toString()), "file"), UpsertResponseValidationError.HTTP_STATUS_CODE_ERROR);
				return;

			}

			@Override
			public void onWarning(String message)
			{
				logger.warn(message);
			}

			@Override
			public void onError(String message)
			{
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(message), GeneralResponseError.HTTP_STATUS_CODE_ERROR);
			}
		});

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
			String type = "image/jpg";
			try
			{
				type = Optional.getOptional(URLConnection.guessContentTypeFromStream(is), null, type);
			} catch (IOException e)
			{
				logger.error(e);
			}

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
