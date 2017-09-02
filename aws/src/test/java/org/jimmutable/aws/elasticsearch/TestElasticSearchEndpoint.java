package org.jimmutable.aws.elasticsearch;

import java.util.logging.Logger;

import org.jimmutable.aws.StartupSingleton;
import org.junit.Before;
import org.junit.Test;

public class TestElasticSearchEndpoint
{

	private static Logger logger = Logger.getLogger(TestElasticSearchEndpoint.class.getName());

	@Before
	public void before()
	{
		StartupSingleton.setupOnce();
	}

	@Test
	public void testNoEnvironmentvariable()
	{

		logger.finest("so fine");

	}

}
