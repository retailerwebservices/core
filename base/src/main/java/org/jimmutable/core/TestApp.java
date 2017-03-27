package org.jimmutable.core;

import java.io.StringReader;

import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.Parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TestApp 
{
	static private int counter = 0;
	
	static public boolean primativeFunc(boolean default_value)
	{
		String primative_value = ++counter % 2 == 0 ? "true" : "false";
		
		if ( primative_value.equalsIgnoreCase("true") ) return true;
		if ( primative_value.equalsIgnoreCase("t") ) return true;
		if ( primative_value.equals("1") ) return true;
		
		if ( primative_value.equalsIgnoreCase("false") ) return false;
		if ( primative_value.equalsIgnoreCase("f") ) return false;
		if ( primative_value.equals("0") ) return false;
		
		return default_value;
	}
	
	static public Boolean objectFunc(Boolean default_value)
	{
		String primative_value = ++counter % 2 == 0 ? "true" : "false";
		
		if ( primative_value.equalsIgnoreCase("true") ) return true;
		if ( primative_value.equalsIgnoreCase("t") ) return true;
		if ( primative_value.equals("1") ) return true;
		
		if ( primative_value.equalsIgnoreCase("false") ) return false;
		if ( primative_value.equalsIgnoreCase("f") ) return false;
		if ( primative_value.equals("0") ) return false;
		
		return default_value;
	}
	
	static public void main(String args[]) throws Exception
	{
		/*Person p = new Person("Jim", "Kane");
		
		char ch = 0;
		
		String xml = ObjectWriterUtils.writeObject(Format.XML_PRETTY_PRINT, "", null);
		
		System.out.println(xml);
		
		JsonFactory jfactory = new JsonFactory();
		
		
		ReadTree tree = Parser.parse(new StringReader(xml), null);
		
		System.out.println(tree);
		
		String str = (String)tree.asObject(null);
		
		System.out.println("Str = "+str);*/
		
		try
		{
			throw new NullPointerException();
		}
		catch(Exception e)
		{
			throw new Exception("Read error",e);
		}
	}
}
