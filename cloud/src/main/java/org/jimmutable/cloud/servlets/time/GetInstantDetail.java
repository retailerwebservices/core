package org.jimmutable.cloud.servlets.time;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlets.util.ServletUtil;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.InstantDetails;

public class GetInstantDetail extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4849652062291860991L;

	private static final Logger logger = LogManager.getLogger(GetInstantDetail.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		Instant instant = null;
		try
		{
			instant = new Instant(Long.parseLong(request.getParameter("ms_from_epoch")));
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Invalid ms_from_epoch: %s", e.getMessage())), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		TimezoneID timezone_id = null;
		try
		{
			timezone_id = new TimezoneID(request.getParameter("timezone_id"));
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format("Invalid timezone_id: %s", e.getMessage())), GetResponseError.HTTP_STATUS_CODE_ERROR);
			return;
		}

		try
		{
			Builder b = new Builder(InstantDetails.TYPE_NAME);
			b.set(InstantDetails.FIELD_SUCCESS, true); // since our servlet will either return an instant or not this is
														// always set to true
			b.set(InstantDetails.FIELD_MS_FROM_EPOCH, instant.getSimpleMillisecondsFromEpoch());

			b.set(InstantDetails.FIELD_TIMEZONE, timezone_id);
			b.set(InstantDetails.FIELD_DAY, instant.toDay(timezone_id));
			b.set(InstantDetails.FIELD_DAY_YEAR, instant.toDay(timezone_id).getSimpleYear());
			b.set(InstantDetails.FIELD_DAY_MONTH, instant.toDay(timezone_id).getSimpleMonthOfYear());
			b.set(InstantDetails.FIELD_DAY_DAY, instant.toDay(timezone_id).getSimpleDayOfMonth());

			b.set(InstantDetails.FIELD_HOURS_ON_TWELVE_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple12hrClockHours());
			b.set(InstantDetails.FIELD_MINUTES_ON_TWELVE_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple12hrClockMinutes());
			b.set(InstantDetails.FIELD_SECONDS_ON_TWELVE_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple12hrClockSeconds());

			b.set(InstantDetails.FIELD_IS_AM_ON_TWELVE_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple12hrClockAm());

			b.set(InstantDetails.FIELD_HOURS_ON_TWENTY_FOUR_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple24hrClockHours());
			b.set(InstantDetails.FIELD_MINUTES_ON_TWENTY_FOUR_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple24hrClockMinutes());
			b.set(InstantDetails.FIELD_SECONDS_ON_TWENTY_FOUR_HOUR_CLOCK, instant.toTimeOfDay(timezone_id).getSimple24hrClockSeconds());
			
			
			b.set(InstantDetails.FIELD_TIMESTAMP, instant.createTimestampString(timezone_id, true, null));
			
			
			ServletUtil.writeSerializedResponse(response, (InstantDetails) b.create(null), GetResponseOK.HTTP_STATUS_CODE_OK);
		} catch (Exception e)
		{
			logger.error(e);
			ServletUtil.writeSerializedResponse(response, new GetResponseError(String.format(e.getMessage())), GetResponseError.HTTP_STATUS_CODE_ERROR);
		}

	}

}
