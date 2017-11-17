package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class DomainNameTest extends TestCase
{
	private StringableTester<DomainName> tester = new StringableTester<DomainName>(new DomainName.MyConverter());
	@Test
	public void testValid()
	{
		tester.assertValid("google.com", "google.com");
		tester.assertValid("google.com3", "google.com3");
		tester.assertValid("Google.com3", "google.com3");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid(".com");
		tester.assertInvalid("google.");
		tester.assertInvalid("&&!(#$");
	}
}
