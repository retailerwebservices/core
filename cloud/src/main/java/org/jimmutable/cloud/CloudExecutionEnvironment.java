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
import org.jimmutable.cloud.elasticsearch.ISearch;
import org.jimmutable.cloud.elasticsearch.StubSearch;
import org.jimmutable.cloud.elasticsearch.ElasticSearch;
import org.jimmutable.cloud.logging.Log4jUtil;
import org.jimmutable.cloud.messaging.IMessaging;

import org.jimmutable.cloud.messaging.StubMessaging;
import org.jimmutable.cloud.messaging.dev_local.MessagingDevLocalFileSystem;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.StorageDevLocalFileSystem;
import org.jimmutable.cloud.storage.StubStorage;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

/**
 * Configures environment and application specific setting, to be used by other
 * classes. This includes EnvironmantType, ApplicationId, and logging settings.
 *
 * @author trevorbox
 *
 */
public class CloudExecutionEnvironment
{
	private static Logger logger;
	private static CloudExecutionEnvironment CURRENT;

	private ISearch search;
	private IStorage storage;
	private IMessaging messaging;

	// System properties
	private static final String ENV_TYPE_VARIABLE_NAME = "JIMMUTABLE_ENV_TYPE";
	private static final String ENV_LOGGING_LEVEL = "JIMMUTABLE_LOGGING_LEVEL";

	private static final Level DEFAULT_LEVEL = Level.INFO;

	private static EnvironmentType ENV_TYPE;
	private static ApplicationId APPLICATION_ID;

	// setup the logging level programmatically
	static
	{
		Level level = Level.toLevel(System.getProperty(ENV_LOGGING_LEVEL), DEFAULT_LEVEL);
		Log4jUtil.setAllLoggerLevels(level);
		logger = LogManager.getLogger(CloudExecutionEnvironment.class);
		logger.trace(String.format("Logging level: %s", level));
	}

	private CloudExecutionEnvironment(ISearch search, IStorage storage, IMessaging messaging)
	{

		this.search = search;
		this.storage = storage;
		this.messaging = messaging;

	}

	public EnvironmentType getSimpleEnvironmentType()
	{
		return ENV_TYPE;
	}

	public ApplicationId getSimpleApplicationId()
	{
		return APPLICATION_ID;
	}

	/**
	 * Search instance used for document upsert and searching of indices
	 *
	 * @return The Search instance
	 */
	public ISearch getSimpleSearch()
	{
		return search;
	}

	/**
	 * Storage system the application uses to store objects
	 *
	 * @return Storage The storage system
	 */
	public IStorage getSimpleStorage()
	{
		return storage;
	}

	/**
	 * Messaging system the application uses to send messages (pub/sub)
	 *
	 * @return
	 */
	public IMessaging getSimpleMessaging()
	{
		return messaging;
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
	@SuppressWarnings("resource")
	public static void startup(ApplicationId application_id, EnvironmentType env_type)
	{

		if (CURRENT != null)
		{
			throw new RuntimeException("Startup has already been called!");
		}

		// register objects


		ENV_TYPE = env_type;
		APPLICATION_ID = application_id;

		logger.info(String.format("ApplicationID=%s Environment=%s", APPLICATION_ID, ENV_TYPE));

		switch (env_type)
		{
		case DEV:

			checkOs();

			TransportClient client = null;
			try
			{
				client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ElasticSearchEndpoint.CURRENT.getSimpleHost()), ElasticSearchEndpoint.CURRENT.getSimplePort()));
			} catch (UnknownHostException e)
			{
				logger.log(Level.FATAL, "Failed to instantiate the elasticsearch client!", e);
			}

			if (client == null)
			{
				throw new RuntimeException("Failed to instantiate the elasticsearch client!");
			}

			CURRENT = new CloudExecutionEnvironment(new ElasticSearch(client), new StorageDevLocalFileSystem(false, APPLICATION_ID), new MessagingDevLocalFileSystem());

			break;
		case STUB:

			checkOs();
			CURRENT = new CloudExecutionEnvironment(new StubSearch(), new StubStorage(), new StubMessaging());
			break;

		default:

			throw new RuntimeException(String.format("Unhandled EnvironmentType: %s! Add the environment to startup to handle it correctly.", env_type));

		}
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();

		// register objects
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();

	}
	

	/**
	 * Use this for running unit tests. Search, Storage, and Messaging are just stub
	 * classes and throw Runtime Exceptions if their methods are called.
	 *
	 * @param application_id
	 *            The ApplicationId
	 */
	public static void startupStubTest(ApplicationId application_id)
	{
		startup(application_id, EnvironmentType.STUB);
	}

	/**
	 * Use this for running integration tests. Search, Storage, and Messaging are
	 * initialized to the Dev instances. Any local clients needed for Search,
	 * Storage or Messaging should be running.
	 *
	 * @param application_id
	 *            The ApplicationId
	 */
	public static void startupIntegrationTest(ApplicationId application_id)
	{
		startup(application_id, EnvironmentType.DEV);
	}

	/**
	 * Get the environment type from the system property JIMMUTABLE_ENV_TYPE. If it
	 * fails the default value is returned.
	 *
	 * @param default_value
	 * @return The EnvironmentType from the system variable or the default_value
	 */
	public static EnvironmentType getEnvironmentTypeFromSystemProperty(EnvironmentType default_value)
	{

		String env_level = System.getProperty(ENV_TYPE_VARIABLE_NAME);
		if (env_level != null)
		{
			EnvironmentType tmp_type = null;
			try
			{
				tmp_type = EnvironmentType.valueOf(env_level);
			} catch (Exception e)
			{
				logger.fatal(String.format("Invalid Environment type %s using default type %s", env_level, tmp_type));
				return default_value;
			}

			return tmp_type;

		}
		return default_value;
	}

	private static void checkOs()
	{
		String operating_system_property = System.getProperty("os.name");

		if (operating_system_property != null)
		{
			String os = operating_system_property.toLowerCase();
			if (os.indexOf("win") < 0 && os.indexOf("mac") < 0)
			{
				logger.fatal(String.format("Unexpected operating system (%s) detected for %s environment!", os, ENV_TYPE));
			}
		} else
		{
			logger.fatal("Failed to detect operating system!");
		}
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
		if (CURRENT == null)
		{
			throw new RuntimeException("The startup mathod was never called first to setup the singleton!");
		}
		return CURRENT;
	}

}
