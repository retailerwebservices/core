package org.jimmutable.cloud.servlets.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlets.util.ServletUtil;

public abstract class DoSearch extends HttpServlet
{

	private static Logger logger = LogManager.getLogger(DoSearch.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8861251014472849044L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		String search_string = request.getParameter("search-string") == null ? "" : request.getParameter("search-string");

		int max_results = 100;
		try
		{
			max_results = Integer.parseInt(request.getParameter("max-results"));
		} catch (Exception e)
		{

		}

		int start_results_after = 0;
		try
		{
			start_results_after = Integer.parseInt(request.getParameter("start-results-after"));
		} catch (Exception e)
		{

		}
		search_string = checkForTimes(search_string);
		StandardSearchRequest search_request = null;
		try
		{
			search_request = new StandardSearchRequest(search_string, max_results, start_results_after);
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Failed to create StandardSearchRequest %s", e.getMessage())), GetResponseError.HTTP_STATUS_CODE_ERROR);
		}

		try
		{
			JSONServletResponse json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(getSearchIndexDefinition(), search_request);

			if (json_servlet_response instanceof SearchResponseOK)
			{
				SearchResponseOK ok = (SearchResponseOK) json_servlet_response;
				ServletUtil.writeSerializedResponse(response, ok, SearchResponseOK.HTTP_STATUS_CODE_OK);
			} else if (json_servlet_response instanceof SearchResponseError)
			{
				SearchResponseError error = (SearchResponseError) json_servlet_response;
				ServletUtil.writeSerializedResponse(response, error, SearchResponseError.HTTP_STATUS_CODE_ERROR);
			} else
			{
				throw new Exception("Unexpected JSONServletResponse returned!");
			}
		} catch (Exception e)
		{
			SearchResponseError error = new SearchResponseError(search_request, e.getMessage());
			ServletUtil.writeSerializedResponse(response, error, SearchResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

	public static String checkForTimes(String search_string)
	{
		if (!(search_string.contains("scheduled_start") || search_string.contains("scheduled_stop")))
		{
			return search_string;
		} else
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String[] clauses = search_string.split(" AND");
			StringJoiner refined_search_string = new StringJoiner(" AND");
			for (String clause : clauses)
			{
				if (clause.contains(">") || clause.contains("<"))
				{ // if we need to do any faffing about with ranges.
					String split_string = ":>";
					if (clause.contains("<"))
					{
						split_string = ":<";
					}
					String[] clause_breakdown = clause.split(split_string, 2);
					try
					{
						Date date = formatter.parse(clause_breakdown[1].replace('T', ' '));
						refined_search_string.add(clause_breakdown[0] + split_string + date.getTime());
					} catch (ParseException e)
					{
						logger.error(e);
					}
				} else
				{
					refined_search_string.add(clause);
				}
			}

			return refined_search_string.toString();
		}
	}

	abstract protected IndexDefinition getSearchIndexDefinition();

}
