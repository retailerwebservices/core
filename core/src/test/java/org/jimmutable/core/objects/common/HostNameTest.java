package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class HostNameTest extends TestCase
{
	private StringableTestingUtils<HostName> tester = new StringableTestingUtils<HostName>(new HostName.MyConverter());
	
	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("https://www.google.com", "www.google.com"));
		assertTrue(tester.isValid("http://www.FARK.com/index.html", "www.fark.com"));
		assertTrue(tester.isValid("http://www.FARK9-.com/index.html", "www.fark9-.com"));
		assertTrue(tester.isValid("www.fark9-.com", "www.fark9-.com"));
		assertTrue(tester.isValid("www.lol.fark9-.com", "www.lol.fark9-.com"));
		assertTrue(tester.isValid("G.Q", "g.q"));
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
