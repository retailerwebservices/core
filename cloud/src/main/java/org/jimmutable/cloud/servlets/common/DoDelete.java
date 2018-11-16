package org.jimmutable.cloud.servlets.common;

import java.util.StringJoiner;

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
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public abstract class DoDelete extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5725723116550684397L;
	private static final Logger logger = LogManager.getLogger(DoDeleteSearchDocument.class);

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
	{

		String id = request.getParameter("id");

		boolean deleted_storage = deleteStorageKey(id);
		boolean deleted_search = deleteSearchDocument(id);

		StringJoiner error = new StringJoiner(",");
		if (!deleted_storage)
		{
			error.add(String.format("Failed to delete id %s from storage", id));
		}
		if (!deleted_search)
		{
			error.add(String.format("Failed to delete id %s from search", id));
		}

		if (error.length() > 0)
		{
			GeneralResponseError err = new GeneralResponseError(error.toString());
			ServletUtil.writeSerializedResponse(response, err, GeneralResponseError.HTTP_STATUS_CODE_ERROR);
		} else
		{
			GeneralResponseOK ok = new GeneralResponseOK(String.format("Deleted id %s", id));
			ServletUtil.writeSerializedResponse(response, ok, GeneralResponseOK.HTTP_STATUS_CODE_OK);
		}

	}

	protected boolean deleteStorageKey(String id)
	{
		try
		{
			StorageKey key = new ObjectIdStorageKey(getKind(), new ObjectId(id), getStorageKeyExtension());
			return CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(key);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}

	}

	protected boolean deleteSearchDocument(String id)
	{
		IndexDefinition index_definition = getIndexDefinition();
		if (index_definition == null) 
		{
			return true;
		}
		
		try
		{
			SearchDocumentId document_id = new SearchDocumentId(new ObjectId(id).getSimpleValue());
			return CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(getIndexDefinition(), document_id);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}
	}

	public abstract IndexDefinition getIndexDefinition();

	public abstract Kind getKind();

	public abstract StorageKeyExtension getStorageKeyExtension();
}
