package org.jimmutable.core.examples.hand_serialization;

import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.reader.HandReader;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.Parser;

public class SimpleHandReaderTest
{
	static public void main(String args[])
	{
		String json_string = "{ \"foo\":\"bar\", \"baz\":[2,3,5,7,11], \"quz\":{ \"first\":\"James\", \"last\":\"Kane\" } }";
		
		
		HandReader r = new HandReader(json_string);
		
		
		String foo = r.readString("foo", null);
		
		// Simple reading of a string
		System.out.println(String.format("foo = %s", foo));
		
		// Read an array of integers
		while(true)
		{
			Integer baz = r.readInt("baz", null);
			if ( baz == null ) break;
			
			System.out.println(baz);
		}
		
		// Read a child object...
		
		{
			String first_name = r.readString("quz/first", null);
			String last_name = r.readString("quz/last", null);
			
			System.out.println(String.format("quz.first = %s, quz.last = %s", first_name, last_name));
		}
		
		
	}
}
