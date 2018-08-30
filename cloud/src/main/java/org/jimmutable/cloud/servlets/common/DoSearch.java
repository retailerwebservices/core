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
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.Sort;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.SortDirection;
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
		String base_search_terms = baseSearchTermsForAllSearchRequests(request, response);
		String search_string = base_search_terms == null ? "" : base_search_terms;
		
		String user_input_search = request.getParameter("search-string") == null ? "" : request.getParameter("search-string");

		if(!search_string.isEmpty() && !user_input_search.isEmpty())
		{
			search_string += " AND " + user_input_search;
		}
		else if(!user_input_search.isEmpty())
		{
			search_string = user_input_search;
		}
		
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
		search_string = getExtraSearchParameters(search_string);
		Sort sort = getSort(Sort.DEFAULT_SORT);
		
		StandardSearchRequest search_request = null;
		try
		{
			search_request = new StandardSearchRequest(search_string, max_results, start_results_after, sort);
		} catch (Exception e)
		{
			logger.error(e);
		}

		getAdditionalParameters(request);
		
		try
		{
			JSONServletResponse json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(getSearchIndexDefinition(), search_request);

			if (json_servlet_response instanceof SearchResponseOK)
			{
				json_servlet_response = updateSearchResponse(json_servlet_response, search_request);
			}
			
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

	protected String getExtraSearchParameters( String search_string )
	{
		return search_string;
	}

	protected void getAdditionalParameters(HttpServletRequest request) {
		// Template method - intended to be overridden if necessary
	}
	
	protected Sort getSort(Sort default_value)
	{
		return default_value;
	}
	
	protected JSONServletResponse updateSearchResponse( JSONServletResponse search_response, StandardSearchRequest request ) 
	{
		// Override to further enrich, change, filter or validate the search response
		return search_response;
	}

	public static String checkForTimes( String search_string )
	{
		if(!(search_string.contains("scheduled_start")||search_string.contains("scheduled_stop"))) {
			return search_string;
		}else {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String[] clauses = search_string.split(" AND");
			StringJoiner refined_search_string = new StringJoiner(" AND");
			for ( String clause : clauses )
			{
				if(clause.contains(">")||clause.contains("<")) { //if we need to do any faffing about with ranges. 
					String split_string = ":>";
					if(clause.contains("<")) {
						split_string = ":<";
					}
					String[] clause_breakdown = clause.split(split_string,2);
					try
					{
						Date date = formatter.parse(clause_breakdown[1].replace('T', ' '));
						refined_search_string.add(clause_breakdown[0]+split_string+date.getTime());
					}
					catch ( ParseException e )
					{
						logger.error(e);
					}
				}else {
					refined_search_string.add(clause);
				}
			}
			
			return refined_search_string.toString();
		}
	}

	abstract protected IndexDefinition getSearchIndexDefinition();

	/**
	 * Override this to add any search terms that need to exist for all searches
	 * on a page.
	 */
	protected String baseSearchTermsForAllSearchRequests(HttpServletRequest request, HttpServletResponse response) {return ""; }
}
