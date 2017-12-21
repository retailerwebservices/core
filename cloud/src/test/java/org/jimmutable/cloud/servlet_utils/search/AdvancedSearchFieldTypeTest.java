package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AdvancedSearchFieldTypeTest
{

	@Test
	public void testCheckBox()
	{
		assertEquals(AdvancedSearchFieldType.CHECK_BOX, AdvancedSearchFieldType.CONVERTER.fromCode("checkbox", null));
		assertEquals("checkbox", AdvancedSearchFieldType.CONVERTER.fromCode("CHECKBOX", null).getSimpleCode());
	}

	@Test
	public void testComboBox()
	{
		assertEquals(AdvancedSearchFieldType.COMBO_BOX, AdvancedSearchFieldType.CONVERTER.fromCode("combo-box", null));
		assertEquals("combo-box", AdvancedSearchFieldType.CONVERTER.fromCode("COMBO-BOX", null).getSimpleCode());
	}

	@Test
	public void testText()
	{
		assertEquals(AdvancedSearchFieldType.TEXT, AdvancedSearchFieldType.CONVERTER.fromCode("text", null));
		assertEquals("text", AdvancedSearchFieldType.CONVERTER.fromCode("TExt", null).getSimpleCode());
	}

	@Test
	public void testDay()
	{
		assertEquals(AdvancedSearchFieldType.DAY, AdvancedSearchFieldType.CONVERTER.fromCode("day", null));
		assertEquals("day", AdvancedSearchFieldType.CONVERTER.fromCode("DAY", null).getSimpleCode());
	}

	@Test
	public void testInstant()
	{
		assertEquals(AdvancedSearchFieldType.INSTANT, AdvancedSearchFieldType.CONVERTER.fromCode("instant", null));
		assertEquals("instant", AdvancedSearchFieldType.CONVERTER.fromCode("INSTANT", null).getSimpleCode());
	}

}
