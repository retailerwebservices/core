package org.jimmutable.cloud.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.CacheKey;

public class ApplicationHeartbeatUtils
{
	public static final int SECONDS_PER_UPDATE = 10;
	private static Logger logger = LogManager.getLogger(ApplicationHeartbeatUtils.class);

	public static CacheKey createCacheKey( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		String heartbeat_key = String.format("%s-%s://heartbeat", application_id, application_sub_service_id);
		return new CacheKey(heartbeat_key);
	}

	/*
	 * Allows user, from any application, to check if any application's sub service
	 * is up and running. This gives some room for cache misses by only checking if
	 * we have recieved a heartbeat in the last 30 seconds. Even though we post
	 * every 10 seconds.
	 */
	public static boolean hasHeartbeat( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		CacheKey cache_key = createCacheKey(application_id, application_sub_service_id);
		String last_heartbeat_timestamp_string = CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService().getString(cache_key, null);
		if ( last_heartbeat_timestamp_string == null )
		{
			logger.error(String.format("No submitted heartbeats for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return false;
		}

		long last_heartbeat_timestamp = Long.valueOf(last_heartbeat_timestamp_string);
		long thirty_seconds_ago = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30);
		if ( last_heartbeat_timestamp < thirty_seconds_ago )
		{
			logger.error(String.format("No submitted heartbeats in the last 30 seconds for application_id:%s and application_sub_service_id:%s", application_id, application_sub_service_id));
			return false;
		}

		return true;
	}

	public static void setupHeartbeat( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ApplicationHeartbeat heartbeat_thread = new ApplicationHeartbeat(application_id, application_sub_service_id);
		scheduler.scheduleAtFixedRate(heartbeat_thread, 0, SECONDS_PER_UPDATE, TimeUnit.MILLISECONDS);
	}
}