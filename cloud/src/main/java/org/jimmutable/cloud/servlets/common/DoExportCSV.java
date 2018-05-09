package org.jimmutable.cloud.servlets.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.search.RequestExportCSV;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.cloud.utils.CSVExport;
import org.jimmutable.core.objects.StandardObject;

public abstract class DoExportCSV extends HttpServlet
{

	/**
	 *
	 */
	private static final long serialVersionUID = -137690687085158047L;

	private static final Logger logger = LogManager.getLogger(DoExportCSV.class);

	public static final String CSV_MIME_TYPE = "applicaiton/csv";

	private static final int BUFFER = 5 * 1024 * 1024; // 5MB

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		String json = doTranslation(request);

		RequestExportCSV request_csv = null;
		try
		{
			request_csv = (RequestExportCSV) StandardObject.deserialize(json);
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Failed to serialize RequestExportCSV."), HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		File temp_file = new File(String.format("%s/csv-export/%s.csv", System.getProperty("user.home"), System.currentTimeMillis()));
		try
		{
			temp_file.getParentFile().mkdirs();
			temp_file.createNewFile();
		}
		catch ( IOException e )
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		if (CSVExport.exportCSV(request_csv, temp_file))
		{
			response.setContentType(CSV_MIME_TYPE);
			response.setContentLength((int) temp_file.length());
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"export-%s\"", temp_file.getName()));

			byte[] bytes = new byte[BUFFER];
			int bytesRead;

			ServletOutputStream out = null;
			InputStream in = null;
			try
			{
				out = response.getOutputStream();
				in = new FileInputStream(temp_file);

				while ((bytesRead = in.read(bytes)) != -1)
				{
					out.write(bytes, 0, bytesRead);

				}

			} catch (Exception e)
			{
				logger.error(e);
				ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

			if (out != null && in != null)
			{
				try
				{
					out.flush();
					in.close();
					out.close();
				} catch (IOException e)
				{
					logger.error(e);
					ServletUtil.writeSerializedResponse(response, new GeneralResponseError(e.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}

		} else
		{
			ServletUtil.writeSerializedResponse(response, new GeneralResponseError("Server Internal Error. Failed to Export."), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		try
		{
			Files.delete(temp_file.toPath());
		} catch (Exception e)
		{
			logger.error(e);
		}
	}

	private String doTranslation( HttpServletRequest request )
	{
		String start = "{";
		String export_all_documents = "\"export_all_documents\" :" + request.getParameter("export_all_documents") + ",";
		String fields_to_include_in_export = "\"field_to_include_in_export\" :[" + getFile(request.getParameterValues("field_to_include_in_export")) + "],";
		String query_string = "\"query_string\" :\"" + request.getParameter("query_string") + "\"";
		String index = "  \"index\" : \"" + getIndex() + "\",";
		String type_hint = "\"type_hint\" : \"request_export_csv\",";
		String end = "}";
		String toReturn = start + type_hint + index + export_all_documents + fields_to_include_in_export + query_string + end;
		return toReturn;
	}

	private String getFile( String[] parameterValues )
	{
		String toReturn = "";
		if ( parameterValues != null )
		{
			for ( String string : parameterValues )
			{
				toReturn = toReturn + "\"" + string + "\",";
			}
			toReturn = toReturn.substring(0, toReturn.length() - 1);
		}
		return toReturn;
	}
	
	protected abstract String getIndex();
}
