package org.jimmutable.gcloud.examples;

import org.jimmutable.gcloud.GCloudTypeNameRegister;

public class ExampleUtils 
{
	/**
	 * Standardized setup for all gcloud examples...
	 */
	static public void setupExample()
	{
		// Register type jimmutable type names
		GCloudTypeNameRegister.registerAllTypes();
		
		// Verify that the credentials are setup
//		String credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
		
//		if ( credentials == null )
//		{
//			System.err.println("You need to set the GOOGLE_APPLICATION_CREDENTIALS environment variable to be the path of a JSON credentials file." );
//			System.err.println("For example: /Users/kanej/platform-test-cred.json");
//			System.err.println("To do this in eclipse, go to Run > Run Configurations..., switch to the Environment tab and add the variable there");
//			System.exit(1);
//		}
	}
}
