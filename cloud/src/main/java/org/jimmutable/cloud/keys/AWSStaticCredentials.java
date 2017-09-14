package org.jimmutable.cloud.keys;

import org.jimmutable.core.utils.Validator;

import com.amazonaws.auth.AWSCredentials;

public class AWSStaticCredentials implements AWSCredentials
{
	private String id;
	private String secret_key;
	
	
	public AWSStaticCredentials(String id, String secret_key)
	{
		Validator.notNull(id, secret_key);
		
		this.id = id;
		this.secret_key = secret_key;
	}
	
	public String getAWSAccessKeyId() 
	{
		return id;
		
	}

	public String getAWSSecretKey() 
	{
		return secret_key;
	}

}
