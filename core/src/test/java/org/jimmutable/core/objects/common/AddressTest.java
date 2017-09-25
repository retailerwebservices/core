package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AddressTest extends TestCase
{

	public AddressTest(String testName)
	{
		super(testName);
	}
	
	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(Address.class);
		return new TestSuite( AddressTest.class );
	}
	
	public void testAddress() 
	{
		
	}
	
	public void testBuilder()
	{
		Builder builder = new Builder(Address.TYPE_NAME);
		
		try
		{
			builder.create(null);
			assert(false);
		}
		catch(Exception e)
		{
			// expected
		}
		
		// Test Invalid US Address
//		builder.set(Address.FIELD_LINE1, "123 Main St");  // Required field left out
//		builder.set(Address.FIELD_CITY, "Phoenix");       // Required field left out
		builder.set(Address.FIELD_STATE, "AZ");
		builder.set(Address.FIELD_POSTAL_CODE, new PostalCode("85003"));
		builder.set(Address.FIELD_COUNTRY, CountryCode.US);
		try
		{
			@SuppressWarnings("unused")
			Address invalid = (Address)builder.create(null);
			assert(false);
		}
		catch(Exception e)
		{
			assert(true);
		}
		
		// Test Valid US Address
		builder.set(Address.FIELD_LINE1, "123 Main St");
		builder.set(Address.FIELD_CITY, "Phoenix");
		builder.set(Address.FIELD_STATE, "AZ");
		builder.set(Address.FIELD_POSTAL_CODE, new PostalCode("85003"));
		builder.set(Address.FIELD_COUNTRY, CountryCode.US);
		Address no_specs = (Address)builder.create(null);

		assert(no_specs != null);
		assert(no_specs.getSimpleLine1().equals("123 Main St"));
		assert(no_specs.getSimpleCity().equals("Phoenix"));
		assert(no_specs.getSimpleState().equals("AZ"));
		assert(no_specs.getSimplePostalCode().getSimpleValue().equals("85003"));
		assert(no_specs.getSimpleCountry() == CountryCode.US);
		
		// Test Valid Canadian Address with optional fields
		builder = new Builder(Address.TYPE_NAME);
		
		builder.set(Address.FIELD_NAME, "Bob Smith");
		builder.set(Address.FIELD_LINE1, "2713 Bridgeport Rd.");
		builder.set(Address.FIELD_LINE2, "test line 2");
		builder.set(Address.FIELD_LINE3, "test line 3");
		builder.set(Address.FIELD_CITY, "Milton");
		builder.set(Address.FIELD_STATE, "Ontario");
		builder.set(Address.FIELD_POSTAL_CODE, new PostalCode("L9T 2Y2"));
		builder.set(Address.FIELD_COUNTRY, CountryCode.CA);

		Address canadian_addr = (Address)builder.create(null);
		
		assert(canadian_addr != null);
		assert(canadian_addr.getOptionalName(null).equals("Bob Smith"));
		assert(canadian_addr.getSimpleLine1().equals("2713 Bridgeport Rd."));
		assert(canadian_addr.getOptionalLine2(null).equals("test line 2"));
		assert(canadian_addr.getOptionalLine3(null).equals("test line 3"));
		assert(canadian_addr.getSimpleCity().equals("Milton"));
		assert(canadian_addr.getSimpleState().equals("Ontario"));
		assert(canadian_addr.getSimplePostalCode().getSimpleValue().equals("L9T 2Y2"));
		assert(canadian_addr.getSimpleCountry() == CountryCode.CA);
		
		System.out.println(canadian_addr.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
		// Test optional fields
		builder = new Builder(Address.TYPE_NAME);
		builder.set(Address.FIELD_LINE1, "123 Main St");
		builder.set(Address.FIELD_CITY, "Phoenix");
		builder.set(Address.FIELD_STATE, "AZ");
		builder.set(Address.FIELD_POSTAL_CODE, new PostalCode("85003"));
		builder.set(Address.FIELD_COUNTRY, CountryCode.US);
		builder.set(Address.FIELD_LATITUDE, 10.03F);
		builder.set(Address.FIELD_LONGITUDE, 18.323F);
		
		Address addr_with_lat_long = (Address)builder.create(null);

		assert(addr_with_lat_long.getOptionalLatitude(999999999) == 10.03F);
		assert(addr_with_lat_long.getOptionalLongitude(999999999) == 18.323F);
		
		builder = new Builder(Address.TYPE_NAME);
		builder.set(Address.FIELD_LINE1, "123 Main St");
		builder.set(Address.FIELD_CITY, "Phoenix");
		builder.set(Address.FIELD_STATE, "AZ");
		builder.set(Address.FIELD_POSTAL_CODE, new PostalCode("85003"));
		builder.set(Address.FIELD_COUNTRY, CountryCode.US);
		Address addr_without_lat_long = (Address)builder.create(null);

		assert(addr_without_lat_long.getOptionalLatitude(999999999) == 999999999F);
		assert(addr_without_lat_long.getOptionalLongitude(999999999) == 999999999F);
	}
	
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.common.Address\","
			     , "  \"name\" : \"Bob Smith\","
			     , "  \"line1\" : \"2713 Bridgeport Rd.\","
			     , "  \"line2\" : \"test line 2\","
			     , "  \"line3\" : \"test line 3\","
			     , "  \"city\" : \"Milton\","
			     , "  \"state\" : \"Ontario\","
			     , "  \"postal_code\" : \"L9T 2Y2\","
			     , "  \"country\" : \"CA\","
			     , "  \"latitude\" : 2.0,"
			     , "  \"longitude\" : 5.0"
			     , "}"
			);

			Address obj = (Address)StandardObject.deserialize(obj_string);
			
			assert(obj.getOptionalName(null).equals("Bob Smith"));
			assert(obj.getSimpleLine1().equals("2713 Bridgeport Rd."));
			assert(obj.getOptionalLine2(null).equals("test line 2"));
			assert(obj.getOptionalLine3(null).equals("test line 3"));
			assert(obj.getSimpleCity().equals("Milton"));
			assert(obj.getSimpleState().equals("Ontario"));
			assert(obj.getSimplePostalCode().getSimpleValue().equals("L9T 2Y2"));
			assert(obj.getSimpleCountry() == CountryCode.CA);
			assert(obj.getOptionalLatitude(9999999) == 2F);
			assert(obj.getOptionalLongitude(9999999) == 5F);
	}

}
