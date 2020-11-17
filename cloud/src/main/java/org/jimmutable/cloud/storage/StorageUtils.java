package org.jimmutable.cloud.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.utils.Validator;

public class StorageUtils
{
	static private final Logger logger = LoggerFactory.getLogger(StorageUtils.class);
	static private final String CHARSET_NAME = "UTF8";

	public static boolean doesExist( Storable object, boolean default_value )
	{
		return CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().exists(object, default_value);
	}

	public static boolean doesExist( StorageKey key, boolean default_value )
	{
		return CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().exists(key, default_value);
	}

	public static boolean doesExist( Kind kind, ObjectId id, boolean default_value )
	{
		return doesExist(new ObjectIdStorageKey(kind, id, StorageKeyExtension.JSON), default_value);
	}

	/**
	 * Generic method to take the error handling out of hand when trying to get an
	 * object out of storage. If anything fails on retrieving from storage this will
	 * return the default_value.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends StandardObject<T>> T getOptional( Kind kind, ObjectId id, T default_value )
	{

		try
		{
			Validator.notNull(kind, "Kind");
			Validator.notNull(id, "Id");
		}
		catch ( ValidationException e )
		{
			logger.error(String.format("A required field was null for Kind:%s Id:%s!", kind, id), e);
			return default_value;
		}

		try
		{

			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(kind, id, StorageKeyExtension.JSON), null);

			if ( bytes == null )
			{

				logger.error(String.format("No bytes returned from storage for Kind:%s Id:%s!", kind, id));
				return default_value;
			}

			return (T) StandardObject.deserialize(new String(bytes, CHARSET_NAME));
		}
		catch ( Exception e )
		{
			logger.error(String.format("Failed to deserialize from storage for Kind:%s Id:%s!", kind, id), e);
			return default_value;
		}

	}

	/**
	 * Writes an object to a temporary file and returns the file
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	public static File writeToTempFile( StorageKey key, File default_value )
	{
		try
		{
			Validator.notNull(key, "Storage Key");
		}
		catch ( Exception e )
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
			if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersionStreaming(key, fos) )
			{
				logger.info(String.format("Created temporary file %s", tmp_file.getAbsolutePath()));
				return tmp_file;
			}
		}
		catch ( IOException e )
		{
			logger.error(String.format("Failed to write %s.%s to temp file!", prefix, suffix), e);
		}
		finally
		{
			if ( fos != null )
			{
				try
				{
					fos.close();
				}
				catch ( IOException e )
				{
					logger.error(String.format("Failed to close FileOutputStream while writing %s.%s to temp file!", prefix, suffix), e);
				}
			}
		}

		return default_value;
	}

}
