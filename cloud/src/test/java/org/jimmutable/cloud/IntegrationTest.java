package org.jimmutable.cloud;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;

public abstract class IntegrationTest
{

	public static void setupEnvironment()
	{
		try
		{
			CloudExecutionEnvironment.startupIntegrationTest(new ApplicationId("integration"));
		} catch (RuntimeException e)
		{
			System.out.println(e);
		}
	}

}
