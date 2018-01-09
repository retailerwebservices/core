package org.jimmutable.core.objects.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jimmutable.core.exceptions.ValidationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DayTest extends TestCase 
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public DayTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( DayTest.class );
	}
	
	public void testValidDay()
	{
		Day day = new Day("2/8/1981");
		
		assertEquals(1981, day.getSimpleYear());
		assertEquals(2, day.getSimpleMonthOfYear());
		assertEquals(8, day.getSimpleDayOfMonth());
		
		assertEquals("Sunday", day.getSimpleDayName());
		assertEquals("Sun", day.getSimpleThreeLetterDayName());
		
		assertEquals("February", day.getSimpleMonthName());
		assertEquals("Feb", day.getSimpleThreeLetterMonthName());
	}
	
	public void testShifts()
	{
		assertEquals(new Day("2/7/1981"), new Day("2/8/1981").createSimpleYesterday());
		assertEquals(new Day("2/9/1981"), new Day("2/8/1981").createSimpleTomorrow());
		
		assertEquals(new Day("1/31/1981"), new Day("2/1/1981").createSimpleYesterday());
		assertEquals(new Day("2/1/1981"), new Day("1/31/1981").createSimpleTomorrow());
		
		assertEquals(new Day("2/28/1981"), new Day("1/31/1981").createSimpleAddMonths(1));
		
		assertEquals(new Day("2/8/1982"), new Day("2/8/1981").createSimpleAddYears(1));
	}
	
	public void testInvalid()
	{
		assertInvalid(null);
		assertInvalid("");
		assertInvalid("something");
		assertInvalid("1/1/1/a");
		assertInvalid("//////");
		assertInvalid("2/8/81");
		assertInvalid("2/31/1981");
		assertInvalid("0/1/1981");
		assertInvalid("13/1/1981");
	}
	
	public void testSort()
	{
		List<Day> test = new ArrayList();
		test.add(new Day("1/1/1981"));
		test.add(new Day("2/1/1980"));
		test.add(new Day("3/1/1979"));
		
		Collections.sort(test);
	
		assertEquals(new Day("3/1/1979"), test.get(0));
		assertEquals(new Day("2/1/1980"), test.get(1));
		assertEquals(new Day("1/1/1981"), test.get(2));
	}
	
	private void assertInvalid(String str)
	{
		try
		{
			new Day(str);
			fail();
		}
		catch(ValidationException e)
		{
			assertTrue(true);
		}
	}
	
	public void testDifference()
	{
		Day first_feb = new Day("2/1/2018");
		Day second_feb = new Day("2/2/2018");
		Day fifth_feb = new Day("2/5/2018");
		
		assertEquals(first_feb.getSimpleDaysBetween(first_feb), 0);
		assertEquals(first_feb.getSimpleDaysBetween(second_feb), 1);
		
		assertEquals(second_feb.getSimpleDaysBetween(fifth_feb), 3);
		assertEquals(first_feb.getSimpleDaysBetween(fifth_feb), 4);
	}
	
	public void testBeforeAndAfter()
	{
		Day first_feb = new Day("2/1/2018");
		Day second_feb = new Day("2/2/2018");
		Day fifth_feb = new Day("2/5/2018");
		
		assertEquals(first_feb.isBefore(first_feb), false);
		assertEquals(first_feb.isBefore(second_feb), true);
		assertEquals(first_feb.isBefore(fifth_feb), true);
		
		assertEquals(second_feb.isBefore(first_feb), false);
		assertEquals(second_feb.isBefore(second_feb), false);
		assertEquals(second_feb.isBefore(fifth_feb), true);
		
		assertEquals(fifth_feb.isBefore(first_feb), false);
		assertEquals(fifth_feb.isBefore(second_feb), false);
		assertEquals(fifth_feb.isBefore(fifth_feb), false);
		
		assertEquals(first_feb.isAfter(first_feb), false);
		assertEquals(first_feb.isAfter(second_feb), false);
		assertEquals(first_feb.isAfter(fifth_feb), false);
		
		assertEquals(second_feb.isAfter(first_feb), true);
		assertEquals(second_feb.isAfter(second_feb), false);
		assertEquals(second_feb.isAfter(fifth_feb), false);
		
		assertEquals(fifth_feb.isAfter(first_feb), true);
		assertEquals(fifth_feb.isAfter(second_feb), true);
		assertEquals(fifth_feb.isAfter(fifth_feb), false);
	}
}