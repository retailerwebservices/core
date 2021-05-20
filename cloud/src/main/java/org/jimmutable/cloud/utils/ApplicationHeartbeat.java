package org.jimmutable.cloud.utils;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.cache.CacheKey;

/**
 * This class is meant to run on start up of any Jimmutable Cloud application.
 * At startup of CEE, we kick off a background thread that will consistently
 * ping a cache key with a heartbeat. Users can then check if an application has
 * reported a heartbeat in recent time via ApplicationHeartbeatUtils.
 */
public class ApplicationHeartbeat implements Runnable
{
	private ApplicationId application_id;
	private ApplicationId application_sub_service_id;

	ApplicationHeartbeat( ApplicationId application_id, ApplicationId application_sub_service_id )
	{
		this.application_id = application_id;
		this.application_sub_service_id = application_sub_service_id;
		// Kick off the beginning marker
		sendStartbeat();
		// Kick off an initial heartbeat
		sendHeartbeat();
	}

	/*
	 * This just gives us a marker for when this application first started
	 */
	private void sendStartbeat()
	{
		CacheKey application_cache_key = ApplicationHeartbeatUtils.createStartbeatCacheKey(application_id, application_sub_service_id);
		String current_time = Long.toString(System.currentTimeMillis());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService().put(application_cache_key, current_time);
	}

	private void sendHeartbeat()
	{
		CacheKey application_cache_key = ApplicationHeartbeatUtils.createHeartbeatCacheKey(application_id, application_sub_service_id);
		String current_time = Long.toString(System.currentTimeMillis());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService().put(application_cache_key, current_time);
	}

	@Override
	public void run()
	{
		sendHeartbeat();
	}

}