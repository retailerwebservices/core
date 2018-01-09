package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class IndexVersionTest
{
	private StringableTestingUtils<IndexVersion> tester = new StringableTestingUtils(new IndexVersion.MyConverter());

	@Test
	public void valid()
	{
		assertTrue(tester.isValid("v0192837465", "v0192837465"));
		assertTrue(tester.isValid(" V2 ", "v2"));
	}

	@Test
	public void inValid()
	{
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("v"));
		assertTrue(tester.isInvalid("vv"));
		assertTrue(tester.isInvalid("v 2"));
	}
}
