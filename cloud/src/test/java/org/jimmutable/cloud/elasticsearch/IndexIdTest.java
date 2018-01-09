package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class IndexIdTest
{
	private StringableTestingUtils<IndexId> tester = new StringableTestingUtils(new IndexId.MyConverter());

	@Test
	public void testConverter()
	{
		IndexId defaulted = IndexId.CONVERTER.fromString("no.no.no", new IndexId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid("foo/bar"));
		assertTrue(tester.isInvalid("foo:bar"));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("foo!"));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++)
		{
			sb.append('a');
		}

		assertTrue(tester.isInvalid(sb.toString()));

		assertTrue(tester.isInvalid("12"));

	}

	@Test
	public void valid()
	{
		assertTrue(tester.isValid("ABB1924", "abb1924"));
		assertTrue(tester.isValid("abb1924", "abb1924"));
		assertTrue(tester.isValid("aBb1924", "abb1924"));
	}
}
