package org.jimmutable.cloud.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.CacheKey;

public class ApplicationHeartbeatUtils
{
	public static final int SECONDS_PER_UPDATE = 10;
	private static Logger logger = LoggerFactory.getLogger(ApplicationHeartbeatUtils.class);

	protected static CacheKey createHeartbeatCacheKey( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		String heartbeat_key = String.format("%s-%s://heartbeat", application_id, application_sub_service_id);
		return new CacheKey(heartbeat_key);
	}

	/*
	 * This is a heartbeat that only should be used at the very initial startup of a
	 * Jimmutable application.
	 */
	protected static CacheKey createStartbeatCacheKey( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		String startbeat_key = String.format("%s-%s://startbeat", application_id, application_sub_service_id);
		return new CacheKey(startbeat_key);
	}

	/*
	 * Allows user, from any application, to check if any application's sub service
	 * is up and running. This gives some room for cache misses by only checking if
	 * we have received a heartbeat in the last 30 seconds. Even though we post
	 * every 10 seconds.
	 */
	public static boolean hasHeartbeat( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		CacheKey cache_key = createHeartbeatCacheKey(application_id, application_sub_service_id);
		String last_heartbeat_timestamp_string = CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService().getString(cache_key, null);
		if ( last_heartbeat_timestamp_string == null )
		{
			logger.error(String.format("No submitted heartbeats for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return false;
		}

		long last_heartbeat_timestamp = Long.valueOf(last_heartbeat_timestamp_string);
		long thirty_seconds_ago = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(SECONDS_PER_UPDATE * 3);
		if ( last_heartbeat_timestamp < thirty_seconds_ago )
		{
			logger.error(String.format("No submitted heartbeats in the last 30 seconds for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return false;
		}

		return true;
	}

	/*
	 * Allows a user to retrieve the last time we got a timestamp for when an
	 * application last reported a heartbeat
	 */
	public static Long getOptionalLastHeartbeatTimestamp( ApplicationId application_id, ApplicationId application_sub_service_id, Long default_value )
	{
		CacheKey cache_key = ApplicationHeartbeatUtils.createHeartbeatCacheKey(application_id, application_sub_service_id);
		return getOptionalLastTimestamp(cache_key, application_id, application_sub_service_id, default_value);
	}

	/*
	 * Allows a user to retrieve the last time we got a timestamp for when an
	 * application started
	 */
	public static Long getOptionalLastStartbeatTimestamp( ApplicationId application_id, ApplicationId application_sub_service_id, Long default_value )
	{
		CacheKey cache_key = ApplicationHeartbeatUtils.createStartbeatCacheKey(application_id, application_sub_service_id);
		return getOptionalLastTimestamp(cache_key, application_id, application_sub_service_id, default_value);
	}

	private static Long getOptionalLastTimestamp( CacheKey cache_key, ApplicationId application_id, ApplicationId application_sub_service_id, Long default_value )
	{
		String last_heartbeat_timestamp_string = CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService().getString(cache_key, null);
		if ( last_heartbeat_timestamp_string == null )
		{
			logger.error(String.format("No submitted beats for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return default_value;
		}
		try
		{
			return Long.valueOf(last_heartbeat_timestamp_string);
		}
		catch ( Exception e )
		{
			logger.error(String.format("Could not parse last_heartbeat_timestamp_string %s for application_id:%s and application_sub_service_id:%s", last_heartbeat_timestamp_string, application_id, application_sub_service_id), e);
			return default_value;
		}
	}

	/*
	 * Makes the most recent runtime a bit more human readable and handles the error
	 * cases. Really only useful for cron jobs as they have a termination case.
	 */
	public static Long getComplexLastRuntime( ApplicationId application_id, ApplicationId application_sub_service_id, Long on_still_running, Long on_error )
	{
		Long last_startbeat = getOptionalLastStartbeatTimestamp(application_id, application_sub_service_id, null);
		if ( last_startbeat == null )
		{
			logger.error(String.format("No submitted startbeat for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return on_error;
		}

		Long last_heartbeat = getOptionalLastHeartbeatTimestamp(application_id, application_sub_service_id, null);
		if ( last_heartbeat == null )
		{
			logger.error(String.format("No submitted heartbeats for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return on_error;
		}

		/*
		 * We only want to state an applications run time after it's last reported
		 * heartbeat has had at least 3 chances to give us its latest heartbeat.
		 */
		long thirty_seconds_ago = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(SECONDS_PER_UPDATE * 3);
		if ( last_heartbeat.longValue() > thirty_seconds_ago )
		{
			logger.error(String.format("Service is likely still running since last heartbeat was less than 30 seconds ago for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return on_still_running;
		}

		/*
		 * We now know we have a dead sub service so we can calculate its last runtime.
		 */
		long runtime = last_heartbeat.longValue() - last_startbeat.longValue();

		return runtime;
	}

	public static void setupHeartbeat( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ApplicationHeartbeat heartbeat_thread = new ApplicationHeartbeat(application_id, application_sub_service_id);
		scheduler.scheduleAtFixedRate(heartbeat_thread, 0, SECONDS_PER_UPDATE, TimeUnit.MILLISECONDS);
	}
}