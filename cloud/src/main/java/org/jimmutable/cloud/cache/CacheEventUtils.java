package org.jimmutable.cloud.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheEventUtils
{	
	private static final char COMMA = ',';
	
	private static final Logger logger = LogManager.getLogger(CacheEventUtils.class);

	public static void log( CacheEvent event )
	{
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
