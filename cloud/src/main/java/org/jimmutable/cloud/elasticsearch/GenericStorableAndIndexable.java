package org.jimmutable.cloud.elasticsearch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;

/**
 * This is a simple class to handle deserializing any Kind's Object and ensuring
 * that the Kind's Object is both Indexable and Storable.
 */
public class GenericStorableAndIndexable<T>
{
	
	private static final Logger logger = LoggerFactory.getLogger(GenericStorableAndIndexable.class);
	private T object;

	@SuppressWarnings("unchecked")
	public GenericStorableAndIndexable( byte[] bytes ) throws ValidationException
	{
		StandardObject<?> obj = null;
		try
		{
			obj = StandardObject.deserialize(new String(bytes));
		}
		catch ( Exception e )
		{
			throw new ValidationException("Unable to deserialize object", e);
		}

		// Broken out this way, rather than just deserializing T so that we know exactly
		// what a
		if ( !(obj instanceof Storable) )
		{
			throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Storable.");
		}
		if ( !(obj instanceof Indexable) )
		{
			throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Indexable.");
		}

		this.object = (T) obj;
	}

	@SuppressWarnings("unchecked")
	public GenericStorableAndIndexable( StorageKey key ) throws ValidationException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersionStreaming(key, baos);
		StandardObject<?> obj = null;
		try
		{
			String data = baos.toString("UTF-8");
			obj = StandardObject.deserialize(data);
		}
		catch ( IOException e )
		{
			throw new ValidationException("Unable to deserialize object", e);
		}
		finally
		{
			try
			{
				baos.close();
			}
			catch ( IOException e )
			{
				logger.error("Can't close output stream", e);
			}
		}

		// Broken out this way, rather than just deserializing T so that we know exactly
		// what a
		if ( !(obj instanceof Storable) )
		{
			throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Storable.");
		}
		if ( !(obj instanceof Indexable) )
		{
			throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Indexable.");
		}

		this.object = (T) obj;
	}

	public T getObject()
	{
		return object;
	}
}