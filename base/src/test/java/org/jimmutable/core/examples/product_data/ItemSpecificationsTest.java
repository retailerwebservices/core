package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.examples.product_data.ItemAttribute;
import org.jimmutable.core.examples.product_data.ItemKey;
import org.jimmutable.core.examples.product_data.ItemSpecifications;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ItemSpecificationsTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public ItemSpecificationsTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		return new TestSuite( ItemSpecificationsTest.class );
	}

	public void testBuilder()
	{
		ItemSpecifications.Builder builder = new ItemSpecifications.Builder();
		
		try
		{
			builder.create();
			assert(false); // error, creation worked without a key set
		}
		catch(Exception e)
		{
			// expect this, key no set
		}
		
		builder.setItemKey(new ItemKey("foo","bar"));
		
		ItemSpecifications no_specs = builder.create();
		
		assertEquals(no_specs.getSimpleItemKey(),new ItemKey("FOO","BAR"));
		
		assert(no_specs.isComplete());
		assert(no_specs.getSimpleItemKey().isComplete());
		assert(no_specs.getSimpleAttributes().isFrozen());
		
		assert(no_specs.getSimpleAttributes().isEmpty());
		
		builder = new ItemSpecifications.Builder(no_specs);
		 
		builder.putAttribute(new ItemAttribute("BRAND"), "foo");
		builder.putAttribute(new ItemAttribute("BRAND"), "bar");
		builder.putAttribute(new ItemAttribute("DOC_SRC_URL0"), "http://toolbox.legacyclassic.com/customer_images/assemblypdf/490-8900_Assembly.pdf");
		builder.putAttribute(new ItemAttribute("DOC_SRC_FILE0"), "FRBJHKQALSYB.PDF");
		
		ItemSpecifications some_specs = builder.create();
		
		assertEquals(some_specs.getSimpleItemKey(),new ItemKey("FOO","BAR"));
		
		assertEquals(some_specs.getSimpleAttributes().size(),2);
		
		assertEquals(some_specs.getSimpleAttributes().get(new ItemAttribute("DOC_SRC_URL0")),"http://toolbox.legacyclassic.com/customer_images/assemblypdf/490-8900_Assembly.pdf");
		assertEquals(some_specs.getSimpleAttributes().get(new ItemAttribute("DOC_SRC_FILE0")),"FRBJHKQALSYB.PDF");
		
		assertEquals(some_specs,some_specs);
		assert(!some_specs.equals(no_specs));
		
		System.out.println(some_specs.toJavaCode(Format.XML_PRETTY_PRINT, "obj"));
	}
	
	public void testSerialization()
	{
		String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
				, "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
				, "  <type_hint>jimmutable.examples.ItemSpecifications</type_hint>"
				, "  <item_key>"
				, "    <type_hint>jimmutable.examples.ItemKey</type_hint>"
				, "    <brand>FOO</brand>"
				, "    <pn>BAR</pn>"
				, "  </item_key>"
				, "  <attributes>"
				, "    <type_hint>MapEntry</type_hint>"
				, "    <key>DOC_SRC_FILE0</key>"
				, "    <value>FRBJHKQALSYB.PDF</value>"
				, "  </attributes>"
				, "  <attributes>"
				, "    <type_hint>MapEntry</type_hint>"
				, "    <key>DOC_SRC_URL0</key>"
				, "    <value>http://toolbox.legacyclassic.com/customer_images/assemblypdf/490-8900_Assembly.pdf</value>"
				, "  </attributes>"
				, "</object>"
				);

		ItemSpecifications some_specs = (ItemSpecifications)StandardObject.deserialize(obj_string);
		
		assertEquals(some_specs.getSimpleItemKey(),new ItemKey("FOO","BAR"));
		
		assertEquals(some_specs.getSimpleAttributes().size(),2);
		
		assertEquals(some_specs.getSimpleAttributes().get(new ItemAttribute("DOC_SRC_URL0")),"http://toolbox.legacyclassic.com/customer_images/assemblypdf/490-8900_Assembly.pdf");
		assertEquals(some_specs.getSimpleAttributes().get(new ItemAttribute("DOC_SRC_FILE0")),"FRBJHKQALSYB.PDF");
	}
}
