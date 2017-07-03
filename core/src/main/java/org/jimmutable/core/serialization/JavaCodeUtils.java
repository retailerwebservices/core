package org.jimmutable.core.serialization;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.utils.Validator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Various static utility functions that are helpful when writing unit test etc. in Java
 * @author jim.kane
 *
 */
public class JavaCodeUtils 
{
	/**
	 * Lets say you have a string that contains special characters (e.g. quotes,
	 * newlines, etc.) and you want to turn it into a Java String literal (i.e.
	 * something you can copy paste into java). toJavaStringLiteral is your
	 * answer!
	 * 
	 * @param src
	 *            Any valid java string
	 * @return A java string literal, ready to copy/paste into java source code
	 *         without compilation errors
	 */
	
	static public String toJavaStringLiteral(String src)
	{
		Validator.notNull(src);
		
		if ( src.contains("\n") ) return toJavaStringLiteralMultiLine(src);
		if ( src.contains("\r") ) return toJavaStringLiteralMultiLine(src);
		
		return toJavaStringLiteralSingleLine(src);
	}
	
	static private String toJavaStringLiteralSingleLine(String src)
	{
		Validator.notNull(src);
		
		StringBuilder ret = new StringBuilder("\"");
		
		for ( char ch : src.toCharArray() )
		{
			if ( ch == '\t' ) { ret.append("\\t"); continue; }
			if ( ch == '\b' ) { ret.append("\\b"); continue; }
			if ( ch == '\n' ) { ret.append("\\n"); continue; }
			if ( ch == '\r' ) { ret.append("\\r"); continue; }
			if ( ch == '\f' ) { ret.append("\\f"); continue; }
			if ( ch == '\'' ) { ret.append("\\'"); continue; }
			if ( ch == '\"' ) { ret.append("\\\""); continue; }
			if ( ch == '\\' ) { ret.append("\\\\"); continue; }
		
			if ( ch >= ' ' && ch <= '~' ) { ret.append(ch); continue; } // quotes etc. are in this range, but are caught by the above checks...
			
			ret.append(String.format("\\u%04X", (int)ch));
		}
		
		ret.append("\"");
		
		return ret.toString();
	}
	
	static private String toJavaStringLiteralMultiLine(String src)
	{
		Validator.notNull(src);
		
		List<String> lines = new ArrayList<>();
		
		StringBuilder format_string = new StringBuilder();
		StringBuilder cur_line = new StringBuilder();
		
		for ( char ch : src.toCharArray() )
		{
			if ( ch == '\n' || ch == '\r' )
			{
				if ( cur_line.length() != 0 )
				{
					format_string.append("%s");
					lines.add(cur_line.toString());
					
					cur_line = new StringBuilder();
				}
				
				if ( ch == '\n' ) format_string.append("\\n");
				if ( ch == '\r' ) format_string.append("\\r");
				continue;
			}
			
			cur_line.append(ch);
		}
		
		if ( cur_line.length() != 0 )
		{
			format_string.append("%s");
			lines.add(cur_line.toString());
		}
		
		
		StringBuilder ret = new StringBuilder("String.format(\"");
		ret.append(format_string.toString());
		ret.append("\"");
		
		for ( String line : lines )
		{
			ret.append("\n");
			ret.append("     , ");
			ret.append(toJavaStringLiteralSingleLine(line));
		}
		
		ret.append("\n)");
		
		return ret.toString();
	}
	
	/**
	 * Conventience method that takes a StandardObject, pretty prints its XML
	 * and then "serializes" the object to java code (rather useful when writing
	 * unit tests...)
	 * 
	 * @param obj The object to serialize to java code
	 * 
	 * @return Java code that will construct obj
	 */
	static public String toJavaStringLiteral(StandardObject<?> obj)
	{
		Validator.notNull(obj);
		
		String xml = obj.serialize(Format.XML_PRETTY_PRINT);
		if ( xml == null ) throw new ValidationException("Object did not serialize to valid XML");
		
		return toJavaStringLiteral(xml);
	}
	
	/**
	 * Take some XML and "pretty print" it (indent, newlines etc.)
	 * 
	 * @param xml
	 *            The XML to pretty print
	 * @param default_value
	 *            The value to return if an error is encountered (i.e. the XML
	 *            is invalid etc.). Frequently, this is either null or xml
	 *            (which, effectively means, hey, try and pretty print this --
	 *            if you can't, do nothing)
	 * @return The pretty printed XML, or default value if this can not be
	 *         achieved
	 */
	static public String prettyPrintXML(String xml, String default_value)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			
			return result.getWriter().toString();
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
}
