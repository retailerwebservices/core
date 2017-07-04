package org.jimmutable.aws.environment;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.aws.keys.AWSStaticCredentials;
import org.jimmutable.aws.utils.PropertiesReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

abstract public class ApplicationEnvironment 
{
	static private final String PROPERTIES_FILE_NAME = "jimmutable-aws.properties";
	
	
	private HostName host_name; // required
	private CloudName cloud_name; // required
	private AWSCredentials aws_credentials; // required
	
	public ApplicationEnvironment()
	{
		DefaultLoggingSetup.configureLogging();
		
		host_name = HostName.CURRENT_COMPUTER_NAME;
		
		PropertiesReader r = new PropertiesReader(PROPERTIES_FILE_NAME);
		
		String cloud_name_str = r.readString("cloud_name", null);
		
		if ( cloud_name_str == null )
		{
			LogManager.getRootLogger().error(String.format("The ~/%s must specify a cloud_name", PROPERTIES_FILE_NAME));
			System.exit(1);
			return;
		}
		
		cloud_name = new CloudName(cloud_name_str);
		
		String aws_id = r.readString("aws_id", null);
		String aws_secret = r.readString("aws_secret", null);
		
		if ( aws_id == null || aws_secret == null )
		{
			LogManager.getRootLogger().error(String.format("The ~/%s file must specify both a aws_id and aws_secret", PROPERTIES_FILE_NAME));
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
