package org.jimmutable.gcloud.storage;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class BucketConnection
{
	Storage storage = StorageOptions.getDefaultInstance().getService();

	BucketConnection(String bucket_name)
	{
		String credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

		if (credentials == null)
		{
			System.err.println(
					"You need to set the GOOGLE_APPLICATION_CREDENTIALS environment variable to be the path of a JSON credentials file.");
			System.err.println("For example: /Users/preston/platform-test-cred.json");
			System.err.println(
					"To do this in eclipse, go to Run > Run Configurations..., switch to the Environment tab and add the variable there");
			System.exit(1);
		}

	}

}
