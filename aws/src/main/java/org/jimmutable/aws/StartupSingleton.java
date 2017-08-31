package org.jimmutable.aws;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.aws.logging.DatedFileHandler;
import org.jimmutable.aws.logging.LoggingUtil;
import org.jimmutable.aws.logging.SingleLineFormatter;

public class StartupSingleton
{

	private static final Logger logger = Logger.getLogger(StartupSingleton.class.getName());

	public static final String LOG_DIR = System.getProperty("user.home").concat(File.separator).concat("logs");

	public static final String APP_NAME = "myapp";

	private static final Level level = Level.INFO;
	private static StartupSingleton instance = null;

	private StartupSingleton()
	{
		// cannot instantiate
	}

	/**
	 * This will load the correct logging formatter and file writer once It will
	 * also make the logs dir within ~/logs
	 */
	public static void setupOnce()
	{
		if (instance == null)
		{

			File dir = new File(LOG_DIR);

			if (!dir.exists())
			{
				try
				{
					dir.mkdir();
				} catch (Exception e)
				{
					logger.warning(String.format("Failed to create logs dir %s", LOG_DIR));
					e.printStackTrace();
				}
			}

			instance = new StartupSingleton();
			LoggingUtil.updateRootLoggingLevel(level);
			LoggingUtil.setRootLoggingFormatter(new SingleLineFormatter());
			LoggingUtil.addRootHandler(new DatedFileHandler());
			logger.info("Singleton loaded");
		}
	}
}
