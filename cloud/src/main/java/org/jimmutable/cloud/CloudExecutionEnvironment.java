package org.jimmutable.cloud;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jimmutable.cloud.elasticsearch.ElasticSearchEndpoint;
import org.jimmutable.cloud.elasticsearch.Search;
import org.jimmutable.cloud.elasticsearch.SearchIndexConfigurationUtils;
import org.jimmutable.cloud.logging.Log4jUtil;

/**
 * Configures environment and application specific setting, to be used by other
 * classes. This includes EnvironmantType, ApplicationId, and logging settings.
 * 
 * @author trevorbox
 *
 */
@SuppressWarnings("resource")
public class CloudExecutionEnvironment
{
	private static Logger logger;
	private static CloudExecutionEnvironment CURRENT;

	private EnvironmentType type;
	private ApplicationId application_id;
	private Search search;
	private SearchIndexConfigurationUtils searchIndexConfigurationUtils;

	// centrally managed client used by a couple classes
	private TransportClient elasticsearchClient;

	// System properties
	private static final String ENV_TYPE_VARIABLE_NAME = "JIMMUTABLE_ENV_TYPE";
	private static final String ENV_APPLICATION_ID = "JIMMUTABLE_APP_ID";
	private static final String ENV_LOGGING_LEVEL = "JIMMUTABLE_LOGGING_LEVEL";

	private static final Level DEFAULT_LEVEL = Level.INFO;

	// setup the logging level programmatically
	static {
		Level level = Level.toLevel(System.getProperty(ENV_LOGGING_LEVEL), DEFAULT_LEVEL);
		Log4jUtil.setAllLoggerLevels(level);
		logger = LogManager.getLogger(CloudExecutionEnvironment.class);
		logger.trace(String.format("Logging level: %s", level));
	}

	private CloudExecutionEnvironment(EnvironmentType type, ApplicationId application_id, TransportClient elasticsearchClient, Search search, SearchIndexConfigurationUtils searchIndexConfigurationUtils)
	{
		this.type = type;
		this.application_id = application_id;
		this.elasticsearchClient = elasticsearchClient;
		this.search = search;
		this.searchIndexConfigurationUtils = searchIndexConfigurationUtils;
	}

	public void closeElasticSearchClient()
	{
		elasticsearchClient.close();
	}

	public TransportClient getSimpleElasticsearchClient()
	{
		return elasticsearchClient;
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
	 * Search instance used for document upsert and searching of indices
	 * 
	 * @return
	 */
	public Search getSimpleSearch()
	{
		return search;
	}

	/**
	 * Search index utility used for index upsert and maintenance
	 * 
	 * @return
	 */
	public SearchIndexConfigurationUtils getSimpleSearchIndexConfigurationUtils()
	{
		return searchIndexConfigurationUtils;
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
	public static void startup(ApplicationId default_id)
	{
		if (CURRENT != null) {
			logger.fatal("Startup has already been called!");
			throw new RuntimeException("Startup has already been called!");
		}
		// default
		EnvironmentType tmp_type = EnvironmentType.DEV;

		String env_level = System.getProperty(ENV_TYPE_VARIABLE_NAME);
		if (env_level != null) {
			try {
				tmp_type = EnvironmentType.valueOf(env_level);
			} catch (Exception e) {
				logger.fatal(String.format("Invalid Environment type %s using default type %s", env_level, tmp_type));
			}
		}

		logger.trace(String.format("Environment type set to %s", tmp_type));

		if (tmp_type.equals(EnvironmentType.DEV)) {

			String operating_system_property = System.getProperty("os.name");

			if (operating_system_property != null) {
				String os = operating_system_property.toLowerCase();
				if (os.indexOf("win") < 0 && os.indexOf("mac") < 0) {
					logger.fatal(String.format("Unexpected operating system (%s) detected for %s environment! This is probabaly because the environment variable %s was not set correctly.", os, tmp_type, ENV_TYPE_VARIABLE_NAME));
				}
			} else {
				logger.fatal("Failed to detect operating system!");
			}

		}

		ApplicationId tmp_application_id = default_id;
		try {
			tmp_application_id = new ApplicationId(System.getProperty(ENV_APPLICATION_ID));
		} catch (Exception e) {
			if (tmp_application_id == null) {
				logger.fatal("Failed to set the application id! Terminating the JVM...");
				System.exit(1);
			}
		}
		logger.trace(String.format("Application Id set to %s", tmp_application_id));

		TransportClient client = null;
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ElasticSearchEndpoint.CURRENT.getSimpleHost()), ElasticSearchEndpoint.CURRENT.getSimplePort()));
		} catch (UnknownHostException e) {
			logger.log(Level.FATAL, "Failed to instantiate the elasticsearch client!", e);
		}

		if (client == null) {
			throw new RuntimeException("Failed to instantiate the elasticsearch client!");
		}

		CURRENT = new CloudExecutionEnvironment(tmp_type, tmp_application_id, client, new Search(client), new SearchIndexConfigurationUtils(client));

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
			logger.fatal("The startup mathod was never called first to setup the singleton! Terminating the JVM...");
			System.exit(1);
		}
		return CURRENT;
	}

}
