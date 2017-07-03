package org.jimmutable.aws.keys;

import com.amazonaws.auth.AWSCredentials;

/**
 * This class is simply an invalid set of AWS credentials. It is used to avoid
 * returning null objects etc.
 * 
 * @author jim.kane
 *
 */
public class AWSInvalidCredentials implements AWSCredentials
{
	public AWSInvalidCredentials() {}
	
	public String getAWSAccessKeyId() 
	{
		return "this is an invalid access key";
	}

	public String getAWSSecretKey() 
	{
		return "this is an invalid secret key";
	}
	
}
