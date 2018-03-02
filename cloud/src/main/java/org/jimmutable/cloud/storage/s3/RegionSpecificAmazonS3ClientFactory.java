package org.jimmutable.cloud.storage.s3;

import org.jimmutable.core.utils.Validator;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;


/**
 * A {@link AmazonS3ClientFactory factory} that uses the
 * {@link com.amazonaws.auth.DefaultAWSCredentialsProviderChain default AWS credentials provider}
 * but allows clients to specify which region to use.
 * </p>
 * The default region for jimmutable is {@link Region#US_West_2 us-west-2}.
 *
 * @author Jeff Dezso
 */
public class RegionSpecificAmazonS3ClientFactory implements AmazonS3ClientFactory
{
    static public final Region DEFAULT_REGION = Region.US_West_2;
    
    
    private final Region region;
    
    public RegionSpecificAmazonS3ClientFactory(final Region region)
    {
        Validator.notNull(region);
        
        this.region = region;
    }
    
    @Override
    public AmazonS3Client create()
    {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region.getFirstRegionId())).build();
    }
    
    static public AmazonS3ClientFactory defaultFactory()
    {
        return new RegionSpecificAmazonS3ClientFactory(DEFAULT_REGION);
    }
}
