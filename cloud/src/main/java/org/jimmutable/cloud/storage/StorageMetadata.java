package org.jimmutable.cloud.storage;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Validator;

/**
 * Simple container class to contain simple metadata attributes about objects in Storage. We need a new class for jimmutable-cloud
 * so that it supports the IStorage abstraction in various deployment environments.
 * @author salvador.salazar
 */
public class StorageMetadata extends StandardImmutableObject<StorageMetadata>
{
	static public final TypeName TYPE_NAME = new TypeName("org.jimmutable.cloud.storage.StorageMetadata"); public TypeName getTypeName() { return TYPE_NAME; }
	static public final Kind KIND = new Kind("storage-metadata");

	static public final FieldDefinition.Long FIELD_LAST_MODIFIED = new FieldDefinition.Long("last_modified", -1l);
	static public final FieldDefinition.Long FIELD_SIZE = new FieldDefinition.Long("size", -1l);
	static public final FieldDefinition.String FIELD_ETAG = new FieldDefinition.String("etag", null);
	
	private long last_modified; // required, positive
	private long size; // required, non-negative
	private String etag; // required, set to last modified if not provided
	
    public StorageMetadata(final long last_modified, final long size)
    {
        this(last_modified, size, null);
    }
    
	public StorageMetadata(final long last_modified, final long size, final String etag)
	{
	    this.last_modified = last_modified;
	    this.size = size;
	    this.etag = etag;
	    
	    complete();
	}
	
	public StorageMetadata(ObjectParseTree reader)
	{
		last_modified = reader.getLong(FIELD_LAST_MODIFIED);
		size = reader.getLong(FIELD_SIZE);
		etag = reader.getString(FIELD_ETAG);
	}

	public int compareTo(StorageMetadata o) 
	{
		return Long.compare(getSimpleLastModified(), o.getSimpleLastModified());
	}
	
	public long getSimpleLastModified()
	{
		return last_modified;
	}
	
	public long getSimpleSize()
	{
		return size;
	}
	
	public String getSimpleEtag()
	{
		return etag;
	}

	public void freeze() 
	{
		// nothing to do
	}

	
	public void normalize() 
	{
		// if etag isn't set, then set to last_modified
		if (getSimpleEtag() == null)
		{
			etag = String.valueOf(last_modified);
		}
	}

	public void validate() 
	{
		Validator.min(getSimpleLastModified(), 1);
		Validator.min(getSimpleSize(), 0);
		Validator.notNull(getSimpleEtag());
	}

	@Override
	public void write(ObjectWriter writer) 
	{
		writer.writeLong(FIELD_LAST_MODIFIED, getSimpleLastModified());
		writer.writeLong(FIELD_SIZE, getSimpleSize());
		writer.writeString(FIELD_ETAG, getSimpleEtag());
	}

	public int hashCode()
	{
		return Objects.hash(last_modified, size, etag);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorageMetadata other = (StorageMetadata) obj;
		if (etag == null) {
			if (other.etag != null)
				return false;
		} else if (!etag.equals(other.etag))
			return false;
		if (last_modified != other.last_modified)
			return false;
		if (size != other.size)
			return false;
		return true;
	}
}