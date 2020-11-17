package org.jimmutable.cloud.cache;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CacheEventUtils
{	
	private static final char COMMA = ',';
	/*
	 * Cache event logging has become a bit too noisy at times in our production
	 * logs as well as taking up a large chunk of cloud logging data we are alloted
	 * per day, this is toggle to disable it by default when we're not actively 
	 * monitoring cache metrics
	 */
	private static final String ENABLE_CACHE_EVENT_LOGGING  = "enable_cache_event_logging";
	private static final boolean IS_CACHE_EVENT_LOGGING_ENABLED = Boolean.getBoolean(ENABLE_CACHE_EVENT_LOGGING);

	private static final Logger logger = LoggerFactory.getLogger(CacheEventUtils.class);

	public static void log( CacheEvent event )
	{
		if(!IS_CACHE_EVENT_LOGGING_ENABLED)
		{
			return;
		}
		
		final StringBuilder sb = new StringBuilder();

		sb.append(event.getSimpleTimestampHumanReadable());
		sb.append(COMMA);

		sb.append("CACHE");
		sb.append(COMMA);

		sb.append(event.getSimpleActivity().getSimpleCode());
		sb.append(COMMA);

		sb.append(event.getSimpleMetric().getSimpleCode());
		sb.append(COMMA);
		
		sb.append(event.getSimpleKey().toString());
		sb.append(COMMA);

		String string = sb.toString();
		
		logger.info(string);
	}
}
