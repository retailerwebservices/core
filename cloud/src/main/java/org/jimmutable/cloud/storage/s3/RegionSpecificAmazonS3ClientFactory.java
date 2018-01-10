package org.jimmutable.cloud.storage.s3;

import org.jimmutable.core.utils.Validator;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


/**
 * A {@link AmazonS3ClientFactory factory} that uses the
 * {@link com.amazonaws.auth.DefaultAWSCredentialsProviderChain default AWS credentials provider}
 * but allows clients to specify which region to use.
 * </p>
 * The default region for jimmutable is {@link Regions#US_WEST_2 us-west-2}.
 *
 * @author Jeff Dezso
 */
public class RegionSpecificAmazonS3ClientFactory implements AmazonS3ClientFactory
{
    static public final Regions DEFAULT_REGION = Regions.US_WEST_2;
    
    
    private final Regions region;
    
    public RegionSpecificAmazonS3ClientFactory(final Regions region)
    {
        Validator.notNull(region);
        
        this.region = region;
    }
    
    @Override
    public AmazonS3Client create()
    {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(region).build();
    }
    
    static public AmazonS3ClientFactory defaultFactory()
    {
        return new RegionSpecificAmazonS3ClientFactory(DEFAULT_REGION);
    }
}
