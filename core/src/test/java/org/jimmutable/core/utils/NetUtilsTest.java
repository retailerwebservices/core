package org.jimmutable.core.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NetUtilsTest extends TestCase
{
	 /* Create the test case
     *
     * @param testName name of the test case
     */
    public NetUtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NetUtilsTest.class );
    }

    public void testExtractHostAndPort()
    {
    		assertEquals(NetUtils.extractHostFromHostPortPair("www.google.com", null), "www.google.com");
    		assertEquals(NetUtils.extractHostFromHostPortPair("www.slashdot.org", null), "www.slashdot.org");
    		assertEquals(NetUtils.extractHostFromHostPortPair("google.com", null), "google.com");
    		assertEquals(NetUtils.extractHostFromHostPortPair("google.com:8080", null), "google.com");
    		assertEquals(NetUtils.extractHostFromHostPortPair("google.com:", null), "google.com");
    		
    		assertEquals(NetUtils.extractHostFromHostPortPair(null, null), null);
    		assertEquals(NetUtils.extractHostFromHostPortPair("", null), null);
    		assertEquals(NetUtils.extractHostFromHostPortPair(" ", null), null);
    		assertEquals(NetUtils.extractHostFromHostPortPair(":", null), null);
    		assertEquals(NetUtils.extractHostFromHostPortPair("  : ", null), null);
    		
    		
    		assertEquals(NetUtils.extractPortFromHostPortPair(null, -1), -1);
    		assertEquals(NetUtils.extractPortFromHostPortPair("", -1), -1);
    		assertEquals(NetUtils.extractPortFromHostPortPair(" ", -1), -1);
    		assertEquals(NetUtils.extractPortFromHostPortPair(":", -1), -1);
    		assertEquals(NetUtils.extractPortFromHostPortPair("  : ", -1), -1);
    		
    		
    		assertEquals(NetUtils.extractPortFromHostPortPair("www.google.com", 80), 80);
    		assertEquals(NetUtils.extractPortFromHostPortPair("www.slashdot.org", 80), 80);
    		assertEquals(NetUtils.extractPortFromHostPortPair("google.com", 80), 80);
    		assertEquals(NetUtils.extractPortFromHostPortPair("google.com:8080", 80), 8080);
    		assertEquals(NetUtils.extractPortFromHostPortPair("google.com: 8080 ", 80), 8080);
    		assertEquals(NetUtils.extractPortFromHostPortPair("google.com:", 80), 80);
    }
}
