package org.jimmutable.cloud.servlets.attachments;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.attachments.DownloadFileName;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlets.avatar.DoUpsertAvatar;
import org.jimmutable.cloud.servlets.util.PageDataElement;
import org.jimmutable.cloud.servlets.util.RequestPageData;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StandardImmutableObjectCache;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.HandReader;

@Deprecated
public class DoUploadAttachments extends HttpServlet
{

	/**
	 *
	 */
	private static final long serialVersionUID = -5235981695936464860L;
	static private final Logger logger = LogManager.getLogger(DoUpsertAvatar.class);

	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		ObjectId id = null;
		String description = null;
		String comments = null;
		String file_name = "";
		byte[] file = null;

		FieldList new_attachments = new FieldArrayList<>();

		RequestPageData page_data = ServletUtil.getPageDataFromPost(request, new RequestPageData());
		Collection<PageDataElement> elements = page_data.getAllElements();

		for ( PageDataElement element : elements )
		{
			if ( element.hasFileData() )
			{
				file_name = element.getOptionalFilename(null);
				file = element.getOptionalFileData(null);

				if ( file == null )
				{
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError("failed to read bytes from part"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}

				if ( file_name != null )
				{
					StorageKeyExtension extension = new StorageKeyExtension(".unknown");
					int extension_delim = file_name.lastIndexOf(".");

					try
					{
						extension = new StorageKeyExtension(file_name.substring(extension_delim).toLowerCase());
					}
					catch ( Exception e )
					{
						logger.error(e);
					}

					ObjectIdStorageKey key = null;
					try
					{
						key = new ObjectIdStorageKey(AttachmentMetaData.KIND, ObjectId.createRandomId(), extension);
					}
					catch ( Exception e )
					{
						logger.error(e);
					}

					if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(key, file, false) )
					{
						new_attachments.add(key.getSimpleObjectId());
						logger.info(String.format("Got request part: %s=%s, file size=%d stored as %s", element.getElementName(), file_name, element.getOptionalFileSize(0), key.getSimpleValue()));
					}
					else
					{

						ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Failed to upsert file %s", file_name)), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return;
					}
				}

			}
			else
			{
				// non-file data
				HandReader reader = new HandReader(page_data.getOptionalDefaultJSONData(""));
				description = reader.readString("description", null);
				comments = reader.readString("comments", null);
				String log_id = reader.readString("log_id", null);
				try
				{
					id = new ObjectId(log_id);
				}
				catch ( Exception e )
				{
					logger.error(e);
				}
			}
		}

		String mime_type = URLConnection.guessContentTypeFromName(file_name);
		upsert(file, description, file_name, mime_type, null, response);
		try
		{

			StorageKey key = new ObjectIdStorageKey(StandardChangeLogEntry.KIND, id, StorageKeyExtension.JSON);

			if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().exists(key, false) )
			{
				String json = new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null), "UTF-8");

				StandardChangeLogEntry old_entry = (StandardChangeLogEntry) StandardObject.deserialize(json);

				new_attachments.addAll(old_entry.getSimpleAttachments());

				StandardChangeLogEntry new_entry = new StandardChangeLogEntry(old_entry.getSimpleObjectId(), new ObjectReference(StandardChangeLogEntry.KIND, old_entry.getSimpleObjectId()), old_entry.getSimpleTimestamp(), old_entry.getSimpleChangeMadeByUserId(), description, comments, new_attachments, old_entry.getOptionalBefore(null), old_entry.getOptionalAfter(null));

				if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(new_entry, Format.JSON_PRETTY_PRINT) || !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new_entry) )
				{
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to upsert new changelog entry"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}

				ServletUtil.writeSerializedResponse(response, new GeneralResponseOK("Success"), HttpServletResponse.SC_OK);
				return;
			}
			else
			{
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("changelog entry %s does not exist", key.toString())), HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

		}
		catch ( Exception e )
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to upsert new changelog entry"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	public static AttachmentMetaData upsert( byte[] data, String description, String file_name, String mime_type, AttachmentMetaData default_value, HttpServletResponse response ) throws IOException
	{
		AttachmentMetaData meta_data = null;
		try
		{
			meta_data = new AttachmentMetaData(ObjectId.createRandomId(), description, new DownloadFileName(file_name), mime_type, System.currentTimeMillis(), data.length);
		}
		catch ( Exception e )
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}

		Kind kind = new Kind("attatchment-file-bytes");
		CloudExecutionEnvironment simple_current = CloudExecutionEnvironment.getSimpleCurrent();
		simple_current.getSimpleStorage().upsert(new ObjectIdStorageKey(kind, meta_data.getSimpleObjectId(), StorageKeyExtension.BIN), data, false);
		simple_current.getSimpleCache().put(kind, meta_data.getSimpleObjectId(), meta_data);

		simple_current.getSimpleSignalService().sendAsync(StandardImmutableObjectCache.TOPIC_ID, new StandardMessageOnUpsert(kind, meta_data.getSimpleObjectId()));
		return meta_data;
	}
}
