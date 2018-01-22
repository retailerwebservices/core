package org.jimmutable.cloud.servlets.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;

/**
 * Returns a file or multiple files in a single zip file based on the
 * storage-key parameter(s).
 * 
 * You simply need a link in html the the full url of this servlet to download
 * in the browser.
 * 
 * Multiple files Example:
 * http://localhost:8080/users/do-get-avatar?storage-key=photo/0000-0000-0000-0000.png&storage-key=photo/0000-0000-0000-0001.png&storage-key=photo/0000-0000-0000-0002.png
 * 
 * @author trevorbox
 *
 */
public class DoGetFile extends HttpServlet
{

	private static final long serialVersionUID = 2891584232341507953L;

	private static Logger logger = LogManager.getLogger(DoGetFile.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		// example:
		// http://localhost:8080/users/do-get-avatar?storage-key=photo/0000-0000-0000-0000.png&storage-key=photo/0000-0000-0000-0001.png&storage-key=photo/0000-0000-0000-0002.png
		String[] values = request.getParameterValues("storage-key") == null ? new String[0] : request.getParameterValues("storage-key");

		if (values.length == 0)
		{
			logger.error("No storage-key parameters");
			return;
		}

		Set<StorageKey> storage_keys = new HashSet<StorageKey>();

		for (String storage_id : values)
		{
			try
			{
				storage_keys.add(new ObjectIdStorageKey(storage_id));
			} catch (Exception e)
			{
				logger.error(String.format("Invalid StorageKey:%s", storage_id));
				return;
			}
		}

		if (storage_keys.size() > 1)
		{

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);

			try
			{
				for (StorageKey storage_key : storage_keys)
				{

					byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);
					if (bytes == null)
					{
						logger.warn(String.format("Nothing returned from storage_key:%s", storage_key.toString()));
						ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Nothing returned from storage_key:%s", storage_key.toString())), HttpServletResponse.SC_BAD_REQUEST);
						return;
					}

					ZipEntry entry = new ZipEntry(storage_key.toString());
					entry.setSize(bytes.length);
					zos.putNextEntry(entry);
					zos.write(bytes);
					zos.closeEntry();

				}

				zos.close();

				response.setContentType("application/zip");
				response.setContentLength(baos.toByteArray().length);
				response.setHeader("Content-disposition", "attachment; filename=" + "files.zip");

				OutputStream out;

				out = response.getOutputStream();
				out.write(baos.toByteArray());

				out.flush();
				baos.close();

			} catch (IOException e)
			{
				logger.error(e);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

		} else
		{

			for (StorageKey storage_key : storage_keys)
			{
				byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);
				if (bytes == null)
				{
					logger.warn(String.format("Nothing returned from storage_key:%s", storage_key.toString()));
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Nothing returned from storage_key:%s", storage_key.toString())), HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				response.setContentType(storage_key.getSimpleExtension().getSimpleMimeType());
				response.setContentLength(bytes.length);

				OutputStream out;
				try
				{
					out = response.getOutputStream();
					out.write(bytes);

					out.flush();
				} catch (IOException e)
				{
					logger.error(e);
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}

				return;

			}

		}

	}

}
