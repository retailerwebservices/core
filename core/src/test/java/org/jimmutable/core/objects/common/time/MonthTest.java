package org.jimmutable.core.objects.common.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MonthTest
{

	@Test
	public void testCodeConverter()
	{
		assertEquals(Month.JANUARY, Month.CONVERTER.fromCode("january", null));
		assertEquals(Month.FEBRUARY, Month.CONVERTER.fromCode("february", null));
		assertEquals(Month.MARCH, Month.CONVERTER.fromCode("march", null));
		assertEquals(Month.APRIL, Month.CONVERTER.fromCode("april", null));
		assertEquals(Month.MAY, Month.CONVERTER.fromCode("may", null));
		assertEquals(Month.JUNE, Month.CONVERTER.fromCode("june", null));
		assertEquals(Month.JULY, Month.CONVERTER.fromCode("july", null));
		assertEquals(Month.AUGUST, Month.CONVERTER.fromCode("august", null));
		assertEquals(Month.SEPTEMBER, Month.CONVERTER.fromCode("september", null));
		assertEquals(Month.OCTOBER, Month.CONVERTER.fromCode("october", null));
		assertEquals(Month.NOVEMBER, Month.CONVERTER.fromCode("november", null));
		assertEquals(Month.DECEMBER, Month.CONVERTER.fromCode("december", null));
	}

	@Test
	public void testNegativeCodeConverter()
	{
		assertEquals(null, Month.CONVERTER.fromCode("jan", null));
	}

	@Test
	public void testMonthIdConverter()
	{
		assertEquals(Month.JANUARY, Month.CONVERTER.fromMonthId(1, null));
		assertEquals(Month.FEBRUARY, Month.CONVERTER.fromMonthId(2, null));
		assertEquals(Month.MARCH, Month.CONVERTER.fromMonthId(3, null));
		assertEquals(Month.APRIL, Month.CONVERTER.fromMonthId(4, null));
		assertEquals(Month.MAY, Month.CONVERTER.fromMonthId(5, null));
		assertEquals(Month.JUNE, Month.CONVERTER.fromMonthId(6, null));
		assertEquals(Month.JULY, Month.CONVERTER.fromMonthId(7, null));
		assertEquals(Month.AUGUST, Month.CONVERTER.fromMonthId(8, null));
		assertEquals(Month.SEPTEMBER, Month.CONVERTER.fromMonthId(9, null));
		assertEquals(Month.OCTOBER, Month.CONVERTER.fromMonthId(10, null));
		assertEquals(Month.NOVEMBER, Month.CONVERTER.fromMonthId(11, null));
		assertEquals(Month.DECEMBER, Month.CONVERTER.fromMonthId(12, null));
	}

	@Test
	public void testNegativeMonthIdConverter()
	{
		assertEquals(null, Month.CONVERTER.fromMonthId(0, null));
		assertEquals(null, Month.CONVERTER.fromMonthId(13, null));
	}

	@Test
	public void testToJavaMonths()
	{
		assertTrue(Month.JANUARY.getComplexJavaMonth(null) == java.time.Month.JANUARY);
		assertTrue(Month.FEBRUARY.getComplexJavaMonth(null) == java.time.Month.FEBRUARY);
		assertTrue(Month.MARCH.getComplexJavaMonth(null) == java.time.Month.MARCH);
		assertTrue(Month.APRIL.getComplexJavaMonth(null) == java.time.Month.APRIL);
		assertTrue(Month.MAY.getComplexJavaMonth(null) == java.time.Month.MAY);
		assertTrue(Month.JUNE.getComplexJavaMonth(null) == java.time.Month.JUNE);
		assertTrue(Month.JULY.getComplexJavaMonth(null) == java.time.Month.JULY);
		assertTrue(Month.AUGUST.getComplexJavaMonth(null) == java.time.Month.AUGUST);
		assertTrue(Month.SEPTEMBER.getComplexJavaMonth(null) == java.time.Month.SEPTEMBER);
		assertTrue(Month.OCTOBER.getComplexJavaMonth(null) == java.time.Month.OCTOBER);
		assertTrue(Month.NOVEMBER.getComplexJavaMonth(null) == java.time.Month.NOVEMBER);
		assertTrue(Month.DECEMBER.getComplexJavaMonth(null) == java.time.Month.DECEMBER);
	}

}
