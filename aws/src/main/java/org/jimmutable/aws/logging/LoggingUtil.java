package org.jimmutable.aws.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 
 * A utility class to setup logging formats and handlers. The StartupSingleton
 * should call this.
 * 
 * @author trevorbox
 *
 */
@Deprecated
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
		if (level != null) {
			Logger rootLogger = LogManager.getLogManager().getLogger(ROOT_LOGGER);
			rootLogger.setLevel(level);
			for (Handler h : rootLogger.getHandlers()) {
				h.setLevel(level);
			}
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
		if (handler != null) {
			Logger.getLogger(ROOT_LOGGER).addHandler(handler);
		}
	}

	/**
	 * Updates all root logging handlers' formatter
	 * 
	 * @param formatter
	 *            the java.util.logging.Formatter
	 */
	public static void setRootLoggingFormatter(Formatter formatter)
	{
		if (formatter != null) {
			for (Handler h : Logger.getLogger(ROOT_LOGGER).getHandlers()) {
				h.setFormatter(formatter);
			}
		}
	}
}
