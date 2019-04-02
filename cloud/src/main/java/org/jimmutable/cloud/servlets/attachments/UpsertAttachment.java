package org.jimmutable.cloud.servlets.attachments;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.attachments.DownloadFileName;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlets.util.PageDataHandler;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.servlets.util.VisitedPageDataElement;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;

/**
 * This is used by the standard change log to upsert a single attachment and
 * return the attachment metatdata object
 * 
 * @author trevorbox
 *
 */
public class UpsertAttachment extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1157627097469022285L;

	static private final Logger logger = LogManager.getLogger(UpsertAttachment.class);
	static public final Kind KIND = new Kind("attachment");

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response )
	{

		ServletUtil.handlePageDataFromPost(request, new PageDataHandler()
		{
			@Override
			public void handle( VisitedPageDataElement page_data )
			{
				if ( page_data.hasInputStream() )
				{

					StorageKeyExtension extension = new StorageKeyExtension(".unknown");
					ObjectId new_attachment_id = ObjectId.createRandomId();

					String file_name = page_data.getOptionalFilename(String.format("%s.%s", new_attachment_id.getSimpleValue(), extension.getSimpleValue()));
					int extension_delim = file_name.lastIndexOf(".");
					try
					{
						extension = new StorageKeyExtension(file_name.substring(extension_delim).toLowerCase());
					}
					catch ( Exception e )
					{
						onWarning(String.format("Unable to parse extension from file name %s!", file_name), e);
					}

					ObjectIdStorageKey key = null;
					try
					{
						key = new ObjectIdStorageKey(KIND, new_attachment_id, extension);
					}
					catch ( Exception e )
					{
						onError(String.format("Unable to create a storage key for file %s!", file_name), e);
					}

					InputStream stream = page_data.getOptionalInputStream(null);
					if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsertStreaming(key, stream, true) )
					{
						StorageMetadata storage_metadata = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getObjectMetadata(key, null);

						if ( storage_metadata != null )
						{

							AttachmentMetaData attachment_metadata = null;

							Builder b = new Builder(AttachmentMetaData.TYPE_NAME);
							b.set(AttachmentMetaData.FIELD_DESCRIPTION, file_name);
							b.set(AttachmentMetaData.FIELD_LAST_MODIFIED_TIME, storage_metadata.getSimpleLastModified());
							b.set(AttachmentMetaData.FIELD_MIME_TYPE, extension.getSimpleMimeType());
							b.set(AttachmentMetaData.FIELD_SIZE, storage_metadata.getSimpleSize());
							b.set(AttachmentMetaData.FIELD_FILE_NAME, new DownloadFileName(file_name));
							b.set(AttachmentMetaData.FIELD_OBJECT_ID, key.getSimpleObjectId());

							// @CR - I don't see a try/catch for this. Should it be createSilent()? -PM
							attachment_metadata = b.create(null);

							if ( attachment_metadata != null )
							{
								if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(attachment_metadata, Format.JSON_PRETTY_PRINT) )
								{
									ServletUtil.writeSerializedResponse(response, new UpsertResponseOK(String.format("Upserted %s", file_name), attachment_metadata), UpsertResponseOK.HTTP_STATUS_CODE_OK);
								}
								else
								{
									onError("Failed to upsert attachment metadata!");
								}
							}
							else
							{
								onError("Failed to create attachment metadata!");
							}

						}
						else
						{
							onError(String.format("Failed to get Storage metadata for %s %s!", key.getSimpleKind().getSimpleValue(), key.getSimpleName().getSimpleValue()));
						}
					}
					else
					{
						onError(String.format("Failed to upsert attachment file metadata for %s %s!", key.getSimpleKind().getSimpleValue(), key.getSimpleName().getSimpleValue()));
					}
					try
					{
						stream.close();
					}
					catch ( IOException e )
					{
						logger.error("Unexpected error while closing input stream!", e);
					}
				}

			}

			public void onWarning( String message, Throwable e )
			{
				logger.warn(message, e);
			}

			@Override
			public void onWarning( String message )
			{
				logger.warn(message);
			}

			public void onError( String message, Throwable e )
			{
				logger.error(message, e);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(message), GeneralResponseError.HTTP_STATUS_CODE_ERROR);
			}

			@Override
			public void onError( String message )
			{
				logger.error(message);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(message), GeneralResponseError.HTTP_STATUS_CODE_ERROR);
			}
		});

	}

}
