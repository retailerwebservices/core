package org.jimmutable.core.objects.common;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class DomainNameTest extends TestCase
{
	private StringableTestingUtils<DomainName> tester = new StringableTestingUtils<DomainName>(new DomainName.MyConverter());
	
	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("google.com", "google.com"));
		assertTrue(tester.isValid("google.com3", "google.com3"));
		assertTrue(tester.isValid("Google.com3", "google.com3"));
		assertTrue(tester.isValid("G-oogle.com3", "g-oogle.com3"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid(".com"));
		assertTrue(tester.isInvalid("google."));
		assertTrue(tester.isInvalid("&&!(#$"));
	}
}
