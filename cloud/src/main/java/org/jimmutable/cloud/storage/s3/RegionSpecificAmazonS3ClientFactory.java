package org.jimmutable.cloud.storage.s3;

import org.jimmutable.core.utils.Validator;

import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.regions.Region;


/**
 * A {@link AmazonS3ClientFactory factory} that uses the
 * {@link software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider default AWS credentials provider}
 * but allows clients to specify which region to use.
 * </p>
 * The default region for jimmutable is {@link Region#US_WEST_2 us-west-2}.
 *
 * @author Jeff Dezso
 */
public class RegionSpecificAmazonS3ClientFactory implements AmazonS3ClientFactory
{
    static public final Region DEFAULT_REGION = Region.US_WEST_2;
    
    
    private final Region region;
    
    public RegionSpecificAmazonS3ClientFactory(final Region region)
    {
        Validator.notNull(region);
        
        this.region = region;
    }
    
    @Override
    public S3AsyncClient create()
    {
        return S3AsyncClient.builder().region(region).build();
    }
    
    static public AmazonS3ClientFactory defaultFactory()
    {
        return new RegionSpecificAmazonS3ClientFactory(DEFAULT_REGION);
    }
}
