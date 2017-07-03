package org.jimmutable.aws.s3;

import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortIncompleteMultipartUpload;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

/**
 * The bucket puppet class is meant to be an analog to the puppet tool but for
 * S3 buckets.
 * 
 * The user of this class creates a simple (and easy to understand) desired
 * bucket configuration (BucketConfiguration)
 * 
 * Calling the execute method causes BucketPuppet to examine the current
 * configuration of the bucket in S3 and take whatever steps are required to
 * modify the bucket configuration to match the desired configuration specified.
 * 
 * Because of the way that bucket puppet focuses on making S3's configuration
 * match a desired target configuration, it is very safe to call. If you call
 * the execute functions multiple times with the same configuration, bucket
 * puppet won't do anything (just examine the config and say "hey all good" and
 * change nothing)
 * 
 * @author jim.kane
 *
 */
public class BucketPuppet 
{
	/**
	 * Take whatever steps are required to make S3 have a bucket in the desired
	 * configuration. This may include:
	 * 
	 * Doing nothing (if the bucket already exists and is properly configured)
	 * 
	 * Creating a bucket
	 * 
	 * Setting the bucket's versioning configuration
	 * 
	 * Setting the bucket's lifecyle rules
	 * 
	 * Setting the bucket's policy (to make it web accessible or not)
	 * 
	 * @param client
	 *            The AmazonS3 client to use
	 * @param desired_configuration
	 *            The desired bucket configuration
	 * @throws Exception
	 *             If something goes wrong
	 */
	static public void execute(AmazonS3 client, BucketConfiguration desired_configuration) throws Exception
	{
		Validator.notNull(desired_configuration);
		
		// Does the bucket exist?
		if ( !client.doesBucketExist(desired_configuration.getSimpleS3BucketNameString()) ) 
		{
			LogManager.getRootLogger().debug(String.format("Creating bucket: %s",desired_configuration.getSimpleS3BucketNameString()));
			client.createBucket(new CreateBucketRequest(desired_configuration.getSimpleS3BucketNameString()));
		}

		// Turn versioning on or off as needed
		{
			if ( desired_configuration.getSimpleIsVersioningEnabled() )
			{
				if ( !client.getBucketVersioningConfiguration(desired_configuration.getSimpleS3BucketNameString()).getStatus().equalsIgnoreCase(BucketVersioningConfiguration.ENABLED) )
				{
					LogManager.getRootLogger().debug(String.format("Enabling versioning on bucket: %s ... ",desired_configuration.getSimpleS3BucketNameString()));
	
					BucketVersioningConfiguration versioning_config = new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED);
					client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(desired_configuration.getSimpleS3BucketNameString(), versioning_config));
				}
			}
			else
			{
				// Versioning not enabled...
				
				if ( client.getBucketVersioningConfiguration(desired_configuration.getSimpleS3BucketNameString()).getStatus().equalsIgnoreCase(BucketVersioningConfiguration.ENABLED) )
				{
					LogManager.getRootLogger().debug(String.format("Suspending versioning on bucket: %s",desired_configuration.getSimpleS3BucketNameString()));
					
					BucketVersioningConfiguration versioning_config = new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED);
					client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(desired_configuration.getSimpleS3BucketNameString(), versioning_config));
				}
			}
		}
		
		// Setup the retention rule
		{
			if ( desired_configuration.getSimpleRetainOldVersionsInDays() == 0 )
			{
				/**
				 * In this case, the above check will suspend versioning
				 * 
				 * It has been decide to leave the lifecycle rules alone in this
				 * case. Why? Well, if you had a bunch of old version data
				 * (because the bucket used to be versioned) we want to *keep*
				 * that data till the *old* expiration time (safest thing to do)
				 * 
				 * If we set a retion time of 1 day or something, all old
				 * versions would be deleted (which could be catastrophic if a
				 * typo or something ever happened on a production bucket.
				 * 
				 * So, safest thing just leave whatever rule is in place, in
				 * place
				 */
			}
			else if ( doesLifecyleConfigurationNeedModification(client, desired_configuration) ) 
			{
				LogManager.getRootLogger().debug(String.format("Updating lifecyle configuration on bucket %s to retain old versions for %d days",desired_configuration.getSimpleS3BucketNameString(), desired_configuration.getSimpleRetainOldVersionsInDays()));
				
				BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule();
				
				rule = rule.withId(String.format("Retain old versions for %d days", desired_configuration.getSimpleRetainOldVersionsInDays()));
				
				rule = rule.withNoncurrentVersionExpirationInDays(desired_configuration.getSimpleRetainOldVersionsInDays());
				rule = rule.withStatus(BucketLifecycleConfiguration.ENABLED.toString());
				
				// Clean up expired object delete markers
				rule = rule.withExpiredObjectDeleteMarker(true);
				
				// Clean up multipart uploads
				rule = rule.withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(2));
				
				BucketLifecycleConfiguration config = new BucketLifecycleConfiguration().withRules(Arrays.asList(rule));
				
				client.setBucketLifecycleConfiguration(desired_configuration.getSimpleS3BucketNameString(), config);
			}
		}
		
		// Setup the bucket policy (this is what controls if the bucket is web accessible or not)
		{
			if ( desired_configuration.getSimpleIsWebAccessible() && !isBucketPolicyConfiguredAsWebAccessible(client, desired_configuration)  )
			{
				LogManager.getRootLogger().debug(String.format("Modifying bucket %s to be web accessible",desired_configuration.getSimpleS3BucketNameString()));
				client.setBucketPolicy(desired_configuration.getSimpleS3BucketNameString(), desired_configuration.createMakeWebAccessibleBucketPolicy());
			}
			
			if ( !desired_configuration.getSimpleIsWebAccessible() && isBucketPolicyConfiguredAsWebAccessible(client, desired_configuration)  )
			{
				LogManager.getRootLogger().debug(String.format("Modifying bucket %s to be web *non* accessible",desired_configuration.getSimpleS3BucketNameString()));
				client.deleteBucketPolicy(desired_configuration.getSimpleS3BucketNameString());
			}
		}
		
		LogManager.getRootLogger().debug(String.format("BucketPuppet: bucket configuration verified: %s", desired_configuration.getSimpleS3BucketName()));
	}
	
	/**
	 * Answer the question: does the bucket's lifecyle configuration need to be modifed?
	 * 
	 * @param client
	 * @param desired_configuration
	 * @return
	 */
	static private boolean doesLifecyleConfigurationNeedModification(AmazonS3 client, BucketConfiguration desired_configuration)
	{
		if ( desired_configuration.getSimpleRetainOldVersionsInDays() == 0 ) return false; // do not modify configuration when versioning is not enabled
		
		BucketLifecycleConfiguration current_lifecyle_configuration = client.getBucketLifecycleConfiguration(desired_configuration.getSimpleS3BucketNameString());
		
		if ( current_lifecyle_configuration.getRules().size() != 1 ) return true;
		
		BucketLifecycleConfiguration.Rule cur_rule = current_lifecyle_configuration.getRules().get(0);
		
		if ( cur_rule.getNoncurrentVersionExpirationInDays() != desired_configuration.getSimpleRetainOldVersionsInDays() ) return true;
		if ( !cur_rule.getStatus().equals(BucketLifecycleConfiguration.ENABLED.toString()) ) return true;
		
		return false;
	} 
	
	/**
	 * Answer the quetsion: does the bucket's policy need to be modified?
	 * 
	 * This method is a bit of a hack... if the bucket is web accessible, it
	 * just looks to make sure the bucket policy contains the string
	 * "web_bucket_policy". If the bucket is not supposed to be web accessible,
	 * the function checks to see if the bucket policy is null
	 * 
	 * Why? Amazon does not give great classes to read and interpret bucket
	 * policies and this simplistic approach seems "good enough" for now
	 * 
	 * @param client
	 * @param desired_configuration
	 * @return
	 */
	static private boolean isBucketPolicyConfiguredAsWebAccessible(AmazonS3 client, BucketConfiguration desired_configuration)
	{
		BucketPolicy cur_policy = client.getBucketPolicy(desired_configuration.getSimpleS3BucketNameString());
		
		if ( cur_policy.getPolicyText() == null ) return false;
		
		String text = cur_policy.getPolicyText();
		
		if ( text.toLowerCase().contains("web_bucket_policy") ) return true;
		
		return false;
	}
	
	/**
	 * This class is used to specify the desired configuration of a bucket
	 * @author jim.kane
	 *
	 */
	static public class BucketConfiguration
	{
		private S3BucketName bucket_name;
		private int retain_old_versions_in_days;
		
		private boolean is_web_accessible;
		
		public BucketConfiguration(S3BucketName bucket_name, int retain_old_versions_in_days, boolean is_web_accessible)
		{
			Validator.notNull(bucket_name);
			
			if ( retain_old_versions_in_days < 0 ) retain_old_versions_in_days = 0;
			
			this.bucket_name = bucket_name;
			this.retain_old_versions_in_days = retain_old_versions_in_days;
			this.is_web_accessible = is_web_accessible;
		}
		
		public S3BucketName getSimpleS3BucketName() { return bucket_name; }
		public String getSimpleS3BucketNameString() { return bucket_name.toString(); }
		
		public int getSimpleRetainOldVersionsInDays() { return retain_old_versions_in_days; }
		
		public boolean getSimpleIsWebAccessible() { return is_web_accessible; }
		
		public boolean getSimpleIsVersioningEnabled() { return retain_old_versions_in_days > 0; }
		
		public String createMakeWebAccessibleBucketPolicy()
		{
			return "{ \"Id\": \"web_bucket_policy\", \"Version\": \"2012-10-17\", \"Statement\": [ { \"Sid\": \"readonly policy\", \"Action\": [ \"s3:GetObject\" ], \"Effect\": \"Allow\", \"Resource\":\"arn:aws:s3:::" + getSimpleS3BucketNameString() + "/*\", \"Principal\": \"*\" } ] }";
		}
	}
}
