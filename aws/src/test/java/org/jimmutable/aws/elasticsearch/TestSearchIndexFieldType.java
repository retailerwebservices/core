package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSearchIndexFieldType
{
	@Test
	public void testAtom()
	{
		assertEquals(SearchIndexFieldType.ATOM, SearchIndexFieldType.CONVERTER.fromCode("KEYWORD", null));
		assertEquals("keyword", SearchIndexFieldType.CONVERTER.fromCode("keyword", null).getSimpleCode());
	}

	@Test
	public void testBoolean()
	{
		assertEquals(SearchIndexFieldType.BOOLEAN, SearchIndexFieldType.CONVERTER.fromCode("BOOLEAN", null));
		assertEquals("boolean", SearchIndexFieldType.CONVERTER.fromCode("BOOLEAN", null).getSimpleCode());
	}

	@Test
	public void testDay()
	{
		assertEquals(SearchIndexFieldType.DAY, SearchIndexFieldType.CONVERTER.fromCode("date", null));
		assertEquals("date", SearchIndexFieldType.CONVERTER.fromCode("Date", null).getSimpleCode());
	}

	@Test
	public void testFloat()
	{
		assertEquals(SearchIndexFieldType.FLOAT, SearchIndexFieldType.CONVERTER.fromCode("FLOAT", null));
		assertEquals("float", SearchIndexFieldType.CONVERTER.fromCode("FLOAT", null).getSimpleCode());
	}

	@Test
	public void testLong()
	{
		assertEquals(SearchIndexFieldType.LONG, SearchIndexFieldType.CONVERTER.fromCode("LONG", null));
		assertEquals("long", SearchIndexFieldType.CONVERTER.fromCode("LONG", null).getSimpleCode());
	}

	@Test
	public void testText()
	{
		assertEquals(SearchIndexFieldType.TEXT, SearchIndexFieldType.CONVERTER.fromCode("TEXT", null));
		assertEquals("text", SearchIndexFieldType.CONVERTER.fromCode("TEXT", null).getSimpleCode());
	}

}
