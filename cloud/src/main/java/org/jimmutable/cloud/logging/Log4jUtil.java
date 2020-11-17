package org.jimmutable.cloud.logging;

import org.apache.logging.log4j.Level;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.selector.BasicContextSelector;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.cloud.storage.UpsertListener;
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
	 * Central topic for all log level change messages
	 */
	public static final SignalTopicId TOPIC_ID = new SignalTopicId("log-level-change");
	public static void setupListeners()
	{
		LogLevelChangeListener log_level_change_listener = new LogLevelChangeListener();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSignalService().startListening(TOPIC_ID, log_level_change_listener);
	}
	
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

//		// for some reason the default configuration level gets set this way...
//		Configurator.setRootLevel(level);
//
//		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//		Configuration conf = ctx.getConfiguration();
//	
////		conf.getRootLogger().setLevel(Level.OFF);
//
//		
//		conf.getLoggers().forEach((k, v) -> {
//			conf.getLoggerConfig(k).setLevel(level);
//		});
//		ctx.updateLoggers(conf);
//
//		Logger log = LoggerFactory.getLogger(LogManager.ROOT_LOGGER_NAME);
//
//		log.info(String.format("Updated logging levels to %s", log.getLevel()));
	
	}

}
