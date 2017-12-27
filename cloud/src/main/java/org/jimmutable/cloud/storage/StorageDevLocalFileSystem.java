package org.jimmutable.cloud.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.Validator;

public class StorageDevLocalFileSystem extends Storage
{

	/**
	 * This is our local implementation of our Storage so we can mock up our storage
	 * on our local machine.
	 */
	private File root;

	public StorageDevLocalFileSystem(boolean is_readonly, ApplicationId applicationId)
	{
		super(is_readonly);
		root = new File(System.getProperty("user.home") + "/jimmutable_dev/" + applicationId);
	}

	/**
	 * @param key
	 *            of the Storable object that we are looking for.
	 * @param default_value
	 *            to be returned if object is not found
	 * @return true if object is found, else Default_value
	 */
	@Override
	public boolean exists(ObjectIdStorageKey key, boolean default_value)
	{
		File f = new File(root + "/" + key.getSimpleValue());
		if (f.exists() && !f.isDirectory())
		{
			return true;
		}
		return default_value;
	}

	/**
	 * @param key
	 *            of the Storable Object to Update/Insert
	 * @param bytes
	 *            the contents of the Storable Object
	 * @param hint_content_likely_to_be_compressible
	 * @return true if the Object was updated/inserted, else false
	 */
	@Override
	public boolean upsert(ObjectIdStorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible)
	{
		if (isReadOnly())
		{
			return false;
		}
		Validator.notNull(key, bytes, hint_content_likely_to_be_compressible);
		try
		{
			File pfile = new File(root.getAbsolutePath() + "/" + key.getSimpleKind());
			pfile.mkdirs();
			String path = root.getAbsolutePath() + "/" + key.getSimpleValue();
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(bytes);
			fos.close();
			return Arrays.equals(bytes, getCurrentVersion(key, null));
		} catch (Exception e)
		{
			File f = new File(root.getAbsolutePath() + "/" + key.getSimpleValue());
			f.delete();
			return false;
		}
	}

	/**
	 * @param key
	 *            key associated with Stored Object you want to retrieve the current
	 *            version of.
	 * @param default_value
	 *            If the object is not found, what would you like returned.
	 * @return Byte array of Stored object if Object was found, otherwise
	 *         default_value
	 */
	@Override
	public byte[] getCurrentVersion(ObjectIdStorageKey key, byte[] default_value)
	{
		Validator.notNull(key);
		if (exists(key, false))
		{
			File file = new File(root.getAbsolutePath() + "/" + key.getSimpleValue());
			byte[] bytesArray = new byte[(int) file.length()];
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(file);
				fis.read(bytesArray); // this method modifies bytesArray to contain the information from the file
			} catch (Exception e)
			{
				return default_value;
			} finally
			{
				try
				{
					fis.close();
				} catch (IOException e)
				{
					return default_value;
				}
			}

			return bytesArray;
		}
		return default_value;
	}

	/**
	 * @param key
	 *            StorageKey associated with StorageObject
	 * @return true if Storage Object existed and was deleted, false otherwise
	 */
	@Override
	public boolean delete(ObjectIdStorageKey key)
	{
		if (isReadOnly())
		{
			return false;
		}
		Validator.notNull(key);
		try
		{
			File f = new File(root.getAbsolutePath() + "/" + key.getSimpleValue());
			return f.delete();
		} catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 */
	@Override
	public Iterable<ObjectIdStorageKey> listComplex(Kind kind, Iterable<ObjectIdStorageKey> default_value)
	{
		Validator.notNull(kind);
		File folder = new File(root.getAbsolutePath() + "/" + kind.getSimpleValue());
		File[] listOfFiles = folder.listFiles();
		List<ObjectIdStorageKey> keys = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				String key = kind + "/" + listOfFiles[i].getName();
				keys.add(new ObjectIdStorageKey(key));
			}
		}
		return keys;
	}
	
	// TODO continue here
	/**
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 */
	
	/*
	@Override
	public Iterable<StorageKey> listComplex(StorageKey prefix, Iterable<StorageKey> default_value)
	{
		Validator.notNull(prefix);
		File folder = new File(root.getAbsolutePath() + "/" + kind.getSimpleValue());
		File[] listOfFiles = folder.listFiles();
		List<ObjectIdStorageKey> keys = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				String key = kind + "/" + listOfFiles[i].getName();
				keys.add(new ObjectIdStorageKey(key));
			}
		}
		return keys;
	}
	*/
}
