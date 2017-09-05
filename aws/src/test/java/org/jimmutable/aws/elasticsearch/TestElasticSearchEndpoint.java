package org.jimmutable.aws.elasticsearch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestElasticSearchEndpoint extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public TestElasticSearchEndpoint(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(TestElasticSearchEndpoint.class);
	}

	public void testNoEnvironmentvariable() {
		System.setProperty("elasticsearch.endpoint", "");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals("localhost", e.getSimpleHost());
		assertEquals(9200, e.getSimplePort());
	}

	public void testEnvironmentvariable() {
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals("my.host.name", e.getSimpleHost());
		assertEquals(1234, e.getSimplePort());
	}

	public void equals() {
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertEquals(e, e2);
	}

	public void notEquals() {
		System.setProperty("elasticsearch.endpoint", "my.host.name:1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		System.setProperty("elasticsearch.endpoint", "my.host.name:");
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertTrue(!e.equals(e2));
	}

	public void defaultedIfBadVariable() {
		// invalid port
		System.setProperty("elasticsearch.endpoint", "my.host.name:");
		ElasticSearchEndpoint e2 = new ElasticSearchEndpoint();
		assertEquals(9200, e2.getSimplePort());
		assertEquals("localhost", e2.getSimpleHost());

		// invalid host
		System.setProperty("elasticsearch.endpoint", ":1234");
		ElasticSearchEndpoint e = new ElasticSearchEndpoint();
		assertEquals(9200, e.getSimplePort());
		assertEquals("localhost", e.getSimpleHost());
	}

}