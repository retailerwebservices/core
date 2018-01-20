package org.jimmutable.cloud.servlets.common;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchDocumentId;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.core.objects.common.ObjectId;

public abstract class DoDeleteSearchDocument extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8209943369568153719L;
	private static final Logger logger = LogManager.getLogger( DoDeleteSearchDocument.class);
	

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
	{

		try
		{
			SearchDocumentId document_id = new SearchDocumentId(new ObjectId(request.getParameter("id")).getSimpleValue());

			if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(getIndexDefinition(), document_id))
			{
				GeneralResponseOK ok = new GeneralResponseOK(String.format("Deleted document %s from index %s", document_id.getSimpleValue(), getIndexDefinition().getSimpleValue()));
				ServletUtil.writeSerializedResponse(response, ok, GeneralResponseOK.HTTP_STATUS_CODE_OK);
			} else
			{
				GeneralResponseError error = new GeneralResponseError(String.format("Failed to delete document %s from index %s", document_id.getSimpleValue(), getIndexDefinition().getSimpleValue()));
				ServletUtil.writeSerializedResponse(response, error, GeneralResponseError.HTTP_STATUS_CODE_ERROR);
			}

		} catch (Exception e)
		{
			GeneralResponseError error = new GeneralResponseError(e.getMessage());
			ServletUtil.writeSerializedResponse(response, error, GeneralResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

	public abstract IndexDefinition getIndexDefinition();

}
