package org.jimmutable.cloud.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.storage.s3.StorageS3;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public class StorageUtils
{
	static private final Logger LOGGER = LogManager.getLogger(StorageUtils.class);
	static private final String CHARSET_NAME = "UTF8";
	
	/**
	 * Generic method to take the error handling out of hand when trying to get an
	 * object out of storage. If anything fails on retrieving from storage this will
	 * return the default_value.
	 */
	private static StandardObject<?> getOptionalFromStorage( Kind kind, ObjectId id, StandardObject<?> default_value )
	{
		StandardObject<?> obj = null;
		try
		{
			obj = StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(kind, id, StorageKeyExtension.JSON), null), CHARSET_NAME));
			if ( obj == null )
			{
				return default_value;
			}
		}
		catch ( Exception e )
		{
			LOGGER.error("Could not retrieve StandardObject from Storage", e);
			return default_value;
		}
		return obj;
	}
}
