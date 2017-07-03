package org.jimmutable.core.serialization.reader;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;

/**
 * The job of the Parser class is to convert XML/JSON/TokenBuffer data back into
 * ObjectParseTree objects as quickly as possible.
 * 
 * Generally speaking, unless you are working on Jimmutable itself, you do not
 * need to do anything with this class (perhaps, other than calling the static
 * convenience methods)
 * 
 * @author jim.kane
 *
 */
public class Parser 
{
	static private JsonFactory json_factory = null;
	static private XmlFactory xml_factor = null;
	
	private Format format;
	
	private FieldName last_field_name = FieldName.FIELD_DOCUMENT_ROOT;

	private ObjectParseTree result;
	
	private JsonParser json_parser;
	
	private Parser(Reader r) throws Exception
	{
		format = figureFormat(r);
		
		if ( format == Format.JSON || format == Format.JSON_PRETTY_PRINT )
		{
			if ( json_factory == null ) json_factory = new JsonFactory();
			json_parser = json_factory.createJsonParser(r);
		}
		else
		{
			if ( xml_factor == null ) xml_factor = new XmlFactory();
			json_parser = xml_factor.createJsonParser(r);
		}
		
		if ( json_parser == null )
			throw new SerializeException("Could not create a parser for the format "+format);
		
		result = processObjectTokens(FieldName.FIELD_DOCUMENT_ROOT);
		
		json_parser.close();
	}
	
	private Parser(TokenBuffer buffer) throws Exception
	{
		format = Format.TOKEN_BUFFER;
		json_parser = buffer.asParser();
		
		result = processObjectTokens(FieldName.FIELD_DOCUMENT_ROOT);
		
		json_parser.close();
	}
	
	private ObjectParseTree processObjectTokens(FieldName object_field_name) throws Exception
	{
		Stack<ObjectParseTree> stack = new Stack<>();
		
		ObjectParseTree root = new ObjectParseTree(object_field_name);
		stack.push(root);
		
		while(true)
		{
			JsonToken token = json_parser.nextToken();
			if ( token == null ) 
			{
				throw new SerializeException("Unexpected end of input");
			}
			
			
			switch(token)
			{
			case NOT_AVAILABLE: 
				throw new SerializeException("Unknown parse error");
			
			case START_OBJECT: 
				break; 
				
			case END_OBJECT:
				stack.pop(); 
				if ( stack.isEmpty() ) return root;
				break;
				
			case START_ARRAY:
				FieldName array_name = stack.peek().getSimpleFieldName(); // get the field name of the stub object
				stack.pop(); // pop out the "stub" object created by the preceding field event
				stack.peek().removeLast(); // Remove the "stub" object added to the current object by the preceding field event
				
				processArrayTokens(array_name, stack.peek());
				break; 
				
			case END_ARRAY: 
				throw new SerializeException("Unexpected end of array token"); 
				
			case FIELD_NAME:
				
				ObjectParseTree new_object = new ObjectParseTree(new FieldName(json_parser.getValueAsString()));
				stack.peek().add(new_object);
				
				stack.push(new_object);
				
				break;
			
			case VALUE_EMBEDDED_OBJECT: 
				throw new SerializeException("Unsupported value type (embedded object)");
			
			case VALUE_STRING:
			case VALUE_NUMBER_INT:
			case VALUE_NUMBER_FLOAT:
			case VALUE_TRUE:
			case VALUE_FALSE:
				
				stack.peek().setValue(json_parser.getValueAsString());
				stack.pop();
				
				break;
				
			case VALUE_NULL:
				
				stack.peek().setValue(null);
				stack.pop();
				
				break;
			}
		}
	}
	
