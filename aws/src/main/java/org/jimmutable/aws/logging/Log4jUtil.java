package org.jimmutable.aws.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.properties.PropertiesConfiguration;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationBuilder;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationFactory;
import org.jimmutable.core.utils.Validator;

/**
 * This allows programmatic override of the Log4j root logging level and should
 * be configured at application startup.
 * 
 * Note: you should not be using Log4j for application logging, this is a
 * supplementary need for elasticsearch to play nicely with our JUL logging
 * implementation since will overrides the JUL console logging level
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

	private static Map<java.util.logging.Level, org.apache.logging.log4j.Level> level_jul_log4j;

	static {

		// https://www.slf4j.org/apidocs/org/slf4j/bridge/SLF4JBridgeHandler.html

		level_jul_log4j = new HashMap<java.util.logging.Level, org.apache.logging.log4j.Level>();

		level_jul_log4j.put(java.util.logging.Level.FINEST, org.apache.logging.log4j.Level.TRACE);
		level_jul_log4j.put(java.util.logging.Level.FINER, org.apache.logging.log4j.Level.DEBUG);
		level_jul_log4j.put(java.util.logging.Level.FINE, org.apache.logging.log4j.Level.DEBUG);
		level_jul_log4j.put(java.util.logging.Level.CONFIG, org.apache.logging.log4j.Level.INFO);
		level_jul_log4j.put(java.util.logging.Level.INFO, org.apache.logging.log4j.Level.INFO);
		level_jul_log4j.put(java.util.logging.Level.WARNING, org.apache.logging.log4j.Level.WARN);
		level_jul_log4j.put(java.util.logging.Level.SEVERE, org.apache.logging.log4j.Level.ERROR);
		level_jul_log4j.put(java.util.logging.Level.OFF, org.apache.logging.log4j.Level.OFF);
		level_jul_log4j.put(java.util.logging.Level.ALL, org.apache.logging.log4j.Level.ALL);

		// Get file from resources folder
		// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// String path = classLoader.getResource("log4j2.properties").getPath();

	}

	public static void setLevel(java.util.logging.Level level)
	{

		Validator.notNull(level);

		String path = String.format("%s%s%s", System.getProperty("user.home"), File.separator, "log4j2.properties");

		System.out.println(String.format("log4j path: %s", path));

		// InputStream is = Log4jUtil.class.getResourceAsStream("log4j2.xml");
		// try {
		// InputStream is = new FileInputStream(new File(path));
		// ConfigurationSource source = new ConfigurationSource(is);
		// Configuration config =
		// PropertiesConfigurationFactory.getInstance().getConfiguration((LoggerContext)
		// LogManager.getContext(false), source);
		// LoggerContext ctx2 = (LoggerContext) LogManager.getContext(false);
		// ctx2.stop();
		// ctx2.start(config);
		// } catch (Exception e) {
		//
		// }

		if (!level_jul_log4j.containsKey(level)) {
			throw new RuntimeException(String.format("No log4j Level mapping exists for JUL Level %s", level));
		}

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();
		conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(level_jul_log4j.get(level));
		ctx.updateLoggers(conf);

		Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
		log.info(String.format("Log4j root logger level is %s", log.getLevel()));
	}

}
