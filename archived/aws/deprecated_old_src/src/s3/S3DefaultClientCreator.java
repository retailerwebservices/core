package org.jimmutable.cloud.s3;

import org.jimmutable.cloud.environment.ApplicationEnvironment;

import software.amazon.awssdk.services.s3.AmazonS3;
import software.amazon.awssdk.services.s3.AmazonS3ClientBuilder;

/**
 * This class simply encapsulates our "default" settings for creating an S3
 * client
 * 
 * @author jim.kane
 *
 */
public class S3DefaultClientCreator 
{
	/**
	 * Create a new AmazonS3 client using the default settings
	 * 
	 * @param env The application environmnent
	 * 
	 * @return A newly created AmazonS3 instance
	 */
	static public AmazonS3 createDefaultAmazonS3Client(ApplicationEnvironment env)
	{
		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
		
		builder.setCredentials(env.getSimpleAWSCredentialsProvider());
		builder.setRegion("us-west-2");
		
		return builder.build();
	}
}
