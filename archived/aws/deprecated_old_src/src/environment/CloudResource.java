package org.jimmutable.cloud.environment;

import org.jimmutable.core.utils.Validator;

abstract public class CloudResource 
{
	private CloudName cloud_name;
	
	public CloudResource(CloudName cloud_name)
	{
		Validator.notNull(cloud_name);
		this.cloud_name = cloud_name;
	}
	
	public CloudName getSimpleCloudName() { return cloud_name; }
}
