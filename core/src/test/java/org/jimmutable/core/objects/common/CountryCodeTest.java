package org.jimmutable.core.objects.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CountryCodeTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
	public CountryCodeTest(String testName)
	{
		super(testName);
	}

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CountryCodeTest.class );
    }
    
	public void testCountryCode()
	{
		assertEquals(CountryCode.CA, CountryCode.CONVERTER.fromCode("CA", null));
		assertEquals("CA", CountryCode.CONVERTER.fromCode("CA", null).getSimpleCode2ltr());
		assertEquals("CAN", CountryCode.CONVERTER.fromCode("CAN", null).getSimpleCode3ltr());
		assertEquals(CountryCode.US, CountryCode.CONVERTER.fromCode("US", null));
		assertEquals("US", CountryCode.CONVERTER.fromCode("US", null).getSimpleCode2ltr());
		assertEquals("USA", CountryCode.CONVERTER.fromCode("USA", null).getSimpleCode3ltr());
		
		assertEquals(CountryCode.CA, CountryCode.CONVERTER.fromCode("Ca", null));
		assertEquals("CA", CountryCode.CONVERTER.fromCode("CA", null).getSimpleCode2ltr());
		assertEquals("CAN", CountryCode.CONVERTER.fromCode("CAN", null).getSimpleCode3ltr());
		assertEquals(CountryCode.US, CountryCode.CONVERTER.fromCode("US", null));
		assertEquals("US", CountryCode.CONVERTER.fromCode("US", null).getSimpleCode2ltr());
		assertEquals("USA", CountryCode.CONVERTER.fromCode("USA", null).getSimpleCode3ltr());
		
		assertEquals(null, CountryCode.CONVERTER.fromCode("ZZ", null));
		
		assertEquals(CountryCode.US, CountryCode.CONVERTER.fromCode("", CountryCode.US));
		assertEquals(CountryCode.US, CountryCode.CONVERTER.fromCode(null, CountryCode.US));
	}

}
