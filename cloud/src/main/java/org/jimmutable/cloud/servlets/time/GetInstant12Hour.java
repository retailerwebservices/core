package org.jimmutable.cloud.servlets.time;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.TimeOfDay;
import org.jimmutable.core.utils.Validator;

public class GetInstant12Hour extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3724321354668706713L;

	private static final Logger logger = LogManager.getLogger(GetInstant12Hour.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		Instant default_instant = null;
		try
		{
			default_instant = new Instant(-1l);
		} catch (Exception e)
		{
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Failed to create a default instant, ", e.getMessage())), GetResponseError.HTTP_STATUS_CODE_ERROR);
		}

		try
		{
			default_instant = new Instant(Long.parseLong(request.getParameter("default-value")));
		} catch (Exception e)
		{

		}

		Day day = null;
		try
		{
			day = new Day(request.getParameter("day"));// required mm/dd/yyyy
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		long hours = 12l;

		try
		{
			hours = Long.parseLong(request.getParameter("hours"));
		} catch (NumberFormatException e)
		{
		}

		try
		{
			Validator.min(hours, 1l);
			Validator.max(hours, 12l);
		} catch (ValidationException e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		hours = hours == 12l ? 0l : hours; // 12 comes first

		boolean is_am = request.getParameter("is-am") != null && request.getParameter("is-am").equalsIgnoreCase("false") ? false : true;

		if (!is_am)
		{
			hours += 12l;
		}

		long minutes = 0l;

		try
		{
			minutes = Long.parseLong(request.getParameter("minutes"));
		} catch (NumberFormatException e)
		{
		}
		try
		{
			Validator.min(minutes, 0l);
			Validator.max(minutes, 59l);
		} catch (ValidationException e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}
		long seconds = 0l;

		try
		{
			seconds = Long.parseLong(request.getParameter("seconds"));
		} catch (NumberFormatException e)
		{
		}

		try
		{
			Validator.min(seconds, 0l);
			Validator.max(seconds, 59l);
		} catch (ValidationException e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		TimezoneID timezone = null;
		try
		{
			timezone = new TimezoneID(request.getParameter("timezone"));
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		try
		{
			TimeOfDay time = new TimeOfDay(TimeOfDay.toMillis(hours, minutes, seconds, 0));
			Instant instant = new Instant(Instant.toMillisecondsFromEpoch(day, time, timezone, -1));
			ServletUtil.writeSerializedResponse(response, instant, GetResponseOK.HTTP_STATUS_CODE_OK);
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, default_instant, GetResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

}
