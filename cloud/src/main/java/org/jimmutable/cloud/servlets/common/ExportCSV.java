package org.jimmutable.cloud.servlets.common;

import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlet_utils.search.RequestExportCSV;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.utils.CSVExport;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.reader.HandReader;
import org.jimmutable.core.utils.Validator;

public abstract class ExportCSV extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733106032413089932L;
	private static final Logger logger = LogManager.getLogger(ExportCSV.class);

	public static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	public static final char DELIM = '@';

	/**
	 * The index definition of the index to export data from
	 * 
	 * @return IndexDefinition
	 */
	protected abstract IndexDefinition getIndex();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{

		HandReader r = null;
		try
		{
			r = new HandReader(ServletUtil.getOptionalJSON(request, null));
		} catch (Exception e)
		{
			logger.error("Failed to read JSON from request!", e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to read JSON from request!"), HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		IndexDefinition index_definition = getIndex();

		Validator.notNull(index_definition);

		RequestExportCSV request_csv = null;
		try
		{

			Builder b = new Builder(RequestExportCSV.TYPE_NAME);

			b.set(RequestExportCSV.FIELD_INDEX, index_definition);
			b.set(RequestExportCSV.FIELD_EXPORT_ALL_DOCUMENTS, r.readBoolean("export_all_documents", null));
			b.set(RequestExportCSV.FIELD_QUERY_STRING, r.readString("query_string", null));

			String field = r.readString("field_to_include_in_export", null);
			while (field != null)
			{
				b.add(RequestExportCSV.FIELD_FIELD_TO_INCLUDE_IN_EXPORT, new SearchFieldId(field));
				field = r.readString("field_to_include_in_export", null);
			}

			request_csv = b.create(null);

		} catch (Exception e)
		{
			String msg = "Failed to create export csv request object!";
			logger.error(msg, e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(msg), HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		File temp_file = null;
		try
		{
			temp_file = File.createTempFile(index_definition.getSimpleValue() + DELIM, ".csv", TEMP_DIR);
			temp_file.deleteOnExit();
		} catch (IOException e)
		{
			logger.error("Failed to create temporary file!", e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to create temporary file!"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		if (CSVExport.exportCSV(request_csv, temp_file))
		{
			ServletUtil.writeSerializedResponse(response, new GeneralResponseOK(temp_file.getName()), GeneralResponseOK.HTTP_STATUS_CODE_OK);
			return;
		}

	}

}
