package org.jimmutable.cloud;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.junit.BeforeClass;

public abstract class StubTest
{

	@BeforeClass
	public static void setUp()
	{
		try
		{
			CloudExecutionEnvironment.startupStubTest(new ApplicationId("stub"));
		} catch (RuntimeException e)
		{
			System.out.println(e);
		}
	}

}
