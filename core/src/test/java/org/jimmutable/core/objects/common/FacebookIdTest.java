package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class FacebookIdTest extends TestCase
{

	private StringableTestingUtils<FacebookId> tester = new StringableTestingUtils<FacebookId>(new FacebookId.MyConverter());
	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("Shadow8788", "shadow8788"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("a"));
		assertTrue(tester.isInvalid("   "));
		assertTrue(tester.isInvalid(".com"));
		assertTrue(tester.isInvalid("google."));
		assertTrue(tester.isInvalid("`~!@#$%^&*()_-+={{"));
		String s = "";
		for(int i = 0 ;i<256; i++) {
			s=s+"a";
		}
		assertTrue(tester.isInvalid(s));
	}
}