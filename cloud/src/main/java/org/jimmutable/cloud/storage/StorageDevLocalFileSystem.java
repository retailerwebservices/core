package org.jimmutable.cloud.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.IOUtils;
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

	// TODO Use hint_content_likely_to_be_compressible to auto-gzip contents. Must
	// be able to detect dynamically on read.
	@Override
	public boolean upsertStreaming(final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible)
	{
		Validator.notNull(key, source);

		if (isReadOnly())
			return false;

		final File dest_file = new File(root.getAbsolutePath() + "/" + key.toString());
		dest_file.getParentFile().mkdirs(); // Make sure the directories exist

		try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(dest_file)))
		{
			IOUtils.transferAllBytes(source, fout);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean getCurrentVersionStreaming(final StorageKey key, final OutputStream sink)
	{
		Validator.notNull(key);

		final File source_file = new File(root.getAbsolutePath() + "/" + key.toString());
		if (!source_file.exists())
			return false;

		try (InputStream fin = new BufferedInputStream(new FileInputStream(source_file)))
		{
			IOUtils.transferAllBytes(fin, sink);
			return true;
		} catch (Exception e)
		{
			return false;
		}
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
	 * WARNING: this will delete EVERYTHING in that is this kind
	 * 
	 * @param kind
	 *            Kind associated with StorageObjects
	 * @return true if the Kind was deleted, false otherwise
	 */

	public boolean deleteKind(Kind kind)
	{
		if (isReadOnly())
		{
			return false;
		}
		Validator.notNull(kind);
		try
		{
			File f = new File(root.getAbsolutePath() + "/" + kind.getSimpleValue() + "/");
			FileUtils.deleteDirectory(f);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Retrieves the ObjectMetadata for this key param. For
	 * StorageDevLocalFileSystem, the last modified is checked from disk every time
	 * this method is called. Further, the etag is simply the last modified
	 * timestamp on the file as well.
	 * 
	 * @Override
	 */
	public StorageMetadata getObjectMetadata(StorageKey key, StorageMetadata default_value)
	{
		File f = new File(root + "/" + key.toString());
		if (!f.exists() || f.isDirectory())
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

	/**
	 * This class does the main listing operation for scan*. It runs in it's own
	 * thread and throws each StorageKey it finds into another OperationRunnable
	 * running in a common pool.
	 *
	 * @author Jeff Dezso
	 */
	private class Scanner extends Storage.Scanner
	{
		public Scanner(final Kind kind, final StorageKeyName prefix, final boolean only_object_ids)
		{
			super(kind, prefix, only_object_ids);
		}

		@Override
		protected Result performOperation() throws Exception
		{
			final File folder = new File(root.getAbsolutePath() + "/" + getSimpleKind().getSimpleValue());

			Files.walkFileTree(folder.toPath(), EnumSet.noneOf(FileVisitOption.class), 1, new Walker());

			return shouldStop() ? Result.STOPPED : Result.SUCCESS;
		}

		private class Walker extends SimpleFileVisitor<Path>
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				if (shouldStop())
					return FileVisitResult.TERMINATE;

				Validator.notNull(file);
				Validator.notNull(attrs);

				String[] file_name_and_ext = file.getFileName().toString().split("\\.");

				if (file_name_and_ext.length < 2)
				{
					System.err.println("Scanner error with file: " + file);
					return FileVisitResult.CONTINUE;
				}

				StorageKeyName name = new StorageKeyName(file_name_and_ext[0]);

				if (hasPrefix())
				{
					if (!name.getSimpleValue().startsWith(getOptionalPrefix(null).getSimpleValue()))
					{
						return FileVisitResult.CONTINUE;
					}
				}

				String key = getSimpleKind() + "/" + file.getFileName();

				if (name.isObjectId())
				{
					emit(new ObjectIdStorageKey(key));
				} else
				{
					if (!onlyObjectIds())
					{
						emit(new GenericStorageKey(key));
					}
				}

				return FileVisitResult.CONTINUE;
			}
		}
	}

	@Override
	protected Storage.Scanner createScanner(Kind kind, StorageKeyName prefix, boolean only_object_ids)
	{
		return new Scanner(kind, prefix, only_object_ids);
	}
}
