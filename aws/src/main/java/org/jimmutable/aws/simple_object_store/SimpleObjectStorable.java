package org.jimmutable.aws.simple_object_store;

import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.core.serialization.Format;

public interface SimpleObjectStorable 
{
	public S3Path getSimplePath();
	public String serialize(Format format);
}
