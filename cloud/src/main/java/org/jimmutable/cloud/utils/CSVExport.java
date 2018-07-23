package org.jimmutable.cloud.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.ElasticSearchTransportClient;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.RequestExportCSV;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CSVExport
{

	// static private final int SEARCH_MAX = 10000;
	static private final CsvPreference CSV_PREFERENCE = CsvPreference.STANDARD_PREFERENCE;

	static private final Logger logger = LogManager.getLogger(CSVExport.class);

	private static void writeAllToCSV(List<SearchFieldId> sorted_header, IndexDefinition index, String query_string, ICsvListWriter list_writer, CellProcessor[] cell_processors)
	{

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().writeAllToCSV(index, query_string, sorted_header, list_writer, cell_processors);
	}

	// private static void getDocuments(List<SearchFieldId> sorted_header,
	// IndexDefinition index, String query_string, ICsvListWriter list_writer,
	// CellProcessor[] cell_processors, int nextStart)
	// {
	//
	// logger.info("query:" + query_string + "nextStart:" + nextStart);
	// JSONServletResponse response =
	// CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(index,
	// new StandardSearchRequest(query_string, SEARCH_MAX, nextStart));
	//
	// if (response instanceof SearchResponseOK)
	// {
	//
	// SearchResponseOK ok = (SearchResponseOK) response;
	//
	// String[] document;
	//
	// for (OneSearchResult result : ok.getSimpleResults())
	// {
	// document = new String[sorted_header.size()];
	//
	// Map<String, String> map = new HashMap<>();
	//
	// result.getSimpleContents().forEach((key, value) ->
	// {
	//
	// map.put(key.getSimpleName(), value);
	//
	// });
	//
	// for (SearchFieldId field_name : sorted_header)
	// {
	// if (map.containsKey(field_name.getSimpleValue()))
	// {
	// document[sorted_header.indexOf(field_name)] =
	// map.get(field_name.getSimpleValue());
	// }
	// }
	//
	// try
	// {
	// list_writer.write(Arrays.asList(document), cell_processors);
	// } catch (IOException e)
	// {
	// logger.error(e);
	// return;
	// }
	// }
	//
	// // It is not possible to get any further documents after 10000 (SEARCH_MAX)
	// // without using Search After,
	//
	// return;
	//
	// } else
	// {
	// return;
	// }
	//
	// }

	/**
	 * Write the search
	 * 
	 * @param request
	 * @param dest
	 * @return
	 */

	public static boolean exportCSV(RequestExportCSV request, File dest)
	{

		if (request == null || dest == null)
		{
			return false;
		}

		if (request.getSimpleFieldToIncludeInExport().size() < 1)
		{
			logger.info("No fields listed to export");
			return true;
		}

		List<SearchFieldId> sorted_fields = new LinkedList<SearchFieldId>();

		for (SearchFieldId field : request.getSimpleFieldToIncludeInExport())
		{
			sorted_fields.add(field);
		}
		Collections.sort(sorted_fields);

		ICsvListWriter list_writer = null;
		try
		{

			dest.getParentFile().mkdirs();
			list_writer = new CsvListWriter(new FileWriter(dest), CSV_PREFERENCE);

			List<CellProcessor> cell_processor_list = new LinkedList<>();
			sorted_fields.forEach(header ->
			{
				cell_processor_list.add(new Optional());
			});

			String[] header = new String[sorted_fields.size()];

			for (int i = 0; i < sorted_fields.size(); i++)
			{
				header[i] = sorted_fields.get(i).getSimpleValue();
			}

			list_writer.writeHeader(header);

			CellProcessor[] cell_processors = cell_processor_list.toArray(new CellProcessor[cell_processor_list.size()]);

			if (request.getSimpleExportAllDocuments())
			{
				// writeAllToCSV(sorted_fields, request.getSimpleIndex(),
				// request.getSimpleQueryString(), list_writer, cell_processors);
				writeAllToCSV(sorted_fields, request.getSimpleIndex(), "*", list_writer, cell_processors);
			} else
			{
				// getDocuments(sorted_fields, request.getSimpleIndex(),
				// request.getSimpleQueryString(), list_writer, cell_processors, 0);
				writeAllToCSV(sorted_fields, request.getSimpleIndex(), request.getSimpleQueryString(), list_writer, cell_processors);
			}

		} catch (IOException e)
		{
			logger.error(e);
		} finally
		{
			if (list_writer != null)
			{
				try
				{
					list_writer.close();
					return true;
				} catch (IOException e)
				{
					logger.error(e);
				}
			}
		}

		return false;

	}
}
