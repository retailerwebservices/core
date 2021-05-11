package org.jimmutable.cloud.elasticsearch;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.common.Kind;

/**
 * This class is used to re-index a single Storable & Indexable Kind by doing
 * the following for the Kind passed in:
 * 
 * 1.) Go through every Object in Storage and upsert its matching search
 * document
 * 
 * @author avery.gonzales
 */
public class SyncSingleKind implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(SyncSingleKind.class);
	private static final NumberFormat TIME_FORMATTER = new DecimalFormat("#0.00000");

	private Kind kind; // Required and must be both Indexable and Storable for any actions to be taken

	public SyncSingleKind( Kind kind ) throws ValidationException
	{
		this.kind = kind;
		validate();
	}

	private void validate()
	{
		if ( this.kind == null )
			throw new ValidationException("No Kind was passed in, kind is required");
	}

	/**
	 * This will kick off the process of syncing the Kind's Storage with Search
	 */
	@Override
	public void run()
	{
		long start = System.currentTimeMillis();
		logger.info("Reindexing of Kind " + kind + " started");
		boolean success = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().reindex(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage(), kind);
		long end = System.currentTimeMillis();
		if ( !success )
		{
			logger.error("Failed to completetly reindex kind \"{}\". Execution time was {} seconds", kind, TIME_FORMATTER.format((end - start) / 1000d));
		}
		else
		{
			logger.info("Successfully reindexed kind \"{}\". Execution time was {} seconds", kind, TIME_FORMATTER.format((end - start) / 1000d));
		}
	}
}
