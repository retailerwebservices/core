package org.jimmutable.core.serialization;

import java.util.Objects;

import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.StandardWritable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ObjectFieldReadWriteTest extends TestCase
{
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ObjectFieldReadWriteTest( String testName )
    {
        super( testName );
        
        ObjectParseTree.registerTypeName(StringFieldBoundaryTest.class);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ObjectFieldReadWriteTest.class );
    }
    
	static public class StringFieldBoundaryTest implements StandardWritable
	{
		static public TypeName TYPE_NAME = new TypeName("serialization_tests.StringFieldBoundaryTest");
		
		static private FieldName FIELD_STRING_MY_STRING = new FieldName("my_string");
		static private FieldName FIELD_STRING_MY_STRING_EXPLICIT = new FieldName("my_string_explicit");
		
		public String my_string;
		
		public StringFieldBoundaryTest() 
		{
		}
		
		public StringFieldBoundaryTest(ObjectParseTree t) 
		{
			my_string = t.getString(FIELD_STRING_MY_STRING,null);
			
			String my_string_explicit = t.getString(FIELD_STRING_MY_STRING_EXPLICIT, null);
			assertEquals(my_string,my_string_explicit);
		}

		
		public TypeName getTypeName() { return TYPE_NAME; }

		public void write(ObjectWriter writer) 
		{
			writer.writeString(FIELD_STRING_MY_STRING, my_string);
			writer.writeObject(FIELD_STRING_MY_STRING_EXPLICIT, my_string);
		}
		
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof StringFieldBoundaryTest)) return false;
			
			StringFieldBoundaryTest other = (StringFieldBoundaryTest)obj;
			
			if ( !Objects.equals(my_string, other.my_string) ) return false;
			
			return true;
		}
	}
	
    public void testStringField()
    {
    	testStringField(null);
    	
    	testStringField("Hello World");
    	testStringField("");
    	
    	
    	testStringField("Fisher & Paykel");
    	testStringField("{ foo : \"bar\" }");
    	
    	testStringField("{ foo : \"bar\" }");
    	
    	testStringField("<html></html>");
    	
    	testStringField("Hello There \u00a9");
    	
    	testStringField(String.format("Hello: %c", (char)0));
    	
    	// The acid string...
    	testStringField(PrimativeReadWriteTest.createNonBase64AcidString());
    	testStringField(PrimativeReadWriteTest.createAcidString());
    }
    
    private void testStringField(String value)
    {
    	testStringField(value,null);
    }
    
    private void testStringField(String value, Format print_diagnostics_in_format)
    {
    	StringFieldBoundaryTest obj = new StringFieldBoundaryTest();
		
		obj.my_string = value;
		
		testObject(obj, print_diagnostics_in_format);
    }
    
    private void testObject(Object obj, Format print_diagnostics_in_format)
    {
    	testObject(Format.XML_PRETTY_PRINT,obj, print_diagnostics_in_format);
    	testObject(Format.JSON_PRETTY_PRINT,obj, print_diagnostics_in_format);
    	
    	testObject(Format.XML,obj,print_diagnostics_in_format);
    	testObject(Format.JSON,obj,print_diagnostics_in_format);
    }
    
    private void testObject(Format format, Object obj, Format print_diagnostics_in_format)
    {
    	String serialized_data = ObjectWriter.serialize(format, obj);
    	assert(serialized_data != null);
    	
    	if ( format == print_diagnostics_in_format )
    	{
    		System.out.println(serialized_data);
    	}
    	
    	Object from_reader = ObjectParseTree.deserialize(serialized_data);
    	
    	assertEquals(obj,from_reader);
    }
}

