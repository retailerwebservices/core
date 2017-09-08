package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestElasticSearchEndpoint
{

	@Test
	public void testNoEnvironmentvariable()
	{
		System.setProperty("elasticsearch.endpoint", "");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals("localhost", e.getSimpleHost());
		assertEquals(9300, e.getSimplePort());
	}

	@Test
	public void testEnvironmentvariable()
	{
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals("my.host.name", e.getSimpleHost());
		assertEquals(1234, e.getSimplePort());
	}

	@Test
	public void equals()
	{
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertEquals(e, e2);
	}

	@Test
	public void notEquals()
	{
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		System.setProperty("elasticsearch.endpoint", "my.host.name:");
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertTrue(!e.equals(e2));
	}

	@Test
	public void defaultedIfBadVariable()
	{
		// invalid port
		System.setProperty("elasticsearch.endpoint", "my.host.name:");
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertEquals(9300, e2.getSimplePort());
		assertEquals("localhost", e2.getSimpleHost());

		// invalid host
		System.setProperty("elasticsearch.endpoint", ":1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals(9300, e.getSimplePort());
		assertEquals("localhost", e.getSimpleHost());
	}

}