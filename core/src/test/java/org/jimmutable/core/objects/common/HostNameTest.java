package org.jimmutable.core.objects.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class HostNameTest
{
	private StringableTestingUtils<HostName> tester = new StringableTestingUtils<HostName>(new HostName.MyConverter());

	@Test
	public void stripPort()
	{
		assertEquals("adrocket-beta-3.rwsgateway.com", new HostName("http://adrocket-beta-3.rwsgateway.com:8080/admin/admin-tools.html").getSimpleValue());
	}

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("https://www.google.com", "www.google.com"));
		assertTrue(tester.isValid("http://www.FARK.com/index.html", "www.fark.com"));
		assertTrue(tester.isValid("http://www.FARK9-.com/index.html", "www.fark9-.com"));
		assertTrue(tester.isValid("fark.com", "fark.com"));
		assertTrue(tester.isValid("www.fark9-.com", "www.fark9-.com")); // I don't believe this is actually allowed in a
																		// hostname but perhaps not a big deal right
																		// now. From Java URI: Each label consists of
																		// <i>alphanum</i> characters
		// * as well as hyphen characters ({@code '-'}), though hyphens never
		// * occur as the first or last characters in a label.
		assertTrue(tester.isValid("www.lol.fark9-.com", "www.lol.fark9-.com"));
		assertTrue(tester.isValid("G.Q", "g.q"));
		
		assertTrue(tester.isValid("https://abcdiscountappliance.com/_CGI/SEARCH3?MINOR=BBQ:BBQPRO|AC:BBQAC|BBQ:BBQNG|BBQ:BBQCH|BBQ:BBQLP|BBQ:BBQEL&MAN=BROILKING&HEADER_IMG=BBQ.JPG&MAJOR_SORT=BBQ&utm_source=April&utm_medium=Display&utm_campaign=IC", "abcdiscountappliance.com"));
		
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("."));
		assertTrue(tester.isInvalid(".com"));
		assertTrue(tester.isInvalid("google."));
		assertTrue(tester.isInvalid("&&!(#$"));
	}
}
