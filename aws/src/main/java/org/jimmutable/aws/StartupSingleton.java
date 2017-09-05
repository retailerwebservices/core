package org.jimmutable.aws;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.aws.logging.DatedFileHandler;
import org.jimmutable.aws.logging.LoggingUtil;
import org.jimmutable.aws.logging.SingleLineFormatter;

/**
 * This Singleton is intended to be called from the application's main
 * or @Before method to initialize the correct logging handlers by calling
 * StartupSingleton.setupOnce()
 * 
 * @author trevorbox
 *
 */
public class StartupSingleton
{

	private static final Logger logger = Logger.getLogger(StartupSingleton.class.getName());

	// System properties
	private static final String system_property_logging_dir = "logging.dir";
	private static final String system_property_logging_file_name = "logging.file.name";
	private static final String system_property_logging_level = "logging.level";

	// default
	private static String LOG_DIR = String.format("%s%slogs", System.getProperty("user.home"), File.separator);
	// default
	private static String FILE_NAME = "application.name";
	// default
	private static Level LOG_LEVEL = Level.WARNING;

	private static StartupSingleton instance = null;

	private StartupSingleton()
	{
		// cannot instantiate
	}

	/**
	 * 
	 * @return The directory logs will be put into, configured by logging.dir system
	 *         property
	 */
	public static String getSimpleLoggingDirectory()
	{
		setupOnce();
		return LOG_DIR;
	}

	/**
	 * 
	 * @return The fine name the logs will be put into, configured by
	 *         logging.file.name system property
	 */
	public static String getSimpleLoggingFileName()
	{
		setupOnce();
		return FILE_NAME;
	}

	/**
	 * 
	 * @return the logging level, configured by logging.level system property
	 */
	public static Level getSimpleLoggingLevel()
	{
		setupOnce();
		return LOG_LEVEL;
	}

	/**
	 * This configure where and how the logs will be written based on the
	 * "logging.dir","logging.file.name" and "logging.level" system properties.
	 * 
	 * See this class for default info.
	 * 
	 */
	public static void setupOnce()
	{
		if (instance == null)
		{
			instance = new StartupSingleton();

			String log_dir = System.getProperty(system_property_logging_dir);
			if (log_dir != null)
			{
				LOG_DIR = log_dir;
			}

			String file_name = System.getProperty(system_property_logging_file_name);
			if (file_name != null)
			{
				FILE_NAME = file_name;
			}

			String log_level = System.getProperty(system_property_logging_level);
			if (log_level != null)
			{
				try
				{
					LOG_LEVEL = Level.parse(log_level);
				} catch (IllegalArgumentException e)
				{
					logger.log(Level.WARNING, String.format("Failed to parse the log level %s", log_level), e);
				}
			}

			File dir = new File(LOG_DIR);
			if (!dir.exists())
			{
				try
				{
					dir.mkdir();
				} catch (Exception e)
				{
					logger.log(Level.SEVERE, String.format("Failed to create logs dir %s", LOG_DIR), e);
				}
			}

			LoggingUtil.setRootLoggingFormatter(new SingleLineFormatter());
			LoggingUtil.addRootHandler(new DatedFileHandler());
			LoggingUtil.updateRootLoggingLevel(LOG_LEVEL);

			logger.config(String.format("Logging output directory: %s", LOG_DIR));
			logger.config(String.format("Logging output file name: %s", FILE_NAME));
			logger.config(String.format("Logging level: %s", LOG_LEVEL));

		}
	}
}
