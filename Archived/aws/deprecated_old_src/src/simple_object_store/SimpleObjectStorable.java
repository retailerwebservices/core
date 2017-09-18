package org.jimmutable.cloud.simple_object_store;

import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.core.serialization.Format;

public interface SimpleObjectStorable 
{
	/**
	 * Return the S3 path the object should be serialized to.  Must be a .xml file
	 * @return The path that this object should be store to
	 */
	public S3Path getStorableS3Path();
	
	
	public String serialize(Format format);
}
