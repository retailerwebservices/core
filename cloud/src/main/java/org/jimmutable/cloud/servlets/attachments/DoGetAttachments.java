package org.jimmutable.cloud.servlets.attachments;

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
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.core.objects.StandardObject;

public class DoGetAttachments extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3700301389300549786L;

	private static Logger logger = LogManager.getLogger(DoGetAttachments.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String[] values = request.getParameterValues("storage-key") == null ? new String[0] : request.getParameterValues("storage-key");

		if (values.length == 0)
		{
			logger.error("No storage-key parameters");
			return;
		}

		Set<ObjectIdStorageKey> storage_keys = new HashSet<ObjectIdStorageKey>();

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
				for (ObjectIdStorageKey storage_key : storage_keys)
				{

					byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);

					if (bytes == null)
					{
						logger.warn(String.format("Nothing returned from storage_key:%s", storage_key.getSimpleValue()));
						ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Nothing returned from storage_key:%s", storage_key.getSimpleValue())), HttpServletResponse.SC_BAD_REQUEST);
						return;
					}

					String file_name = storage_key.getSimpleValue();
					try
					{
						AttachmentMetaData meta = (AttachmentMetaData) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(AttachmentMetaData.KIND, storage_key.getSimpleObjectId(), AttachmentMetaData.STORABLE_EXTENSION), null)));
						file_name = meta.getSimpleFileName().getSimpleValue();

					} catch (Exception e)
					{
						logger.error("No Metadata available", e);
					}

					ZipEntry entry = new ZipEntry(file_name);
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

			for (ObjectIdStorageKey storage_key : storage_keys)
			{
				byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(storage_key, null);
				if (bytes == null)
				{
					logger.warn(String.format("Nothing returned from storage_key:%s", storage_key.getSimpleValue()));
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Nothing returned from storage_key:%s", storage_key.getSimpleValue())), HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				response.setContentType(storage_key.getSimpleExtension().getSimpleMimeType());
				response.setContentLength(bytes.length);

				String file_name = storage_key.getSimpleValue();
				try
				{
					AttachmentMetaData meta = (AttachmentMetaData) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(AttachmentMetaData.KIND, storage_key.getSimpleObjectId(), AttachmentMetaData.STORABLE_EXTENSION), null)));
					file_name = meta.getSimpleFileName().getSimpleValue();

				} catch (Exception e)
				{
					logger.warn(String.format("No metadata available for attachment, using %s as the filename", file_name));
				}

				response.setHeader("Content-Disposition", "attachment; filename=" + file_name);

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
