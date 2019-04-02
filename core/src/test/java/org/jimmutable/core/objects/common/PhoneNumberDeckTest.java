package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests both PhoneNumber and PhoneNumberDeck
 * 
 * @author jim.kane
 *
 */
public class PhoneNumberDeckTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PhoneNumberDeckTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	JimmutableTypeNameRegister.registerAllTypes();
    	
        return new TestSuite( PhoneNumberDeckTest.class );
    }
    
	public void testBuilder()
	{
		PhoneNumber mobile = new PhoneNumber("1231231234", PhoneNumberType.MOBILE);
		PhoneNumber work = new PhoneNumber("1111111111", PhoneNumberType.WORK);

		Builder phone_deck_builder = new Builder(PhoneNumberDeck.TYPE_NAME);
		phone_deck_builder.add(PhoneNumberDeck.FIELD_PHONE_NUMBERS, mobile);
		phone_deck_builder.add(PhoneNumberDeck.FIELD_PHONE_NUMBERS, work);
		PhoneNumberDeck deck = phone_deck_builder.create();

		PhoneNumberDeck deck2 = new PhoneNumberDeck(mobile, work);

		assertEquals(deck, deck2);
	}

    
    public void testManual()
    {
        PhoneNumber work = new PhoneNumber("555-867-5309", PhoneNumberType.WORK);
        
        PhoneNumber mobile1 = new PhoneNumber("555-212-5309", PhoneNumberType.MOBILE);
        PhoneNumber mobile2 = new PhoneNumber("111-212-3333", PhoneNumberType.MOBILE);
        
        PhoneNumber home = new PhoneNumber("111-999-8716", PhoneNumberType.HOME);
        
        PhoneNumber fax = new PhoneNumber("111-999-8717", PhoneNumberType.FAX);
        
        PhoneNumber other = new PhoneNumber("222-333-4448", PhoneNumberType.OTHER);
        
        PhoneNumberDeck deck = new PhoneNumberDeck(work, mobile1, mobile2, home, fax, other);
        
        assertEquals(work, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.WORK, null));
        assertEquals(mobile1, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.MOBILE, null));
        assertEquals(home, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.HOME, null));
        assertEquals(fax, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.FAX, null));
        assertEquals(other, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.OTHER, null));
        
        assertEquals(6, deck.getSimplePhoneNumbers().size());
        
        assertEquals(work, deck.getSimplePhoneNumbers().get(0));
        assertEquals(mobile1, deck.getSimplePhoneNumbers().get(1));
        assertEquals(mobile2, deck.getSimplePhoneNumbers().get(2));
        assertEquals(home, deck.getSimplePhoneNumbers().get(3));
        assertEquals(fax, deck.getSimplePhoneNumbers().get(4));
        assertEquals(other, deck.getSimplePhoneNumbers().get(5));
        
        //System.out.println(deck.toJavaCode(Format.XML_PRETTY_PRINT, "obj"));
    }
    
    public void testSerialization()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
    		     , "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
    		     , "  <type_hint>jimmutable.common.PhoneNumberDeck</type_hint>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-555-867-5309</digits>"
    		     , "    <type>work</type>"
    		     , "  </phone_numbers>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-555-212-5309</digits>"
    		     , "    <type>mobile</type>"
    		     , "  </phone_numbers>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-111-212-3333</digits>"
    		     , "    <type>mobile</type>"
    		     , "  </phone_numbers>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-111-999-8716</digits>"
    		     , "    <type>home</type>"
    		     , "  </phone_numbers>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-111-999-8717</digits>"
    		     , "    <type>fax</type>"
    		     , "  </phone_numbers>"
    		     , "  <phone_numbers>"
    		     , "    <type_hint>jimmutable.common.PhoneNumber</type_hint>"
    		     , "    <digits>1-222-333-4448</digits>"
    		     , "    <type>other</type>"
    		     , "  </phone_numbers>"
    		     , "</object>"
    		);

    	PhoneNumberDeck deck = (PhoneNumberDeck)StandardObject.deserialize(obj_string);

    	PhoneNumber work = new PhoneNumber("555-867-5309", PhoneNumberType.WORK);

    	PhoneNumber mobile1 = new PhoneNumber("555-212-5309", PhoneNumberType.MOBILE);
    	PhoneNumber mobile2 = new PhoneNumber("111-212-3333", PhoneNumberType.MOBILE);

    	PhoneNumber home = new PhoneNumber("111-999-8716", PhoneNumberType.HOME);

    	PhoneNumber fax = new PhoneNumber("111-999-8717", PhoneNumberType.FAX);

    	PhoneNumber other = new PhoneNumber("222-333-4448", PhoneNumberType.OTHER);


    	assertEquals(work, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.WORK, null));
    	assertEquals(mobile1, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.MOBILE, null));
    	assertEquals(home, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.HOME, null));
    	assertEquals(fax, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.FAX, null));
    	assertEquals(other, deck.getOptionalPrimaryPhoneNumber(PhoneNumberType.OTHER, null));

    	assertEquals(6, deck.getSimplePhoneNumbers().size());

    	assertEquals(work, deck.getSimplePhoneNumbers().get(0));
    	assertEquals(mobile1, deck.getSimplePhoneNumbers().get(1));
    	assertEquals(mobile2, deck.getSimplePhoneNumbers().get(2));
    	assertEquals(home, deck.getSimplePhoneNumbers().get(3));
    	assertEquals(fax, deck.getSimplePhoneNumbers().get(4));
    	assertEquals(other, deck.getSimplePhoneNumbers().get(5));
    }
}

