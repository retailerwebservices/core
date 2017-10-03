package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

public class SearchFieldIdTest extends StringableTest
{
	
	@Test
	public void testConverter()
	{
		SearchFieldId defaulted = SearchFieldId.CONVERTER.fromString("no.no.no", new SearchFieldId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		assertNotValid(null);
		assertNotValid("foo/bar");
		assertNotValid("foo:bar");
		assertNotValid("foo!");

	}

	@Test
	public void valid()
	{
		assertValid("abb_1924 ", "abb-1924");
		assertValid("abb-1924", "abb-1924");
		assertValid("abb1924", "abb1924");
	}

	public Stringable fromString( String src )
	{
		return new SearchFieldId(src);
	}
	
}
