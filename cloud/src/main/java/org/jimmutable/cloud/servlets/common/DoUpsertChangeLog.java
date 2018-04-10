package org.jimmutable.cloud.servlets.common;

import java.util.Collection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.attachments.DownloadFileName;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlets.util.PageDataElement;
import org.jimmutable.cloud.servlets.util.RequestPageData;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.HandReader;

public class DoUpsertChangeLog extends HttpServlet
{

	static public final Kind KIND = new Kind("attachment");

	/**
	 * 
	 */
	private static final long serialVersionUID = -8771597079214512255L;
	static private final Logger logger = LogManager.getLogger(DoUpsertChangeLog.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			RequestPageData page_data = ServletUtil.getPageDataFromPost(request, new RequestPageData());

			if (page_data.isEmpty())
			{
				logger.error("Request contains no data");
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Request failed"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			HandReader r = new HandReader(page_data.getOptionalDefaultJSONData(""));

			logger.info(page_data.getOptionalDefaultJSONData(""));

			ObjectId id = new ObjectId(r.readString(StandardChangeLogEntry.FIELD_ID.getSimpleFieldName().getSimpleName(), null));

			StandardChangeLogEntry entry = (StandardChangeLogEntry) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(StandardChangeLogEntry.KIND, id, StandardChangeLogEntry.STORABLE_EXTENSION), null)));

			// logger.info(entry.toJavaCode(Format.JSON_PRETTY_PRINT, "entry"));

			Builder b = new Builder(StandardChangeLogEntry.TYPE_NAME);

			b.set(StandardChangeLogEntry.FIELD_ID, entry.getSimpleObjectId());
			b.set(StandardChangeLogEntry.FIELD_SUBJECT, entry.getSimpleSubject());
			b.set(StandardChangeLogEntry.FIELD_TIMESTAMP, entry.getSimpleTimestamp());
			b.set(StandardChangeLogEntry.FIELD_CHANGE_MADE_BY_USER_ID, entry.getSimpleChangeMadeByUserId());
			b.set(StandardChangeLogEntry.FIELD_SHORT_DESCRIPTION, r.readString(StandardChangeLogEntry.FIELD_SHORT_DESCRIPTION.getSimpleFieldName().getSimpleName(), null));
			b.set(StandardChangeLogEntry.FIELD_COMMENTS, r.readString(StandardChangeLogEntry.FIELD_COMMENTS.getSimpleFieldName().getSimpleName(), entry.getOptionalComments(null)));

			while (true)
			{
				String attachment_id = r.readString(StandardChangeLogEntry.FIELD_ATTACHMENTS.getSimpleFieldName().getSimpleName(), null);
				if (attachment_id == null)
				{
					break;
				}

				b.add(StandardChangeLogEntry.FIELD_ATTACHMENTS, new ObjectId(attachment_id));
				logger.info("builder added " + new ObjectId(attachment_id).getSimpleValue());
			}

			Collection<PageDataElement> elements = page_data.getAllElements();

			for (PageDataElement element : elements)
			{
				if (element.hasFileData())
				{
					String file_name = element.getOptionalFilename(null);
					byte[] bytes = element.getOptionalFileData(null);

					if (bytes == null)
					{
						ServletUtil.writeSerializedResponse(response, new GeneralResponseError("failed to read bytes from part"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return;
					}

					if (file_name != null)
					{
						StorageKeyExtension extension = new StorageKeyExtension(".unknown");

						int extension_delim = file_name.lastIndexOf(".");
						try
						{
							extension = new StorageKeyExtension(file_name.substring(extension_delim).toLowerCase());
						} catch (Exception e)
						{
							logger.error(e);
						}

						ObjectIdStorageKey key = null;
						try
						{
							key = new ObjectIdStorageKey(KIND, ObjectId.createRandomId(), extension);
						} catch (Exception e)
						{
							logger.error(e);
						}

						AttachmentMetaData meta_data = null;
						try
						{
							meta_data = new AttachmentMetaData(key.getSimpleObjectId(), file_name, new DownloadFileName(file_name), extension.getSimpleMimeType(), System.currentTimeMillis(), new Long(bytes.length));
						} catch (Exception e)
						{
							logger.error(e);
						}

						if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(key, bytes, false) && CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(meta_data, Format.JSON_PRETTY_PRINT))
						{
							b.add(StandardChangeLogEntry.FIELD_ATTACHMENTS, key.getSimpleObjectId());
							logger.info(String.format("Got request part: %s=%s, file size=%d stored as %s", element.getElementName(), file_name, element.getOptionalFileSize(0), key.getSimpleValue()));

						} else
						{
							ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Failed to upsert file %s", file_name)), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return;
						}
					}
				}
			}

			b.set(StandardChangeLogEntry.FIELD_BEFORE, entry.getOptionalBefore(null));

			b.set(StandardChangeLogEntry.FIELD_AFTER, entry.getOptionalAfter(null));

			StandardChangeLogEntry new_entry = (StandardChangeLogEntry) b.create(null);

			// logger.info(new_entry);

			try
			{
				if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(new_entry, Format.JSON_PRETTY_PRINT))
				{
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Storage failed"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
			} catch (Exception e)
			{
				logger.warn(e);
			}

			try
			{

				if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new_entry))
				{
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Search failed"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
			} catch (Exception e)
			{
				logger.warn(e);
			}

			ServletUtil.writeSerializedResponse(response, new GeneralResponseOK("Success"), HttpServletResponse.SC_OK);
			return;
		} catch (Exception e)
		{
			logger.error("Unexpected Exception", e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to upsert new changelog entry"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
}