	private void processArrayTokens(FieldName array_name, ObjectParseTree parent) throws Exception
	{
		while(true)
		{
			JsonToken token = json_parser.nextToken();
			if ( token == null ) 
				throw new SerializeException("Unexpected end of input");
			
			switch(token)
			{
			case NOT_AVAILABLE: 
				throw new SerializeException("Unknown parse error");
			
			case START_OBJECT: 
				parent.add(processObjectTokens(array_name));
				break; 
				
			case END_OBJECT:
				throw new SerializeException("End object token found while processing array");
								
			case START_ARRAY:
				throw new SerializeException("ERROR: Encountered start of array while in array");
				
			case END_ARRAY: 
				return; // done processing array!
				
			case FIELD_NAME:
				throw new SerializeException("ERROR: Encountered field name in array");
			
			case VALUE_EMBEDDED_OBJECT: 
				throw new SerializeException("Unsupported value type (embedded object)");
			
			case VALUE_STRING:
			case VALUE_NUMBER_INT:
			case VALUE_NUMBER_FLOAT:
			case VALUE_TRUE:
			case VALUE_FALSE:
				
				
				ObjectParseTree value_object = new ObjectParseTree(array_name);
				value_object.setValue(json_parser.getValueAsString());
				
				parent.add(value_object);
				
				break;
				
			case VALUE_NULL:
				
				ObjectParseTree null_object = new ObjectParseTree(array_name);
				null_object.setValue(null);
				
				parent.add(null_object);
				
				break;
			}
		}
	}

	
	private Format figureFormat(Reader reader)
	{
		try
		{
			// Make sure that reader supports mark and reset
			if ( !reader.markSupported()  )
			{
				reader = new BufferedReader(reader);
			}
	
			reader.mark(11);
			char buf[] = new char[10];
			int ar = reader.read(buf, 0, 10);
			reader.reset();
			
			String start = new String(buf,0,ar);
			start = start.trim();
			
			if ( start.startsWith("{") ) return Format.JSON;
			if ( start.startsWith("<") ) return Format.XML;
			
			
			throw new SerializeException("Unable to determine the input format: read 10 characters without a definitive answer");
		}
		catch(Exception e)
		{
			throw new SerializeException("Unable to determine the input format",e);
		}
	}
	
	/**
	 * Create an ObjectParseTree from a Reader
	 * 
	 * @param r
	 *            The reader to consume raw data from
	 * 
	 * @return The ObjectParseTree created from the data
	 * 
	 * @throws SerializeException
	 *             Any read errors (IO, parse, etc.) will be thrown as
	 *             SerializeException (the only exception that can come out of
	 *             this function)
	 */
	static public ObjectParseTree parse(Reader r) throws SerializeException
	{
		try
		{
			Parser p = new Parser(r);
			
			if ( p.result == null ) 
				throw new SerializeException("Unknown error while parsing ReadTree (null result)");
			
			return p.result;
		}
		catch(SerializeException e)
		{
			throw e;
		}
		catch(Exception e2)
		{
			throw new SerializeException("Error while parsing ReadTree",e2);
		}
	}
	
	/**
	 * Create an ObjectParseTree from a String
	 * 
	 * @param str
	 *            The string to consume raw data from
	 * 
	 * @return The ObjectParseTree created from the data
	 * 
	 * @throws SerializeException
	 *             Any read errors (IO, parse, etc.) will be thrown as
	 *             SerializeException (the only exception that can come out of
	 *             this function)
	 */
	static public ObjectParseTree parse(String str) throws SerializeException
	{
		StringReader r = new StringReader(str);
		return parse(r);
	}
	
	/**
	 * Create an ObjectParseTree from a TokenBuffer
	 * 
	 * @param buffer
	 *            The TokenBuffer to consume raw data from
	 * 
	 * @return The ObjectParseTree created from the data
	 * 
	 * @throws SerializeException
	 *             Any read errors (IO, parse, etc.) will be thrown as
	 *             SerializeException (the only exception that can come out of
	 *             this function)
	 */
	static public ObjectParseTree parse(TokenBuffer buffer) throws SerializeException
	{
		try
		{
			Parser p = new Parser(buffer);
			
			if ( p.result == null ) 
				throw new SerializeException("Unknown error while parsing ReadTree (null result)");
			
			return p.result;
		}
		catch(SerializeException e)
		{
			throw e;
		}
		catch(Exception e2)
		{
			throw new SerializeException("Error while parsing ReadTree",e2);
		}
	}
}
