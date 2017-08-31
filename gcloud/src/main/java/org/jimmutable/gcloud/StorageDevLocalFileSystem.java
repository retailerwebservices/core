package org.jimmutable.gcloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.utils.Validator;

import com.google.appengine.repackaged.com.google.common.io.Files;
import com.google.appengine.repackaged.com.google.common.primitives.Bytes;

import sun.misc.IOUtils;

public class StorageDevLocalFileSystem extends Storage{
	
	private String prefix;
	
	public StorageDevLocalFileSystem(boolean is_readOnly) 
	{
		super(is_readOnly);
		
		if ( !ApplicationId.hasOptionalDevApplicationId() )
		{
			System.err.println("Hey -- you are trying to instantiate a dev local file system...");
			throw new RuntimeException();
		}
		prefix = System.getProperty("user.home")+"/dev/storage/"+ApplicationId.getOptionalDevApplicationId(new ApplicationId("Development"))+"/";		
	}

	@Override
	public boolean exists(StorageKey key, boolean default_value) 
	{
		File f = new File(prefix+key.getSimpleValue());
		if(f.exists() && !f.isDirectory()) 
		{ 
			return true;
		}
		return default_value;
	}

	@Override
	public boolean upsert(StorageKey key, byte[] bytes, boolean hint_content_likely_to_be_compressible) 
	{
		if(isReadOnly()) 
		{
			return false;
		}
		Validator.notNull(key,bytes,hint_content_likely_to_be_compressible);
		try 
		{
			File pfile = new File(prefix+key.getSimpleKind());
			pfile.mkdirs();
			String path = prefix+key.getSimpleValue();
			File file = new File(path);
			boolean success = file.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(bytes);
			fos.close();
		} 
		catch (Exception e) 
		{
			return false;
		} 
		return true;
	}

	@Override
	public byte[] getCurrentVersion(StorageKey key, byte[] default_value) {
		Validator.notNull(key);
		if(exists(key,false)) 
		{
			File file = new File(prefix+key.getSimpleValue());
			byte[] bytesArray = new byte[(int) file.length()];
			FileInputStream fis=null;
			try 
			{
				fis = new FileInputStream(file);
				fis.read(bytesArray); //read file into bytes[]
				
			} 
			catch (FileNotFoundException e) 
			{
				return default_value;
			} 
			catch (IOException e) 
			{
				return default_value;
			}
			finally
			{
				try 
				{
					fis.close();
				} 
				catch (IOException e) 
				{
					return default_value;
				}
			}

			return bytesArray;
		}
		return default_value;
	}

	@Override
	public boolean delete(StorageKey key) {
		if(isReadOnly()) 
		{
			return false;
		}
		Validator.notNull(key);
		try
		{
			File f = new File(prefix+key.getSimpleValue());
			return f.delete();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public Iterable<StorageKey> list(Kind kind) {
		Validator.notNull(kind);
		File folder = new File(prefix+kind.getSimpleValue());
		File[] listOfFiles = folder.listFiles();
		List<StorageKey> keys = new ArrayList<>();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
			{
				String key = kind+"/"+listOfFiles[i].getName();
				keys.add(new StorageKey(key));
			}
		}
		return keys;
	}
}
