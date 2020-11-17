package org.jimmutable.cloud.servlets.attachments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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

	private static final Logger logger = LoggerFactory.getLogger(DoGetAttachments.class);

	private void addToZipFile(ObjectIdStorageKey storage_key, ZipOutputStream zos) throws IOException
	{

		File attachment_file = File.createTempFile("attachment", "." + storage_key.getSimpleExtension().getSimpleValue());
		FileOutputStream sink = new FileOutputStream(attachment_file);

		logger.info(String.format("Downloading %s to %s", storage_key, attachment_file.getPath()));

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getThreadedCurrentVersionStreaming(storage_key, sink);

		String file_name = storage_key.getSimpleValue();
		try
		{
			AttachmentMetaData meta = (AttachmentMetaData) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(AttachmentMetaData.KIND, storage_key.getSimpleObjectId(), AttachmentMetaData.STORABLE_EXTENSION), null)));
			file_name = meta.getSimpleFileName().getSimpleValue();
		} catch (Exception e)
		{
			logger.error(String.format("No metadata available for %s!", storage_key), e);
		}

		ZipEntry entry = new ZipEntry(file_name);
		entry.setCreationTime(FileTime.fromMillis(attachment_file.lastModified()));
		entry.setSize(attachment_file.length());
		zos.putNextEntry(entry);

		try (FileInputStream inputStream = new FileInputStream(attachment_file))
		{

			byte[] readBuffer = new byte[2048];
			int amountRead;

			while ((amountRead = inputStream.read(readBuffer)) > 0)
			{
				zos.write(readBuffer, 0, amountRead);
			}

			inputStream.close();
		} catch (IOException e)
		{
			logger.error("Failed to add file to zip file!", e);
		}

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		long start = System.currentTimeMillis();
		
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
			File zip_file = null;
			try
			{
				zip_file = File.createTempFile("attachment_zip", ".zip");
			} catch (IOException e)
			{
				logger.error("Failed to create temporary zip file!", e);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			// open the zip stream in a try resource block, no finally needed
			try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zip_file)))
			{
				storage_keys.forEach(key ->
				{
					try
					{
						addToZipFile(key, zipStream);
					} catch (IOException e)
					{
						logger.error("Failed to add file to zip file!", e);
					}
				});

				response.setContentType("application/zip");
				// response.setContentLength(baos.toByteArray().length);
				response.setHeader("Content-disposition", "attachment; filename=" + "attachments.zip");

				zipStream.close();

				IOUtils.copy(new FileInputStream(zip_file), response.getOutputStream());
				
				logger.info(String.format("Took %d millis", System.currentTimeMillis() - start));

			} catch (IOException e)
			{
				logger.error("Failed to create zip file!", e);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

		} else
		{

			for (ObjectIdStorageKey storage_key : storage_keys)
			{

				response.setContentType(storage_key.getSimpleExtension().getSimpleMimeType());
				// response.setContentLength(bytes.length);

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

				OutputStream out = null;
				try
				{
					out = response.getOutputStream();

					CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getThreadedCurrentVersionStreaming(storage_key, out);

					out.flush();
				} catch (IOException e)
				{
					logger.error("Error streaming", e);
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				} finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						} catch (IOException e)
						{
							logger.error("Failed to close output stream when writing atachment!", e);
						}
					}
				}

				logger.info(String.format("Took %d millis", System.currentTimeMillis() - start));
				return;

			}

		}
	}
}
