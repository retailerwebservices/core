package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;

import org.jimmutable.util.StringableTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchDocumentIdTest extends StringableTest
{

	@Test
	public void testConverter()
	{
		SearchDocumentId defaulted = SearchDocumentId.CONVERTER.fromString("no.no.no", new SearchDocumentId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		assertNotValid(null);
		assertNotValid("foo/bar");
		assertNotValid("foo:bar");
		assertNotValid("");
		assertNotValid("foo!");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 257; i++) {
			sb.append('a');
		}

		assertNotValid(sb.toString());

	}

	@Test
	public void valid()
	{
		assertValid("ABB-1924", "abb-1924");
		assertValid("abb1924", "abb1924");
		assertValid("aBb1924", "abb1924");
	}

	@Override
	public Stringable fromString(String src)
	{
		return new SearchDocumentId(src);
	}

}
