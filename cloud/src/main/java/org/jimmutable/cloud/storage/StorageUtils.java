package org.jimmutable.cloud.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;

public class StorageUtils
{
	static private final Logger logger = LogManager.getLogger(StorageUtils.class);
	static private final String CHARSET_NAME = "UTF8";

	/**
	 * Generic method to take the error handling out of hand when trying to get an
	 * object out of storage. If anything fails on retrieving from storage this will
	 * return the default_value.
	 */
	public static StandardObject<?> getOptionalFromStorage(Kind kind, ObjectId id, StandardObject<?> default_value)
	{
		if (kind == null)
		{
			// This will allow us to know if id was also null
			logger.error("Could not retrieve StandardObject for id " + id + " because Kind was null");
			return default_value;
		}
		if (id == null)
		{
			logger.error("Could not retrieve StandardObject for Kind " + kind + " because id was null");
			return default_value;
		}

		StandardObject<?> obj = null;
		try
		{
			obj = StandardObject.deserialize(new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(kind, id, StorageKeyExtension.JSON), null), CHARSET_NAME));
			if (obj == null)
			{
				return default_value;
			}
		} catch (Exception e)
		{
			logger.error("Could not retrieve StandardObject " + id + " of Kind " + kind + " from Storage", e);
			return default_value;
		}
		return obj;
	}

	/**
	 * Writes an object to a temporary file and returns the file
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static File writeToTempFile(StorageKey key, File default_value)
	{
		try
		{
			Validator.notNull(key, "Storage Key");
		} catch (Exception e)
		{
			logger.error("Null Storage Key");
			return default_value;
		}

		String prefix = key.getSimpleKind().getSimpleValue();
		String suffix = key.getSimpleExtension().getSimpleValue();

		File tmp_file = null;
		FileOutputStream fos = null;
		try
		{
			tmp_file = File.createTempFile(String.format("%s-", prefix), String.format(".%s", suffix));
			tmp_file.deleteOnExit();

			fos = new FileOutputStream(tmp_file);
			if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersionStreaming(key, fos))
			{
				logger.info(String.format("Created temporary file %s", tmp_file.getAbsolutePath()));
				return tmp_file;
			}
		} catch (IOException e)
		{
			logger.error(String.format("Failed to write %s.%s to temp file!", prefix, suffix), e);
		} finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				} catch (IOException e)
				{
					logger.error(String.format("Failed to close FileOutputStream while writing %s.%s to temp file!", prefix, suffix), e);
				}
			}
		}

		return default_value;
	}

}
