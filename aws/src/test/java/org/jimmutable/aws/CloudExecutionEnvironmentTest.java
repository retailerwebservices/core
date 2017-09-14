package org.jimmutable.aws;

import org.jimmutable.storage.ApplicationId;
import org.junit.BeforeClass;
import org.junit.Test;

public class CloudExecutionEnvironmentTest
{

	//@BeforeClass
	public static void setup()
	{

	}

	//@Test
	public void testOnce()
	{
		System.setProperty("JIMMUTABLE_ENV_TYPE", "lol");
		CloudExecutionEnvironment.startup(new ApplicationId("foggle"));
	}

}
