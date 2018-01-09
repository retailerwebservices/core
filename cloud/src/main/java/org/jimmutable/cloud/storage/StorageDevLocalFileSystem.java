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
import java.util.function.Consumer;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.threading.OperationPool;
import org.jimmutable.core.threading.OperationRunnable;
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

	@Override
	public boolean upsert(final StorageKey key, final InputStream source, final boolean hint_content_likely_to_be_compressible)
	{
        Validator.notNull(key, source);
        
	    if (isReadOnly()) return false;
	    
        final File dest_file = new File(root.getAbsolutePath() + "/" + key.toString());
        dest_file.getParentFile().mkdirs(); // Make sure the directories exist
        
        try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(dest_file)))
        {
            IOUtils.transferAllBytes(source, fout);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
	}

	@Override
	public boolean getCurrentVersion(final StorageKey key, final OutputStream sink)
	{
        Validator.notNull(key);
        
        final File source_file = new File(root.getAbsolutePath() + "/" + key.toString());
        if (! source_file.exists()) return false;
        
        try (InputStream fin = new BufferedInputStream(new FileInputStream(source_file)))
        {
            IOUtils.transferAllBytes(fin, sink);
            return true;
        }
        catch (Exception e)
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

    @Override
	public boolean scan(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads)
	{
	    return scanImpl(kind, prefix, handler, num_handler_threads, false);
	}
	
    @Override
    public boolean scanForObjectIds(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads)
    {
        return scanImpl(kind, prefix, handler, num_handler_threads, true);
    }
    
    private boolean scanImpl(final Kind kind, final StorageKeyName prefix, final StorageKeyHandler handler, final int num_handler_threads, final boolean only_object_ids)
    {
        Scanner scanner = new Scanner(kind, prefix, only_object_ids);
        OperationPool pool = new OperationPool(scanner, num_handler_threads);
        
        scanner.setSink((StorageKey key) ->
        {
            pool.submitOperation(new StorageKeyHandlerWorker(handler, key));
        });
        
        OperationRunnable.Result result = OperationRunnable.execute(pool, OperationRunnable.Result.ERROR);
        return OperationRunnable.Result.SUCCESS == result;
    }
    
    static private class StorageKeyHandlerWorker extends OperationRunnable
    {
        private final StorageKeyHandler handler;
        private final StorageKey key;
        
        public StorageKeyHandlerWorker(final StorageKeyHandler handler, final StorageKey key)
        {
            this.handler = handler;
            this.key = key;
        }
        
        @Override
        protected Result performOperation() throws Exception
        {
            if (null == handler) return Result.SUCCESS;
            
            handler.handle(key);
            
            return Result.SUCCESS;
        }
    }

    /**
     * This class does the main listing operation for scan*.
     * It runs in it's own thread and throws each StorageKey it
     * finds into another OperationRunnable running in a common
     * pool.
     *
     * @author Jeff Dezso
     */
	private class Scanner extends OperationRunnable
	{
	    private final Kind kind;
	    private final StorageKeyName prefix;
	    private final boolean only_object_ids;
        
	    private Consumer<StorageKey> sink;
	    
	    public Scanner(final Kind kind, final StorageKeyName prefix, final boolean only_object_ids)
	    {
	        Validator.notNull(kind);
	        
	        this.kind = kind;
	        this.prefix = prefix;
	        this.only_object_ids = only_object_ids;
	    }
	    
        @Override
        protected Result performOperation() throws Exception
        {
            Validator.notNull(sink);
            
            final File folder = new File(root.getAbsolutePath() + "/" + kind.getSimpleValue());
            
            Files.walkFileTree(folder.toPath(), EnumSet.noneOf(FileVisitOption.class), 1, new Walker());
            
            return shouldStop() ? Result.STOPPED : Result.SUCCESS;
        }
        
        /**
         * The sink has to be set after construction to avoid a race condition
         * between construction of the OperationPool and construction the seed
         * OperationRunnable
         * 
         * @param handler
         */
        public void setSink(Consumer<StorageKey> sink)
        {
            this.sink = sink;
        }
        
	    private class Walker extends SimpleFileVisitor<Path>
	    {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (shouldStop()) return FileVisitResult.TERMINATE;
                
                Validator.notNull(file); 
                Validator.notNull(attrs);
                
                String[] file_name_and_ext = file.getFileName().toString().split("\\.");

                if (file_name_and_ext.length < 2)
                {
                    System.err.println("Scanner error with file: " + file);
                    return FileVisitResult.CONTINUE;
                }

                StorageKeyName name = new StorageKeyName(file_name_and_ext[0]);

                if (prefix != null)
                {
                    if (! name.getSimpleValue().startsWith(prefix.getSimpleValue()) )
                    {
                        return FileVisitResult.CONTINUE;
                    }
                }

                String key = kind + "/" + file.getFileName();

                if (name.isObjectId())
                {
                    sink.accept(new ObjectIdStorageKey(key));
                }
                else
                {
                    if (! only_object_ids)
                    {
                        sink.accept(new GenericStorageKey(key));
                    }
                }
                
                return FileVisitResult.CONTINUE;
            }
	    }
	}
}
