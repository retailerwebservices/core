package org.jimmutable.cloud.cache;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class CacheKeyTest
{
	private StringableTester<CacheKey> tester = new StringableTester(new CacheKey.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo://bar", "foo://bar");
		tester.assertValid("foo:// bar ", "foo://bar");
		tester.assertValid("foo:// BaZ ", "foo://BaZ");
		
		tester.assertValid("/foo://bar", "foo://bar");
		tester.assertValid("/foo//://bar", "foo://bar");
		tester.assertValid("/FOO//://bar", "foo://bar");
		
		tester.assertValid("/foo/bar/baz://one", "foo/bar/baz://one");
		tester.assertValid("FOO////BAR/////BAZ://one", "foo/bar/baz://one");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("/foo");
		tester.assertInvalid("foo/");
		tester.assertInvalid("foo.bar");
		
		tester.assertInvalid("://foo");
		tester.assertInvalid("bar:baz://foo");
		tester.assertInvalid("$messaging/foo/bar://foo");
	}
}
