package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class HostNameTest extends TestCase
{
	private StringableTester<HostName> tester = new StringableTester<HostName>(new HostName.MyConverter());
	@Test
	public void testValid()
	{
		tester.assertValid("https://www.google.com", "www.google.com");
		tester.assertValid("http://www.FARK.com/index.html", "www.fark.com");
		tester.assertValid("http://www.FARK9-.com/index.html", "www.fark9-.com");
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
