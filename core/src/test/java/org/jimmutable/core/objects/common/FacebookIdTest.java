package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class FacebookIdTest extends TestCase
{

	private StringableTester<FacebookId> tester = new StringableTester<FacebookId>(new FacebookId.MyConverter());
	@Test
	public void testValid()
	{
		tester.assertValid("Shadow8788", "shadow8788");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("a");
		tester.assertInvalid("   ");
		tester.assertInvalid(".com");
		tester.assertInvalid("google.");
		tester.assertInvalid("`~!@#$%^&*()_-+={{");
		String s = "";
		for(int i = 0 ;i<256; i++) {
			s=s+"a";
		}
		tester.assertInvalid(s);
	}
}