package org.jimmutable.cloud.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.selector.BasicContextSelector;
import org.jimmutable.core.utils.Validator;

/**
 * Programmatic Log4j administration. Such as setting the logging levels of all
 * appenders.
 * 
 * @author trevorbox
 *
 */
public class Log4jUtil
{

	/**
	 * -Dlog4j.configurationFile=/Users/trevorbox/log4j2.properties
	 * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
	 */

	/**
	 * Set the Log4j Level of all Loggers
	 * 
	 * @param level
	 *            The logging Level
	 */
	public static void setAllLoggerLevels(Level level)
	{
		Validator.notNull(level);

		// for some reason the default configuration level gets set this way...
		Configurator.setRootLevel(level);

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();
	
//		conf.getRootLogger().setLevel(Level.OFF);

		
		conf.getLoggers().forEach((k, v) -> {
			conf.getLoggerConfig(k).setLevel(level);
		});
		ctx.updateLoggers(conf);

		Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

		log.info(String.format("Updated logging levels to %s", log.getLevel()));
	
	}

}
