package org.jimmutable.cloud.servlets.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.Sort;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlets.util.RequestPageData;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.core.serialization.reader.HandReader;

public abstract class DoSearch extends HttpServlet
{

	public static final List<String> DEFAULT_TIME_KEYWORDS = Arrays.asList("scheduled_start", "scheduled_stop", "start", "stop");

	private static Logger logger = LogManager.getLogger(DoSearch.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8861251014472849044L;

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response )
	{
		String base_search_terms = baseSearchTermsForAllSearchRequests(request, response);
		String search_string = base_search_terms == null ? "" : base_search_terms;

		String user_input_search = request.getParameter("search-string") == null ? "" : request.getParameter("search-string");

		if ( !search_string.isEmpty() && !user_input_search.isEmpty() )
		{
			search_string += " AND " + user_input_search;
		}
		else if ( !user_input_search.isEmpty() )
		{
			search_string = user_input_search;
		}

		int max_results = 100;
		try
		{
			max_results = Integer.parseInt(request.getParameter("max-results"));
		}
		catch ( Exception e )
		{

		}

		int start_results_after = 0;
		try
		{
			start_results_after = Integer.parseInt(request.getParameter("start-results-after"));
		}
		catch ( Exception e )
		{

		}

		search_string = checkForTimes(search_string);
		Sort sort = getSort(Sort.DEFAULT_SORT);

		StandardSearchRequest search_request = null;
		try
		{
			search_request = new StandardSearchRequest(search_string, max_results, start_results_after, sort);
		}
		catch ( Exception e )
		{
			logger.error(e);
		}

		getAdditionalParameters(request);

		try
		{
			List<OneSearchResultWithTyping> json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(getSearchIndexDefinition(), search_request, null);

			
			json_servlet_response = updateSearchResponse(json_servlet_response, search_request);
			
			ServletUtil.writeSerializedResponse(response, new SearchResponseOK(search_request, json_servlet_response), SearchResponseOK.HTTP_STATUS_CODE_OK);
		}
		catch ( Exception e )
		{
			SearchResponseError error = new SearchResponseError(search_request, e.getMessage());
			ServletUtil.writeSerializedResponse(response, error, SearchResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response )
	{
		String base_search_terms = baseSearchTermsForAllSearchRequestsPOST(request, response);
		String search_string = base_search_terms == null ? "" : base_search_terms;

		RequestPageData page_data = ServletUtil.getPageDataFromPost(request, new RequestPageData());

		if ( page_data.isEmpty() )
		{
			logger.error("Request contains no data");
			ServletUtil.writeSerializedResponse(response, "Request contains no data", SearchResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		HandReader reader = new HandReader(page_data.getOptionalDefaultJSONData(""));

		String param_search_string = reader.readString("search_string", null);

		String user_input_search = param_search_string == null ? "" : param_search_string;

		if ( !search_string.isEmpty() && !user_input_search.isEmpty() )
		{
			search_string += " AND " + user_input_search;
		}
		else if ( !user_input_search.isEmpty() )
		{
			search_string = user_input_search;
		}

		int max_results = 100;
		try
		{
			max_results = Integer.parseInt(reader.readString("search_string", null));
		}
		catch ( Exception e )
		{

		}

		int start_results_after = 0;
		try
		{
			start_results_after = Integer.parseInt(reader.readString("search_string", null));
		}
		catch ( Exception e )
		{

		}
		getAdditionalParametersPOST(request);

		// TODO move the below common GET & POST search code to own common methods
		search_string = checkForTimes(search_string);
		Sort sort = getSort(Sort.DEFAULT_SORT);

		StandardSearchRequest search_request = null;
		try
		{
			search_request = new StandardSearchRequest(search_string, max_results, start_results_after, sort);
		}
		catch ( Exception e )
		{
			logger.error(e);
		}

		try
		{
			List<OneSearchResultWithTyping> json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(getSearchIndexDefinition(), search_request, null);

			json_servlet_response = updateSearchResponse(json_servlet_response, search_request);

			ServletUtil.writeSerializedResponse(response, new SearchResponseOK(search_request, json_servlet_response), SearchResponseOK.HTTP_STATUS_CODE_OK);

		}
		catch ( Exception e )
		{
			SearchResponseError error = new SearchResponseError(search_request, e.getMessage());
			ServletUtil.writeSerializedResponse(response, error, SearchResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

	protected void getAdditionalParameters( HttpServletRequest request )
	{
		// Template method - intended to be overridden if necessary
	}

	protected void getAdditionalParametersPOST( HttpServletRequest request )
	{
		// Template method - intended to be overridden if necessary
	}

	protected Sort getSort( Sort default_value )
	{
		return default_value;
	}

	protected List<OneSearchResultWithTyping> updateSearchResponse( List<OneSearchResultWithTyping> search_response, StandardSearchRequest request )
	{
		// Override to further enrich, change, filter or validate the search response
		return search_response;
	}

	public String checkForTimes( String search_string )
	{
		if ( !getListOfTimeKeywords().stream().anyMatch(s -> search_string.contains(s)) )
		{
			return search_string;
		}
		else
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getTimeZone("EST"));
			String[] clauses = search_string.split(" AND");
			StringJoiner refined_search_string = new StringJoiner(" AND");
			for ( String clause : clauses )
			{
				boolean inclusive = false;
				if ( clause.contains(">") || clause.contains("<") )
				{ // if we need to do any faffing about with ranges.
					String split_string = ":>";
					if ( clause.contains("=") )
					{
						clause = clause.replace("=", "");
						inclusive = true;
					}
					if ( clause.contains("<") )
					{
						split_string = ":<";
					}
					String[] clause_breakdown = clause.split(split_string, 2);
					try
					{
						String date_string = clause_breakdown[1].replace('T', ' ');
						if ( !date_string.contains(":") )
						{
							date_string = date_string + " 0:00";
						}
						Date date = formatter.parse(date_string);
						if ( inclusive )
						{
							refined_search_string.add("(" + clause_breakdown[0] + split_string + date.getTime() + " OR " + clause_breakdown[0] + ":" + date.getTime() + ")");
						}
						else
						{
							refined_search_string.add(clause_breakdown[0] + split_string + date.getTime());
						}

					}
					catch ( ParseException e )
					{
						logger.error(e);
					}
				}
				else
				{
					refined_search_string.add(clause);
				}
			}

			return refined_search_string.toString();
		}
	}

	public List<String> getListOfTimeKeywords()
	{
		return DEFAULT_TIME_KEYWORDS;
	}

	abstract protected IndexDefinition getSearchIndexDefinition();

	/**
	 * Override this to add any search terms that need to exist for all searches on
	 * a page.
	 */
	protected String baseSearchTermsForAllSearchRequests( HttpServletRequest request, HttpServletResponse response )
	{
		return "";
	}

	/**
	 * Override this to add any search terms that need to exist for all searches on
	 * a page.
	 */
	protected String baseSearchTermsForAllSearchRequestsPOST( HttpServletRequest request, HttpServletResponse response )
	{
		return "";
	}
}
