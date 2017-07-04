package org.jimmutable.aws.blob_store;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

import com.amazonaws.util.ComparableUtils;

public class BlobStoreUploadRequest extends TransientImmutableObject<BlobStoreUploadRequest>
{
	private S3Path base_path; // required
	private String fixed_portion_of_name; // required
	private String extension; // required
	
	private File src; // required
	
	public BlobStoreUploadRequest(File src, S3Path base_path, String fixed_portion_of_name, String extension)
	{
		if ( fixed_portion_of_name == null ) fixed_portion_of_name = "";
		
		Validator.notNull(src,base_path,fixed_portion_of_name,extension);
		
		this.src = src;
		this.base_path = base_path;
		this.fixed_portion_of_name = fixed_portion_of_name.trim().toLowerCase();
		this.extension = extension.trim().toLowerCase();
		
		complete();
	}
	
	public File getSimpleSourceFile() { return src; }
	public S3Path getSimpleBasePath() { return base_path; }
	public String getSimpleFixedPortionOfName() { return fixed_portion_of_name; }
	public String getSimpleExtension() { return extension; }

	public int compareTo(BlobStoreUploadRequest o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimpleBasePath(), o.getSimpleBasePath());
		ret = Comparison.continueCompare(ret, getSimpleFixedPortionOfName(), o.getSimpleFixedPortionOfName());
		ret = Comparison.continueCompare(ret, getSimpleExtension(), o.getSimpleExtension());
		
		return ret;
	}
	
	public void freeze() {}

	
	public void normalize() 
	{
		
	}

	@Override
	public void validate() 
	{
		Validator.notNull(base_path, fixed_portion_of_name, extension);
		Validator.containsOnlyValidCharacters(this.fixed_portion_of_name, Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
		Validator.containsOnlyValidCharacters(this.extension, Validator.LOWERCASE_LETTERS, Validator.NUMBERS);	
	}

	
	public int hashCode() 
	{
		return Objects.hash(base_path, fixed_portion_of_name, extension);
	}

	
	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof BlobStoreUploadRequest) ) return false;
		
		BlobStoreUploadRequest other = (BlobStoreUploadRequest)obj;
		
		if ( !src.getAbsolutePath().equals(other.getSimpleSourceFile().getAbsolutePath()) ) return false;
		if ( !getSimpleBasePath().equals(other.getSimpleBasePath()) ) return false;
		if ( !getSimpleFixedPortionOfName().equals(other.getSimpleFixedPortionOfName()) ) return false;
		if ( !getSimpleExtension().equals(other.getSimpleExtension()) ) return false;
		
		return true;
	}
	
	public String toString() 
	{
		return String.format("BlobStoreUploadRequest, base_path = %s, fixed_portion_of_name = %s, extension = %s", getSimpleBasePath(), getSimpleFixedPortionOfName(), getSimpleExtension());
	}
	
	/**
	 * Using the base path, fixed portion of name and extension, create a random
	 * target S3 path
	 * 
	 * @return A randomized S3 path
	 */
	public S3Path createRandomPath()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(base_path.getSimpleValue());
		
		if ( getSimpleFixedPortionOfName().length() != 0 )
		{
			builder.append(getSimpleFixedPortionOfName());
			builder.append('-');
		}
		
		Random r = new Random();
		
		for ( int i = 0; i < 9; i++ )
		{
			builder.append('a'+r.nextInt(25));
		}
		
		builder.append(".");
		
		builder.append(getSimpleExtension());
		
		return new S3Path(builder.toString());
	}
}

