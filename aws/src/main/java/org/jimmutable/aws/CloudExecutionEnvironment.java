package org.jimmutable.aws;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.aws.logging.DatedFileHandler;
import org.jimmutable.aws.logging.Log4jUtil;
import org.jimmutable.aws.logging.LoggingUtil;
import org.jimmutable.aws.logging.SingleLineFormatter;
import org.jimmutable.storage.ApplicationId;

/**
 * Configures environment and application specific setting, to be used by other
 * classes. This includes EnvironmantType, ApplicationId, and logging settings.
 * 
 * @author trevorbox
 *
 */
public class CloudExecutionEnvironment
{

	private static CloudExecutionEnvironment CURRENT;

	private EnvironmentType type;
	private ApplicationId application_id;

	private static Logger logger;

	// System properties
	private static final String ENV_TYPE_VARIABLE_NAME = "JIMMUTABLE_ENV_TYPE";
	private static final String ENV_APPLICATION_ID = "JIMMUTABLE_APP_ID";
	private static final String ENV_LOGGING_DIR = "logging.dir";
	private static final String ENV_LOGGNG_FILE_NAME = "logging.file.name";
	private static final String ENV_LOGGING_LEVEL = "logging.level";

	// setup the required logging configurations
	static {

		String log_dir = System.getProperty(ENV_LOGGING_DIR);
		if (log_dir == null) {
			// default value
			log_dir = String.format("%s%s%s%slogs", System.getProperty("user.home"), File.separator, "jummutable_aws_dev", File.separator);
		}

		String file_name = System.getProperty(ENV_LOGGNG_FILE_NAME);
		if (file_name == null) {
			// default value
			file_name = "application.name";
		}

		// default value
		Level log_level = Level.ALL;

		String string_level = System.getProperty(ENV_LOGGING_LEVEL);
		if (string_level != null) {
			try {
				log_level = Level.parse(string_level);
			} catch (IllegalArgumentException e) {
				Logger.getLogger(CloudExecutionEnvironment.class.getName()).log(Level.WARNING, String.format("Failed to parse the log level %s", string_level), e);
			}
		}

		LoggingUtil.setRootLoggingFormatter(new SingleLineFormatter());
		LoggingUtil.addRootHandler(new DatedFileHandler());
		LoggingUtil.updateRootLoggingLevel(log_level);
		Log4jUtil.setLevel(log_level);

		logger = Logger.getLogger(CloudExecutionEnvironment.class.getName());

		File dir = new File(log_dir);
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format("Failed to create logs dir %s", log_dir), e);
			}
		}

		logger.config(String.format("Logging output directory: %s", log_dir));
		logger.config(String.format("Logging output file name: %s", file_name));
		logger.config(String.format("Logging level: %s", log_level));
	}

	private CloudExecutionEnvironment(EnvironmentType type, ApplicationId application_id)
	{
		this.type = type;
		this.application_id = application_id;
	}

	public EnvironmentType getSimpleEnvironmentType()
	{
		return type;
	}

	public ApplicationId getSimpleApplicationId()
	{
		return application_id;
	}

	/**
	 * 
	 * ONLY CALL THIS METHOD ONCE
	 * 
	 * Startup must be called within the main method of the application and will
	 * attempt to set the environment type and application id based on the
	 * environment variables JIMMUTABLE_ENV_TYPE and JIMMUTABLE_APP_ID.
	 * 
	 * The DEV environment variable is the default if the variable is not provided
	 * 
	 * @param default_id
	 *            A default application id in case the environment variable is not
	 *            set
	 */
	static public void startup(ApplicationId default_id)
	{
		if (CURRENT != null) {
			logger.severe("Startup has already been called!");
			throw new RuntimeException("Startup has already been called!");
		}
		// default
		EnvironmentType tmp_type = EnvironmentType.DEV;

		String env_level = System.getProperty(ENV_TYPE_VARIABLE_NAME);
		if (env_level != null) {
			try {
				tmp_type = EnvironmentType.valueOf(env_level);
			} catch (Exception e) {
				logger.severe(String.format("Invalid Environment type %s using default type %s", env_level, tmp_type));
			}
		}

		logger.config(String.format("Environment type set to %s", tmp_type));

		if (tmp_type.equals(EnvironmentType.DEV)) {

			String operating_system_property = System.getProperty("os.name");

			if (operating_system_property != null) {
				String os = operating_system_property.toLowerCase();
				if (os.indexOf("win") < 0 && os.indexOf("mac") < 0) {
					logger.severe(String.format("Unexpected operating system (%s) detected for %s environment! This is probabaly because the environment variable %s was not set correctly.", os, tmp_type, ENV_TYPE_VARIABLE_NAME));
				}
			} else {
				logger.severe("Failed to detect operating system!");
			}

		}

		ApplicationId tmp_application_id = default_id;
		try {
			tmp_application_id = new ApplicationId(System.getProperty(ENV_APPLICATION_ID));
		} catch (Exception e) {
			if (tmp_application_id == null) {
				logger.severe("Failed to set the application id! Terminating the JVM...");
				System.exit(1);
			}
		}
		logger.config(String.format("Application Id set to %s", tmp_application_id));

		CURRENT = new CloudExecutionEnvironment(tmp_type, tmp_application_id);

	}

	/**
	 * MAKE SURE YOU CALLED STARTUP ONCE BEFORE
	 * 
	 * Make sure your application calls startup first to setup the singleton. If
	 * CURRENT is not set the JVM will be terminated!
	 * 
	 * @return the cloud execution environment
	 */
	static public CloudExecutionEnvironment getSimpleCurrent()
	{
		if (CURRENT == null) {
			logger.severe("The startup mathod was never called first to setup the singleton! Terminating the JVM...");
			System.exit(1);
		}
		return CURRENT;
	}

}
