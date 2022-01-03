package org.jimmutable.cloud.logging;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.messaging.signal.SignalTopicId;
import org.jimmutable.core.utils.Validator;
import org.slf4j.event.Level;

/**
 * Replaces deprecated Log4jUtil using the generic SLF4J.
 * 
 * @author preston.mccumber
 */
public class Slf4jUtil
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
	 * Set the Log4j Level of all Loggers
	 * 
	 * @param level
	 *            The logging Level
	 */
	public static void setAllLoggerLevels(Level level)
	{
		Validator.notNull(level);
		
		// Not implemented
	}
	
}
