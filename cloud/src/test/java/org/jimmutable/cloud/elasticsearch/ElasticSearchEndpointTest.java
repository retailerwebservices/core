package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import org.jimmutable.core.exceptions.ValidationException;
import org.junit.Test;

public class ElasticSearchEndpointTest
{

	@Test
	public void testEnvironmentvariable()
	{
		System.setProperty("elasticsearch.endpoint", "abc:123");
		assertEquals("abc", ElasticSearchEndpoint.CURRENT.getSimpleHost());
		assertEquals(123, ElasticSearchEndpoint.CURRENT.getSimplePort());
	}

	@Test
	public void testValidInstantiation()
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