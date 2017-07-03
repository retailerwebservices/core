package org.jimmutable.aws.blob_store;

import java.io.File;

import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.core.utils.Validator;

public class BlobStoreUploadRequest 
{
	private S3Path base_path; // required
	private String fixed_portion_of_name; // required
	private String extension; // required
	
	private File src; // required
	
	public BlobStoreUploadRequest(File src, S3Path base_path, String fixed_portion_of_name, String extension)
	{
		Validator.notNull(src,base_path,fixed_portion_of_name,extension);
		
		this.src = src;
		this.base_path = base_path;
		this.fixed_portion_of_name = fixed_portion_of_name.trim().toLowerCase();
		this.extension = extension.trim().toLowerCase();
		
		Validator.containsOnlyValidCharacters(this.fixed_portion_of_name, Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
		Validator.containsOnlyValidCharacters(this.extension, Validator.LOWERCASE_LETTERS, Validator.NUMBERS);
	}
	
	public S3Path getSimpleBasePath() { return base_path; }
	public String getSimpleFixedPortionOfName() { return fixed_portion_of_name; }
	public String getSimpleExtension() { return extension; }
}
