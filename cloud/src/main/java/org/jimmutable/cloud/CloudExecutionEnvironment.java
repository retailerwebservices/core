package org.jimmutable.cloud;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jimmutable.cloud.cache.CacheRedis;
import org.jimmutable.cloud.cache.CacheStub;
import org.jimmutable.cloud.cache.ICache;
import org.jimmutable.cloud.cache.redis.LowLevelRedisDriver;
import org.jimmutable.cloud.elasticsearch.ElasticSearchEndpoint;
import org.jimmutable.cloud.elasticsearch.ElasticSearchRESTClient;
import org.jimmutable.cloud.elasticsearch.ISearch;
import org.jimmutable.cloud.elasticsearch.StubSearch;
import org.jimmutable.cloud.email.EmailStub;
import org.jimmutable.cloud.email.IEmail;
import org.jimmutable.cloud.email.SESClient;
import org.jimmutable.cloud.logging.Log4jUtil;
import org.jimmutable.cloud.messaging.queue.IQueue;
import org.jimmutable.cloud.messaging.queue.QueueRedis;
import org.jimmutable.cloud.messaging.queue.QueueStub;
import org.jimmutable.cloud.messaging.signal.ISignal;
import org.jimmutable.cloud.messaging.signal.SignalRedis;
import org.jimmutable.cloud.messaging.signal.SignalStub;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.StandardImmutableObjectCache;
import org.jimmutable.cloud.storage.StorageDevLocalFileSystem;
import org.jimmutable.cloud.storage.StubStorage;
import org.jimmutable.cloud.storage.s3.RegionSpecificAmazonS3ClientFactory;
import org.jimmutable.cloud.storage.s3.StorageS3;
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
	private static Logger logger = LogManager.getLogger(CloudExecutionEnvironment.class);
	private static CloudExecutionEnvironment CURRENT;

	private ISearch search;
	private IStorage storage;
	private IQueue queue_service;
	private ISignal signal_service;
	private IEmail email_service;
	private ICache cache_service;

	// System properties
	private static final String ENV_TYPE_VARIABLE_NAME = "JIMMUTABLE_ENV_TYPE";
	private static final String ENV_LOGGING_LEVEL = "JIMMUTABLE_LOGGING_LEVEL";

	private static final Level DEFAULT_LEVEL = Level.INFO;

	private static EnvironmentType ENV_TYPE;
	private static ApplicationId APPLICATION_ID;
	private static StandardImmutableObjectCache STANDARD_IMMUTABLE_OBJECT_CACHE;
	public static final String STAGING_POST_FIX = "-staging";

	private CloudExecutionEnvironment(ISearch search, IStorage storage, IQueue queue_service, ISignal signal_service, IEmail email_service, ICache cache_service)
	{
		this.search = search;
		this.storage = storage;
		this.queue_service = queue_service;
		this.signal_service = signal_service;
		this.email_service = email_service;
		this.cache_service = cache_service;
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

	public IQueue getSimpleQueueService()
	{
		return queue_service;
	}

	public ISignal getSimpleSignalService()
	{
		return signal_service;
	}

	public IEmail getSimpleEmailService()
	{
		return email_service;
	}
	
	public ICache getSimpleCacheService()
	{
		return cache_service;
	}

	private static SESClient getSESClient()
	{
		try
		{
			return new SESClient(SESClient.getClient());
		} catch (Exception e)
		{
			logger.error("Failed to created email client!", e);
			throw new RuntimeException("Failed to created email client!");
		}
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
		CacheRedis redis = new CacheRedis(APPLICATION_ID, new LowLevelRedisDriver());
		STANDARD_IMMUTABLE_OBJECT_CACHE = new StandardImmutableObjectCache(redis, "storagecache");
		switch (env_type)
		{
		case DEV:

			// setup the logging level programmatically in dev and staging
			Level dev_level = Level.toLevel(System.getProperty(ENV_LOGGING_LEVEL), DEFAULT_LEVEL);
			Log4jUtil.setAllLoggerLevels(dev_level);
			logger.trace(String.format("Logging level: %s", dev_level));

			checkOs();

			TransportClient dev_client = null;
			try
			{
				dev_client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName(ElasticSearchEndpoint.CURRENT.getSimpleHost()), ElasticSearchEndpoint.CURRENT.getSimplePort()));
			}
			catch ( UnknownHostException e )
			{
				logger.log(Level.FATAL, "Failed to instantiate the elasticsearch client!", e);
			}

			if (dev_client == null)
			{
				throw new RuntimeException("Failed to instantiate the elasticsearch client!");
			}

			CURRENT = new CloudExecutionEnvironment(new ElasticSearchRESTClient(), new StorageDevLocalFileSystem(false, APPLICATION_ID, STANDARD_IMMUTABLE_OBJECT_CACHE), new QueueRedis(APPLICATION_ID), new SignalRedis(APPLICATION_ID), getSESClient(),redis);

			break;
		// For now, staging is the same as dev
		case STAGING:
			// setup the logging level programmatically in dev and staging
			Level staging_level = Level.toLevel(System.getProperty(ENV_LOGGING_LEVEL), DEFAULT_LEVEL);
			Log4jUtil.setAllLoggerLevels(staging_level);
			logger.trace(String.format("Logging level: %s", staging_level));

			checkOs();

			TransportClient staging_client = null;
			try
			{
				staging_client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName(ElasticSearchEndpoint.CURRENT.getSimpleHost()), ElasticSearchEndpoint.CURRENT.getSimplePort()));
			}
			catch ( UnknownHostException e )
			{
				logger.log(Level.FATAL, "Failed to instantiate the elasticsearch client!", e);
			}

			if (staging_client == null)
			{
				throw new RuntimeException("Failed to instantiate the elasticsearch client!");
			}
			/*
			 * For our staging mode we still use S3, but we use a different bucket that is
			 * meant to be synced up nightly
			 */
			ApplicationId staging_application_id = createStagingApplicationIDComplex(APPLICATION_ID, null);
			if(staging_application_id == null)
			{
				throw new RuntimeException("Failed to create staging application ID for Storage!");
			}
			
			StorageS3 staging_storage = new StorageS3(RegionSpecificAmazonS3ClientFactory.defaultFactory(), staging_application_id, STANDARD_IMMUTABLE_OBJECT_CACHE, false);
			staging_storage.upsertBucketIfNeeded();
			
			CURRENT = new CloudExecutionEnvironment(new ElasticSearchRESTClient(), staging_storage, new QueueRedis(APPLICATION_ID), new SignalRedis(APPLICATION_ID), getSESClient(),redis);

			break;

		case PRODUCTION:
			checkOs();
			Level prod_level = Level.toLevel(System.getProperty(ENV_LOGGING_LEVEL), DEFAULT_LEVEL);
			Log4jUtil.setAllLoggerLevels(prod_level);
			logger.trace(String.format("Logging level: %s", prod_level));

			logger.log(Level.INFO, "Starting production environment");

			StorageS3 production_storage = new StorageS3(RegionSpecificAmazonS3ClientFactory.defaultFactory(), APPLICATION_ID, STANDARD_IMMUTABLE_OBJECT_CACHE, false);
			production_storage.upsertBucketIfNeeded();

			CURRENT = new CloudExecutionEnvironment(new ElasticSearchRESTClient(), production_storage, new QueueRedis(APPLICATION_ID), new SignalRedis(APPLICATION_ID), getSESClient(), new CacheRedis(APPLICATION_ID, new LowLevelRedisDriver()));
			break;
		case STUB:

			checkOs();
			CURRENT = new CloudExecutionEnvironment(new StubSearch(), new StubStorage(), new QueueStub(), new SignalStub(), new EmailStub(), new CacheStub());
			break;

		default:

			throw new RuntimeException(String.format("Unhandled EnvironmentType: %s! Add the environment to startup to handle it correctly.", env_type));

		}
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();
		Log4jUtil.setupListeners();
		STANDARD_IMMUTABLE_OBJECT_CACHE.createListeners();

	}
	

	/**
	 * This creates a staging specific application ID for Storage.
	 */
	public static ApplicationId createStagingApplicationIDComplex(ApplicationId application_id, ApplicationId default_value)
	{
		try
		{
			return new ApplicationId(application_id.getSimpleValue() + STAGING_POST_FIX);
		}
		catch ( Exception e )
		{
			logger.fatal(String.format("Could not create staging post fix for application ID %s. Dying now.", application_id.getSimpleValue()), e);
			return default_value;
		}
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
		if (env_level == null)
			env_level = System.getProperty(ENV_TYPE_VARIABLE_NAME.toLowerCase());

		if (env_level != null)
		{
			EnvironmentType tmp_type = null;
			try
			{
				env_level = env_level.toUpperCase();
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

	public StandardImmutableObjectCache getSimpleCache()
	{
		return STANDARD_IMMUTABLE_OBJECT_CACHE;
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
			throw new RuntimeException("The startup method was never called first to setup the singleton!");
		}
		return CURRENT;
	}
}
