package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import org.jimmutable.core.exceptions.ValidationException;
import org.junit.Test;

public class ElasticSearchEndpointTest
{

	@Test
	public void testCurrent()
	{
		assertEquals("localhost", ElasticSearchEndpoint.CURRENT.getSimpleHost());
		assertEquals(9300, ElasticSearchEndpoint.CURRENT.getSimplePort());
	}

	@Test
	public void testInstant()
	{
		ElasticSearchEndpoint e = new ElasticSearchEndpoint("my.host.name", 1234);
		assertEquals("my.host.name", e.getSimpleHost());
		assertEquals(1234, e.getSimplePort());
	}

	@Test(expected = ValidationException.class)
	public void invalid()
	{
		new ElasticSearchEndpoint(null, 123);
	}

}