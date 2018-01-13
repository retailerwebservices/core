package org.jimmutable.core.objects.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.USDMonetaryAmount;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.BeforeClass;
import org.junit.Test;


public class USDMonetaryAmountTest
{

	@BeforeClass
	public static void setUpBeforeClass() 
	{
		JimmutableTypeNameRegister.registerAllTypes();
	}
	
	@Test
	public void testUserSerialization()
	{
		
		USDMonetaryAmount test_business = new USDMonetaryAmount(20l);
		String serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 20,\n" + "  \"text\" : \"$.20\"\n" + "}", serialized_value);
		System.out.println(test_business.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"usd_monetary_amount\",", "  \"amount_in_cents\" : 20,", "  \"text\" : \"$.20\"", "}");

		USDMonetaryAmount obj = (USDMonetaryAmount) StandardObject.deserialize(obj_string);

		assertEquals(20l, obj.getSimpleAmountInCents());
	}
	
	@Test
	public void testOtherSerialization()
	{
		USDMonetaryAmount test_business = new USDMonetaryAmount("20");
		String serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 2000,\n" + "  \"text\" : \"$20.00\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount("$20");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 2000,\n" + "  \"text\" : \"$20.00\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount(".20");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 20,\n" + "  \"text\" : \"$.20\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount(".2");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 20,\n" + "  \"text\" : \"$.20\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount(".002");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 0,\n" + "  \"text\" : \"$.00\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount(".012");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 1,\n" + "  \"text\" : \"$.01\"\n" + "}", serialized_value);
		
		test_business = new USDMonetaryAmount("$1,000,000,000,000.00");
		serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : 100000000000000,\n" + "  \"text\" : \"$1,000,000,000,000.00\"\n" + "}", serialized_value);
		
		boolean correct = false;
		try {
		test_business = new USDMonetaryAmount("YOYOMA");
		}catch(org.jimmutable.core.exceptions.ValidationException e) {
			correct = true;
		}
		assertTrue(correct);
	}
	
	@Test
	public void testNegativeValues()
	{
		USDMonetaryAmount test_business = new USDMonetaryAmount(-1l);
		assertEquals(-1l, test_business.getSimpleAmountInCents());
		String serialized_value = test_business.serialize(Format.JSON_PRETTY_PRINT);
		assertEquals("{\n" + "  \"type_hint\" : \"usd_monetary_amount\",\n" + "  \"amount_in_cents\" : -1,\n" + "  \"text\" : \"-$.01\"\n" + "}", serialized_value);
	
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		USDMonetaryAmount test_business = new USDMonetaryAmount(1l);
		USDMonetaryAmount test_business2 = new USDMonetaryAmount(1l);
		assertTrue(test_business.equals(test_business2));
		assertEquals(0, test_business.compareTo(test_business2));

		test_business2 = new USDMonetaryAmount(0l);
		assertFalse(test_business.equals(test_business2));
		assertEquals(1, test_business.compareTo(test_business2));

		test_business2 = new USDMonetaryAmount(2l);
		assertFalse(test_business.equals(test_business2));
		assertEquals(-1, test_business.compareTo(test_business2));
	}

	@Test
	public void testConvertFromLongToString()
	{
		String s = USDMonetaryAmount.convertFromLongToString(200l);
		assertEquals("$2.00", s);

		s = USDMonetaryAmount.convertFromLongToString(123121l);
		assertEquals("$1,231.21", s);

		s = USDMonetaryAmount.convertFromLongToString(1232l);
		assertEquals("$12.32", s);

		s = USDMonetaryAmount.convertFromLongToString(1277l);
		assertEquals("$12.77", s);

		s = USDMonetaryAmount.convertFromLongToString(10700l);
		assertEquals("$107.00", s);

		s = USDMonetaryAmount.convertFromLongToString(180219200l);
		assertEquals("$1,802,192.00", s);
	}

	@Test
	public void testConvertFromStringToLong()
	{
		long l = USDMonetaryAmount.convertFromStringToLong("$2.00");
		assertEquals(200l, l);

		l = USDMonetaryAmount.convertFromStringToLong("$1,231.21");
		assertEquals(123121, l);

		l = USDMonetaryAmount.convertFromStringToLong("12.32");
		assertEquals(1232l, l);

		l = USDMonetaryAmount.convertFromStringToLong("$12.77");
		assertEquals(1277l, l);

		l = USDMonetaryAmount.convertFromStringToLong("107");
		assertEquals(10700l, l);

		l = USDMonetaryAmount.convertFromStringToLong("107.0");
		assertEquals(10700l, l);

		l = USDMonetaryAmount.convertFromStringToLong("$1,802,192");
		assertEquals(180219200l, l);
	}
}
