package org.jimmutable.cloud.cache;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class CacheKeyTest
{
	private StringableTestingUtils<CacheKey> tester = new StringableTestingUtils(new CacheKey.MyConverter());

	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("foo://bar", "foo://bar"));
		assertTrue(tester.isValid("foo:// bar ", "foo://bar"));
		assertTrue(tester.isValid("foo:// BaZ ", "foo://BaZ"));
		
		assertTrue(tester.isValid("/foo://bar", "foo://bar"));
		assertTrue(tester.isValid("/foo//://bar", "foo://bar"));
		assertTrue(tester.isValid("/FOO//://bar", "foo://bar"));
		
		assertTrue(tester.isValid("/foo/bar/baz://one", "foo/bar/baz://one"));
		assertTrue(tester.isValid("FOO////BAR/////BAZ://one", "foo/bar/baz://one"));
		
		assertTrue(tester.isValid("foo:// https://www.google.com/index?p1=2 ", "foo://https://www.google.com/index?p1=2"));
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("/foo"));
		assertTrue(tester.isInvalid("foo/"));
		assertTrue(tester.isInvalid("foo.bar"));
		
		assertTrue(tester.isInvalid("://foo"));
		assertTrue(tester.isInvalid("bar:baz://foo"));
		assertTrue(tester.isInvalid("$messaging/foo/bar://foo"));
	}
}
