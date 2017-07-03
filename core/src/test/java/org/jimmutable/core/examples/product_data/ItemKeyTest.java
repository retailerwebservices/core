package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.examples.product_data.BrandCode;
import org.jimmutable.core.examples.product_data.ItemKey;
import org.jimmutable.core.examples.product_data.PartNumber;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ItemKeyTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ItemKeyTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	JimmutableTypeNameRegister.registerAllTypes();
        return new TestSuite( ItemKeyTest.class );
    }

    
    public void testApp()
    {
        ItemKey one = new ItemKey("foo","bar");
        ItemKey two = new ItemKey(new BrandCode("FOO"),new PartNumber("BAR"));
        ItemKey three = new ItemKey("ge","WTW9600");
        
        assertEquals(one.toString(),"FOO:BAR");
        
        assertEquals(one,two);
        assert(!one.equals(three));
        
        assert(one.isComplete());
        assert(one.getSimpleBrand().isComplete());
        assert(one.getSimplePN().isComplete());
        
        assert(two.isComplete());
        assert(two.getSimpleBrand().isComplete());
        assert(two.getSimplePN().isComplete());
        
        System.out.println(one.toJavaCode(Format.XML_PRETTY_PRINT, "obj"));
    }
    
    public void testSerialization()
    {
    	String obj_string = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
    			, "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
    			, "  <type_hint>jimmutable.examples.ItemKey</type_hint>"
    			, "  <brand>FOO</brand>"
    			, "  <pn>BAR</pn>"
    			, "</object>"
    			);

    	ItemKey obj = (ItemKey)StandardObject.deserialize(obj_string);

    	assertEquals(obj.getSimpleBrand(), new BrandCode("foo"));
    	assertEquals(obj.getSimplePN(), new PartNumber("bar"));
    }
}
