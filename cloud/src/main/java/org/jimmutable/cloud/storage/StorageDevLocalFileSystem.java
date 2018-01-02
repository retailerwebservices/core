package org.jimmutable.cloud.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Builder;
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
	public boolean exists(StorageKey key, boolean default_value)
	{
		File f = new File(root + "/" + key.toString());
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
	public boolean upsert(StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible)
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
			String path = root.getAbsolutePath() + "/" + key.toString();
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(bytes);
			fos.close();
			return Arrays.equals(bytes, getCurrentVersion(key, null));
		} catch (Exception e)
		{
			File f = new File(root.getAbsolutePath() + "/" + key.toString());
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
	public byte[] getCurrentVersion(StorageKey key, byte[] default_value)
	{
		Validator.notNull(key);
		if (exists(key, false))
		{
			File file = new File(root.getAbsolutePath() + "/" + key.toString());
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
	public boolean delete(StorageKey key)
	{
		if (isReadOnly())
		{
			return false;
		}
		Validator.notNull(key);
		try
		{
			File f = new File(root.getAbsolutePath() + "/" + key.toString());
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
	public Iterable<StorageKey> listComplex(Kind kind, Iterable<StorageKey> default_value)
	{
		return (Iterable<StorageKey>) listComplexIter(kind, null, default_value, false);
	}
	
	/**
	 * @param kind
	 *            The kind of the storable object you are looking for
	 * @param prefix
	 * 			The prefix to filter against
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 * @Override
	 */
	public Iterable<StorageKey> listComplex(Kind kind, StorageKeyName prefix, Iterable<StorageKey> default_value)
	{
		return (Iterable<StorageKey>) listComplexIter(kind, prefix, default_value, false);
	}
	
	/**
	 * @param StorageKey
	 *            The prefix of the storable object you are looking for
	 * @param default_value
	 *            the value you want returned if nothing is found.
	 * @return If any StorageKeys were found, that Collection of objects will be
	 *         returned, Otherwise the Default_value that was passed in.
	 */
	@Override
	public Iterable<ObjectIdStorageKey> listAllObjectIdsComplex(Kind kind, Iterable<ObjectIdStorageKey> default_value)
	{
		return (Iterable<ObjectIdStorageKey>) listComplexIter(kind, null, default_value, true);
	}

	/**
	 * Private method that lists all files from the local env
	 * @param kind
	 * @param prefix
	 * @param default_value
	 * @param only_object_ids
	 * @return
	 */
	private Iterable<? extends StorageKey> listComplexIter(Kind kind, StorageKeyName prefix, Iterable<? extends StorageKey> default_value, boolean only_object_ids)
	{
		Validator.notNull(kind);
		File folder = new File(root.getAbsolutePath() + "/" + kind.getSimpleValue());
		File[] list_of_files = folder.listFiles();
		List<StorageKey> keys = new ArrayList<>();
		for (int i = 0; i < list_of_files.length; i++)
		{
			if (list_of_files[i].isFile())
			{
				String[] file_name_and_ext = list_of_files[i].getName().split("\\."); 

				if (file_name_and_ext.length < 2)
				{
					System.err.println("listComplex error with file: " + list_of_files[i]);
					continue;
				}

				StorageKeyName name = new StorageKeyName(file_name_and_ext[0]);

				if (prefix != null)
				{
					if ( !name.getSimpleValue().startsWith(prefix.getSimpleValue()) )
					{
						continue;
					}
				}

				String key = kind + "/" + list_of_files[i].getName();

				if (name.isObjectId())
				{
					keys.add(new ObjectIdStorageKey(key));
				}
				else
				{
					if (!only_object_ids)
					{
						keys.add(new GenericStorageKey(key));
					}
				}
			}
		}
		
		return keys;
	}

	/**
	 * Retrieves the ObjectMetadata for this key param. For StorageDevLocalFileSystem, the last modified is checked from disk every time this method is called.
	 * Further, the etag is simply the last modified timestamp on the file as well.
	 * 
	 * @Override
	 */
	public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value)
	{
		File f = new File(root + "/" + key.toString());
		if ( !f.exists() || f.isDirectory() )
		{
			return default_value;
		}
		
		long last_modified = f.lastModified();
		long size = f.length();

		Builder builder = new Builder(StorageMetadata.TYPE_NAME);
		builder.set(StorageMetadata.FIELD_LAST_MODIFIED, last_modified);
		builder.set(StorageMetadata.FIELD_SIZE, size);

		return (StorageMetadata) builder.create(null);
	}
}
