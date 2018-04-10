package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SortDirectionTest
{
	@Test
	public void testAscending()
	{
		assertEquals(SortDirection.ASCENDING, SortDirection.CONVERTER.fromCode("ASCENDING", null));
		assertEquals("ascending", SortDirection.CONVERTER.fromCode("ascending", null).getSimpleCode());
	}

	@Test
	public void testDescending()
	{
		assertEquals(SortDirection.DESCENDING, SortDirection.CONVERTER.fromCode("DESCENDING", null));
		assertEquals("descending", SortDirection.CONVERTER.fromCode("descending", null).getSimpleCode());
	}


}
