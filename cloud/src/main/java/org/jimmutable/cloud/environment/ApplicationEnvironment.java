package org.jimmutable.cloud.environment;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.cloud.keys.AWSStaticCredentials;
import org.jimmutable.cloud.utils.PropertiesReader;
import org.jimmutable.core.utils.Validator;

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
		
		cloud_name = loadLoadNameFromPropertiesFile(r, null);
		Validator.notNull(cloud_name);
		
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
	
	/**
	 * This function loads the cloud name from the properties file. Unit testing
	 * environment overide this function to return CloudName.UNIT_TEST
	 * 
	 * @param r
	 *            The properties reader to read from
	 * @param default_value
	 *            The value to return if the cloud name can not be loaded
	 * @return The cloud name of the environment
	 */
	public CloudName loadLoadNameFromPropertiesFile(PropertiesReader r, CloudName default_value)
	{
		String cloud_name_str = r.readString("cloud_name", null);
		
		if ( cloud_name_str == null )
		{
			LogManager.getRootLogger().error(String.format("The ~/%s must specify a cloud_name", PROPERTIES_FILE_NAME));
			System.exit(1);
			return default_value;
		}
		
		return new CloudName(cloud_name_str);
	}
}
