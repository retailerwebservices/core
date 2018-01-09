package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class SearchDocumentIdTest
{
	private StringableTestingUtils<SearchDocumentId> tester = new StringableTestingUtils(new SearchDocumentId.MyConverter());

	@Test
	public void testConverter()
	{
		SearchDocumentId defaulted = SearchDocumentId.CONVERTER.fromString("no.no.no", new SearchDocumentId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid("foo/bar"));
		assertTrue(tester.assertInvalid("foo:bar"));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("foo!"));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++)
		{
			sb.append('a');
		}

		assertTrue(tester.assertInvalid(sb.toString()));

	}

	@Test
	public void valid()
	{
		assertTrue(tester.assertValid("ABB-1924", "abb-1924"));
		assertTrue(tester.assertValid("abb1924", "abb1924"));
		assertTrue(tester.assertValid("aBb1924", "abb1924"));
	}
}
