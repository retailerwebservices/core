package org.jimmutable.core.objects.common;

import org.jimmutable.core.AppTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PhoneNumberTypeTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PhoneNumberTypeTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PhoneNumberTypeTest.class );
    }

    
    public void testPhoneNumberType()
    {
    	assertEquals(PhoneNumberType.HOME, PhoneNumberType.CONVERTER.fromCode("home", null));
    	assertEquals(PhoneNumberType.FAX, PhoneNumberType.CONVERTER.fromCode("fax", null));
    	assertEquals(PhoneNumberType.MOBILE, PhoneNumberType.CONVERTER.fromCode("mobile", null));
    	assertEquals(PhoneNumberType.OTHER, PhoneNumberType.CONVERTER.fromCode("other", null));
    	assertEquals(PhoneNumberType.WORK, PhoneNumberType.CONVERTER.fromCode("work", null));
    	
    	
    	assertEquals(PhoneNumberType.HOME, PhoneNumberType.CONVERTER.fromCode("HOME", null));
    	assertEquals(PhoneNumberType.FAX, PhoneNumberType.CONVERTER.fromCode("FAX", null));
    	assertEquals(PhoneNumberType.MOBILE, PhoneNumberType.CONVERTER.fromCode(" MOBILE ", null));
    	assertEquals(PhoneNumberType.OTHER, PhoneNumberType.CONVERTER.fromCode(" OTHER", null));
    	assertEquals(PhoneNumberType.WORK, PhoneNumberType.CONVERTER.fromCode("WoRk", null));
    	
    	assertEquals(null, PhoneNumberType.CONVERTER.fromCode("zztop", null));
    	
    	assertEquals(PhoneNumberType.OTHER, PhoneNumberType.CONVERTER.fromCode("", PhoneNumberType.OTHER));
    	assertEquals(PhoneNumberType.OTHER, PhoneNumberType.CONVERTER.fromCode(null, PhoneNumberType.OTHER));
    }
}

