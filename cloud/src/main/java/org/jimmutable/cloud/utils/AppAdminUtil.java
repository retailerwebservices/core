package org.jimmutable.cloud.utils;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchSync;
import org.jimmutable.core.objects.common.Kind;

public class AppAdminUtil
{
	private static final Logger logger = LogManager.getLogger(AppAdminUtil.class);

	public static boolean indicesProperlyConfigured()
	{

		boolean startup_allowed = true;

		logger.info("Validating all search indices...");

		for (Map.Entry<Kind, SearchIndexDefinition> entry : SearchSync.getSimpleAllRegisteredIndexableKindsMap().entrySet())
		{

			SearchIndexDefinition index = entry.getValue();
			// index doesn't exist
			if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(index))
			{
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(index);
				continue;
			}

			// index is not properly configured
			if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(index))
			{
				// put all field mappings
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().putAllFieldMappings(index);

				// if its still not configured correctly add it to error list
				if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(index))
				{

					logger.fatal(String.format("The index %s is not properly configured! You must rebuild or recreate this index using the reindex utility. The app will not start until all indices are properly mapped.", index.getSimpleIndex().getSimpleValue()));
					startup_allowed = false;
				}
			}

		}

		return startup_allowed;
	}

}
