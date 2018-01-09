package org.jimmutable.cloud.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.IOUtils;
import org.jimmutable.core.utils.Validator;

/**
 *
 * @author andrew.towe This class is the parent of our file storage handlers. It
 *         is designed to handle all updates, insertions, checking of existence,
 *         listing of objects, and deletions.
 */

public abstract class Storage implements IStorage
{
    /**
     * The largest {@code byte[]} that can be {@link #upsert(StorageKey, byte[], boolean) upserted}
     * or {@link #getCurrentVersion(StorageKey, byte[]) retrieved}.
     */
    static public final int MAX_TRANSFER_BYTES_IN_MB = 25;
    static public final int MAX_TRANSFER_BYTES_IN_BYTES = MAX_TRANSFER_BYTES_IN_MB * 1024 * 1024;
    
    
	// Storage instance = null;
	private boolean is_readonly = false;

	protected Storage(boolean is_readOnly)
	{
		this.is_readonly = is_readOnly;
	}

	/**
	 * Retrieves the StorageMetadata associated to this Storable object,
	 * returning the default_value if lookup fails. (Object doesn't exist, or internal error)
	 */
	public StorageMetadata getObjectMetadata(Storable obj, StorageMetadata default_value)
	{
		if (obj == null) return default_value;
		
		return getObjectMetadata(obj.createStorageKey(), default_value);
	}
	
	public boolean upsert(Storable obj, Format format)
	{
        Validator.notNull(obj);
        
        if (isReadOnly()) return false;

		return upsert(obj.createStorageKey(), ObjectWriter.serialize(format, obj).getBytes(), true);
	}

    @Override
    public boolean upsert(final StorageKey key, final byte[] bytes, final boolean hint_content_likely_to_be_compressible)
    {
        Validator.max(bytes.length, MAX_TRANSFER_BYTES_IN_BYTES);
        
        if (isReadOnly()) return false;

        return upsert(key, new ByteArrayInputStream(bytes), hint_content_likely_to_be_compressible);
    }

    @Override
    @SuppressWarnings("resource")
    public byte[] getCurrentVersion(final StorageKey key, final byte[] default_value)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        OutputStream out = new IOUtils.LimitBytesOutputStream(bytes, MAX_TRANSFER_BYTES_IN_BYTES);
        
        final boolean result = getCurrentVersion(key, out);
        
        if (result)
        {
            return bytes.toByteArray();
        }
        else
        {
            return default_value;
        }
    }

	public boolean exists(Storable obj, boolean default_value)
	{
		if (obj == null)
			return default_value;

		try
		{
			return exists(obj.createStorageKey(), false);
		} catch (Exception e)
		{
			LogManager.getRootLogger().error("Failure to list object " + obj, e);
			return default_value;
		}
	}

	public boolean delete(Storable obj)
	{
		if (obj == null)
			return false;
		if (isReadOnly())
			return false;

		return delete(obj.createStorageKey());
	}

    public boolean scan(Kind kind, StorageKeyHandler handler, int num_handler_threads)
    {
        return scan(kind, null, handler, num_handler_threads);
    }
    
    public boolean scanForObjectIds(Kind kind, StorageKeyHandler handler, int num_handler_threads)
    {
        return scanForObjectIds(kind, null, handler, num_handler_threads);
    }
	
	public boolean isReadOnly()
	{
		return is_readonly;
	}
}
