package org.jimmutable.aws.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A utility class to setup logging formats and handlers. The StartupSingleton
 * should call this.
 * 
 * @author trevorbox
 *
 */
public class LoggingUtil
{

	private static final String ROOT_LOGGER = "";

	/**
	 * Update all root logger levels
	 * 
	 * @param level
	 *            the java.util.logging.Level
	 */
	public static void updateRootLoggingLevel(Level level)
	{
		// CODE REVIEW: What if level is null?
		for (Handler h : Logger.getLogger(ROOT_LOGGER).getHandlers())
		{
			h.setLevel(level);
		}

	}

	/**
	 * Adds a handler to the root logger. Be careful adding handlers, logs are
	 * written to all of them.
	 * 
	 * You shouldn't need to call this except for special circumstances, a file
	 * handler is added at runtime already
	 * 
	 * @param handler
	 *            the java.util.logging.Handler
	 */
	public static void addRootHandler(Handler handler)
	{
		// CODE REVIEW: What if handler is null?
		Logger.getLogger(ROOT_LOGGER).addHandler(handler);
	}

	/**
	 * Updates all root logging handlers' formatter
	 * 
	 * @param formatter
	 *            the java.util.logging.Formatter
	 */
	public static void setRootLoggingFormatter(Formatter formatter)
	{
		// CODE REVIEW: What if formatter is null?
		
		for (Handler h : Logger.getLogger(ROOT_LOGGER).getHandlers())
		{
			h.setFormatter(formatter);
		}
	}
}
