package org.jimmutable.aws.environment;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.aws.keys.AWSStaticCredentials;
import org.jimmutable.aws.utils.PropertiesReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

abstract public class ApplicationEnvironment 
{
	private HostName host_name; // required
	private CloudName cloud_name; // required
	private AWSCredentials aws_credentials; // required
	
	public ApplicationEnvironment()
	{
		DefaultLoggingSetup.configureLogging();
		
		host_name = HostName.CURRENT_COMPUTER_NAME;
		
		PropertiesReader r = new PropertiesReader("panda.properties");
		
		String cloud_name_str = r.readString("cloud_name", null);
		
		if ( cloud_name_str == null )
		{
			LogManager.getRootLogger().error("The ~/panda.properties must specify a cloud_name");
			System.exit(1);
			return;
		}
		
		for ( PandaCloudName panda_cloud_name : PandaCloudName.values() )
		{
			if ( panda_cloud_name.getSimpleCloudName().getSimpleValue().equalsIgnoreCase(cloud_name_str) )
			{
				cloud_name = panda_cloud_name.getSimpleCloudName();
				break;
			}
		}
		
		if ( cloud_name == null )
		{
			LogManager.getRootLogger().error(String.format("The ~/panda.properties file specifies a cloud_name of %s, which is not a valid cloud name in PandaCloudName", cloud_name_str));
			System.exit(1);
			return;
		}
		
		String aws_id = r.readString("aws_id", null);
		String aws_secret = r.readString("aws_secret", null);
		
		if ( aws_id == null || aws_secret == null )
		{
			LogManager.getRootLogger().error("The ~/panda.properties file must specify both a aws_id and aws_secret");
			System.exit(1);
			return;
		}
		
		aws_credentials = new AWSStaticCredentials(aws_id, aws_secret);
	}
	
	
	public HostName getSimpleHostName() { return host_name; }
	public CloudName getSimpleCloudName() { return cloud_name; }
	public AWSCredentials getSimpleAWSCredentials() { return aws_credentials; }
	
	public AWSCredentialsProvider getSimpleAWSCredentialsProvider() { return new CredentialsProvider(); }
	
	private class CredentialsProvider implements AWSCredentialsProvider
	{

		public AWSCredentials getCredentials() 
		{
			return getSimpleAWSCredentials();
		}

		public void refresh() {}
	}
}
