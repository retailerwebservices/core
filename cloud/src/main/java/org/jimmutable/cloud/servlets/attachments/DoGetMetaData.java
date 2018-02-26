package org.jimmutable.cloud.servlets.attachments;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.servlets.common.DoGetGeneric;
import org.jimmutable.core.objects.common.Kind;

public class DoGetMetaData extends DoGetGeneric<AttachmentMetaData>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 5792636588695267792L;
	
	private static final Logger logger = LogManager.getLogger(DoGetMetaData.class);

	@Override
	protected Logger getLogger()
	{
		return logger;
	}

	@Override
	protected Kind getKind()
	{
		return AttachmentMetaData.KIND;
	}
	
	@Override
	protected String getId() {
		return "objectid";
	}
	
	@Override
	protected void objectNotFoundFunction(HttpServletResponse response)
	{
		try
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Override
//	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException
//	{
//		try
//		{
//			ObjectId object_id = new ObjectId(request.getParameter("objectid"));
//		
//
//			String json = new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(AttachmentMetaData.KIND, object_id, StorageKeyExtension.JSON), null), "UTF-8");
//
//			AttachmentMetaData meta_data = (AttachmentMetaData) StandardObject.deserialize(json);
//			if ( !meta_data.equals(null) )
//			{
//				ServletUtil.writeSerializedResponse(response, meta_data, HttpServletResponse.SC_OK);
//			}
//			else
//			{
//				response.sendError(HttpServletResponse.SC_NOT_FOUND);
//			}
//		}
//		catch ( Exception E ) // If anything goes wrong send a 404
//		{
//			response.sendError(HttpServletResponse.SC_NOT_FOUND);
//		}
//
//	}
}
