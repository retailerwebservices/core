package org.jimmutable.cloud.servlets.common;

import java.util.StringJoiner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlets.util.RequestPageData;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.HandReader;

/**
 * Upserts a standard change log entry. The attachment will have already been
 * upserted and the array of ids sent to this servlet
 * 
 * @author trevorbox
 *
 */
public class UpsertStandardChangeLog extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4783836176047064994L;
	static private final Logger logger = LoggerFactory.getLogger(UpsertStandardChangeLog.class);

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response )
	{

		RequestPageData page_data = ServletUtil.getPageDataFromPost(request, new RequestPageData());

		if ( page_data.isEmpty() )
		{
			logger.error("Request contains no data");
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Request contains no data"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		HandReader r = new HandReader(page_data.getOptionalDefaultJSONData(""));

		StorageKey key = null;
		try
		{
			key = new ObjectIdStorageKey(StandardChangeLogEntry.KIND, new ObjectId(r.readString(StandardChangeLogEntry.FIELD_ID.getSimpleFieldName().getSimpleName(), null)), StandardChangeLogEntry.STORABLE_EXTENSION);
		}
		catch ( Exception e )
		{
			String error_message = "Failed to create storage key from json!";
			logger.error(error_message, e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(error_message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		StandardChangeLogEntry entry = null;
		try
		{
			entry = (StandardChangeLogEntry) StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null)));
		}
		catch ( Exception e )
		{
			String error_message = String.format("Failed to retrieve current change log entry %s %s!", key.getSimpleKind(), key.getSimpleName());
			logger.error(error_message, e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(error_message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		StandardChangeLogEntry new_entry = null;
		try
		{

			Builder b = new Builder(StandardChangeLogEntry.TYPE_NAME);

			b.set(StandardChangeLogEntry.FIELD_ID, entry.getSimpleObjectId());
			b.set(StandardChangeLogEntry.FIELD_SUBJECT, entry.getSimpleSubject());
			b.set(StandardChangeLogEntry.FIELD_TIMESTAMP, entry.getSimpleTimestamp());
			b.set(StandardChangeLogEntry.FIELD_CHANGE_MADE_BY_USER_ID, entry.getSimpleChangeMadeByUserId());
			b.set(StandardChangeLogEntry.FIELD_SHORT_DESCRIPTION, r.readString(StandardChangeLogEntry.FIELD_SHORT_DESCRIPTION.getSimpleFieldName().getSimpleName(), null));
			b.set(StandardChangeLogEntry.FIELD_COMMENTS, r.readString(StandardChangeLogEntry.FIELD_COMMENTS.getSimpleFieldName().getSimpleName(), entry.getOptionalComments(null)));

			while ( true )
			{
				String attachment_id = r.readString(StandardChangeLogEntry.FIELD_ATTACHMENTS.getSimpleFieldName().getSimpleName(), null);
				if ( attachment_id == null )
				{
					break;
				}

				b.add(StandardChangeLogEntry.FIELD_ATTACHMENTS, new ObjectId(attachment_id));
			}

			b.set(StandardChangeLogEntry.FIELD_BEFORE, entry.getOptionalBefore(null));
			b.set(StandardChangeLogEntry.FIELD_AFTER, entry.getOptionalAfter(null));

			new_entry = (StandardChangeLogEntry) b.create();

		}
		catch ( Exception e )
		{
			logger.error("Failed to create new change log object!", e);
			StringJoiner joiner = new StringJoiner(", ");
			joiner.add(String.format("%s %s", e.getClass().getName(), e.getMessage()));

			Throwable throwable = e.getCause();
			while ( throwable != null )
			{
				joiner.add(String.format("%s %s", throwable.getClass().getName(), throwable.getMessage()));
				throwable = throwable.getCause();
			}

			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(joiner.toString()), GeneralResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(new_entry, Format.JSON_PRETTY_PRINT) )
		{
			String error_message = "Failed to upsert new change log entry to storage!";
			logger.error(error_message);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(error_message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new_entry) )
		{
			String error_message = "Failed to upsert new change log entry to search!";
			logger.error(error_message);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(error_message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		ServletUtil.writeSerializedResponse(response, new UpsertResponseOK("Success", new_entry), HttpServletResponse.SC_OK);
		return;
	}

}
