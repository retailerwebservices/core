package org.jimmutable.cloud.servlets.attachments;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;

public class DoGetMetaData extends HttpServlet
{

	/**
	 *
	 */
	private static final long serialVersionUID = 5792636588695267792L;

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		try
		{
			ObjectId object_id = new ObjectId(request.getParameter("objectid"));
		

			String json = new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(AttachmentMetaData.KIND, object_id, StorageKeyExtension.JSON), null), "UTF-8");

			AttachmentMetaData meta_data = (AttachmentMetaData) StandardObject.deserialize(json);
			if ( !meta_data.equals(null) )
			{
				ServletUtil.writeSerializedResponse(response, meta_data, HttpServletResponse.SC_OK);
			}
			else
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch ( Exception E ) // If anything goes wrong send a 404
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

	}
}
