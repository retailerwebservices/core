package org.jimmutable.cloud.elasticsearch;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.utils.AppAdminUtil;
import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.common.Kind;


/**
 * This class is used to begin the process of re-indexing Kinds in your
 * Environment's Search to match that of what is in Environment's Storage.
 * 
 * It accomplishes this by doing the following. It takes in a set of Kinds that
 * are both Indexable as well as Storable. From there a thread pool is created.
 * In this thread pool, each thread will do two things:
 * 
 * 1.) Go through every Object in Storage and upsert a matching search document.
 * 
 * Once that is complete for all Kinds passed in the start() method will
 * complete.
 * 
 * Since each application that uses Jimmutable Cloud will have a different
 * application ID and TypeNameRegisters, it is required that you extend this
 * class and implement the proper methods to retrieve the right values specific
 * to your application.
 * 
 * @author avery.gonzales
 */
public abstract class SearchSync
{
	private static final Logger logger = LogManager.getLogger(SearchSync.class);
	static private Map<Kind, SearchIndexDefinition> indexable_kinds = new ConcurrentHashMap<>();

	public static final int MAX_REINDEX_COMPLETION_TIME_MINUTES = 120;
	public static final String ALL_REGISTERED_KINDS = "ALL_REGISTERED_KINDS";
	
	
	/**
	 * Constructor assumes we need to setup the environment as well as use only
	 * the kinds that are currently registered
	 */
	public SearchSync()
	{
		
	}

	/**
	 * Once ReindexKinds is created this will start the process of going through
	 * every Kind passed in and re-indexing them. If this is run from a script
	 * it is required that you have the EnvironmentType is set in your system
	 * variables for the script in order for this process to begin.
	 */
	public void start() throws ValidationException
	{
		EnvironmentType environment_type = CloudExecutionEnvironment.getEnvironmentTypeFromSystemProperty(null);
		if (environment_type == null)
		{
			throw new ValidationException("Unable to run script, environment_type is not set in system properties");
		}
		//Need environment type and application id passed in, otherwise the environment won't be setup properly
		CloudExecutionEnvironment.startup(getSimpleApplicationID(), environment_type);
		//Need a method to collect all the possible types
		setupRegisters();
		
		long start = System.currentTimeMillis();
		logger.info("Checking that all indices are properly configured...");

		boolean reindexing_allowed = AppAdminUtil.indicesProperlyConfigured();
		if (!reindexing_allowed)
		{
			logger.fatal("Failed check out properly configured indices.. Exiting now...");
			System.exit(1);
		}
		logger.info("Success checking that all indices are properly configured...");
		logger.info("Reindexing of all Kinds started...");
		//We want a single thread per kind
		int executor_threads = getSimpleKinds().size();
		if(executor_threads <= 0) 
		{
			executor_threads = 1;
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(executor_threads);
		//Log the kinds it is attempting to reindex
		for(Kind kind : getSimpleKinds())
		{
			try
			{
				executor.submit(new SyncSingleKind(kind));
			}
			catch(Exception e)
			{
				logger.error("Unable to run ReindexSingleKind", e);
			}
		}
		executor.shutdown();
		try
		{
			executor.awaitTermination(MAX_REINDEX_COMPLETION_TIME_MINUTES, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			logger.error("ExecutorService termination was interrupted", e);
		}
		
		long end = System.currentTimeMillis();
		logger.info("Reindexing of all Kinds finished");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		logger.info("Reindexing of all Kinds execution time was " + formatter.format((end - start) / 1000d) + " seconds");
	}

	/**
	 * Should call a method that sets up all of your applications types through
	 * ObjectParseTree.registerTypeName and SearchSync.registerIndexableKind
	 */
	public abstract void setupRegisters();
	
	/**
	 * Should return an ApplicationId identical to your applications
	 * ApplicationId when run normally.
	 */
	public abstract ApplicationId getSimpleApplicationID();
	
	private Set<Kind> getSimpleKinds()
	{
		return indexable_kinds.keySet();
	}
	
	/**
	 * The usage of this class is similar to that of ObjectParseTree, anytime
	 * you have a new Storable & Indexable class it should be registered with SearchSync so
	 * that syncing of Search and Storage can be accomplished with the Object.
	 */
	static public <C extends Storable & Indexable> void registerIndexableKind(Class<C> c)
	{
		try
		{
			Kind kind = (Kind)c.getField("KIND").get(null);
			if ( kind == null ) throw new SerializeException("Unable to extract Kind from " + c);
			
			SearchIndexDefinition index_definition = (SearchIndexDefinition)c.getField("INDEX_MAPPING").get(null);
			if ( index_definition == null ) throw new SerializeException("Unable to extract IndexDefinition from " + c);
			
			indexable_kinds.put(kind, index_definition);
		}
		catch(Exception e)
		{
			logger.error(String.format("Unable to register a Kind for %s, could not read static public field KIND or INDEX_DEFINITION.", c.getSimpleName(),c.getSimpleName()), e);
		}
	}
	
	static public Set<Kind> getSimpleAllRegisteredIndexableKinds()
	{
		return indexable_kinds.keySet();
	}
	
	static public Map<Kind, SearchIndexDefinition> getSimpleAllRegisteredIndexableKindsMap()
	{
		return indexable_kinds;
	}
}
