package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

public class SearchFieldIdTest extends StringableTest
{
	
	@Test
	public void testConverter()
	{
		SearchFieldId defaulted = SearchFieldId.CONVERTER.fromString(" yEs YES ", null);
		assertEquals(defaulted.getSimpleValue(), "yes yes");
	}

	@Test
	public void inValid()
	{
		assertNotValid(null);
//		assertNotValid("foo/bar");
//		assertNotValid("foo:bar");
//		assertNotValid("foo!");
	}

	@Test
	public void valid()
	{
		assertValid("abb_1924 ", "abb_1924");
		assertValid("abb-1924", "abb-1924");
		assertValid("abB1924", "abb1924");
		assertValid("abb1924", "abb1924");
	}

	public Stringable fromString( String src )
	{
		return new SearchFieldId(src);
	}
	
}
