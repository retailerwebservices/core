package org.jimmutable.core.serialization.writer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Base64;

import javax.xml.namespace.QName;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

/**
 * LowLevelWriter is a low level wrapper around Jackson's JsonGenerator.
 * 
 * Absent something *truly unusual*, you should *never* have any need of
 * LowLevelWriter in your code.
 * 
 * Use ObjectWriter instead.
 * 
 * Default error handling behavior for all low level IO function is trap all
 * exceptions (including IOExceptions) and then throw them on as chained
 * SerializeException(s)
 * 
 * @author jim.kane
 *
 */
public class LowLevelWriter 
{
	private Format format;  // required, the format being written,
	private JsonGenerator gen; // required, the JSON geneator
	
	/**
	 * Construct a low level writer
	 * 
	 * @param format The format to write data in
	 * @param out The output stream to write the data to
	 */
	public LowLevelWriter(Format format, OutputStream out)
	{
		this(format, new OutputStreamWriter(out));
	}
	
	/**
	 * Construct a low level writer
	 * 
	 * @param format The format to write data in
	 * @param writer The writer to write the data to
	 */
	public LowLevelWriter(Format format, Writer writer)
	{
		Validator.notNull(format);
		
		this.format = format;
		
		try
		{
			if ( format == Format.JSON || format == Format.JSON_PRETTY_PRINT )
			{
				JsonFactory jfactory = new JsonFactory();
				gen = jfactory.createGenerator(writer);
				
				if ( format == Format.JSON_PRETTY_PRINT )
					gen.useDefaultPrettyPrinter();
			}
			else
			{
				XmlFactory xfactory = new XmlFactory();
				ToXmlGenerator xgen = xfactory.createGenerator(writer);

				if ( format == Format.XML_PRETTY_PRINT )
					xgen.useDefaultPrettyPrinter();
				
				xgen.writeRaw("<?xml version='1.0' encoding='UTF-8'?>");
				xgen.setNextName(new QName("object"));
				
				gen = xgen;
			}
		}
		catch(Exception e)
		{
			throw new SerializeException("Error creating low level writer", e);
		}
	}
	
	/**
	 * Construct a LowLevelWriter that writes to a TokenBuffer
	 * 
	 * @param buffer
	 *            The token buffer to write to
	 */
	
	public LowLevelWriter(TokenBuffer buffer) 
	{
		this.format = Format.TOKEN_BUFFER;

		gen = buffer;
	}
	
	/**
	 * Write a field name
	 * 
	 * If field_name is FieldName.FIELD_ARRAY_ELEMENT, nothing is written
	 * 
	 * @param field_name
	 *            The field name to write
	 */
	public void writeFieldName(FieldName field_name)
	{
		try
		{
			Validator.notNull(field_name);
			
			if ( field_name.equals(FieldName.FIELD_ARRAY_ELEMENT) ) 
				return; // This is an array element, do not write the field name
			
			gen.writeFieldName(field_name.getSimpleName());
		}
		catch(Exception e)
		{
			throw new SerializeException("Serialization error", e);
		}
	}
	
	/**
	 * Write a null
	 */
	public void writeNull() 
	{
		try
		{
			if ( isXML() )
			{
				// in XML, we should basically never get there (as nulls are simply "omitted" from the output).  
				// But, if some weird way, we wind up here, we need to explicitly write the null object out...
				
				gen.writeStartObject();
				
				writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
				gen.writeString(TypeName.TYPE_NAME_NULL.getSimpleName());
				
				writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
				gen.writeNull();
				
				gen.writeEndObject();
			}
			else
			{
				gen.writeNull();
			}
		}
		catch(Exception e)
		{
			throw new SerializeException("Serialization error", e);
		}
	}
	
	/**
	 * Write a String.
	 *
	 * This function has been "battle tested" and str can be anything, include
	 * null and containing any character
	 * 
	 * @param str
	 *            The string to write
	 */
	public void writeString(String str) 
	{
		if ( str == null )
		{
			writeNull();
			return;
		}
		
		if ( isXML() && str.length() == 0 )
		{
			writeStringObject(str);
			return;
		}
		
		try
		{
			gen.writeString(str);
		}
		catch(Exception e)
		{
			throw new SerializeException("Serialization error", e);
		}
	}
	
	/**
	 * Test to see if the string can be written without base64 encoding
	 * 
	 * @param src
	 *            The string to test
	 * @return true if the string requires base64 encoding, false otherwise
	 */
	static public boolean isBase64Required(String src)
	{
		if ( src == null ) return false;
		
		char chars[] = src.toCharArray();
		
		boolean needs_strip = false;
		
		for ( char ch : chars )
		{
			if ( ch >= 32 && ch <= 10_000 ) continue;
			if ( ch == 9 ) continue; // tab
			if ( ch == 10 ) continue; // newline
			if ( ch == 13 ) continue; // carriage return;
			
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * base64 encode a String
	 * @param str The string to encode
	 * @return The base64 encoded string
	 */
	static public String base64EncodeString(String str)
	{
		Validator.notNull(str);
		return Base64.getEncoder().encodeToString(str.getBytes());
	}
	
	/**
	 * Write a boolean
	 * 
	 * @param value The boolean to write
	 */
	public void writeBoolean(boolean value)
	{
		try
		{
			gen.writeBoolean(value);
		}
		catch(Exception e)
		{
			throw new SerializeException("Serialization error", e);
		}
	}
	
	/**
	 * Write a character 
	 * 
	 * @param value
	 */
	public void writeChar(char value) 
	{ 
		writeString(String.format("%c", value)); 
	}
	
	/**
	 * Write a byte
	 * @param value
	 */
	public void writeByte(byte value) 
	{
		writeLong(value);
	}

	/**
	 * Write a short
	 * @param value
	 */
	public void writeShort(short value) 
	{ 
		writeLong(value); 
	}
	
	
	/**
	 * Write an integer
	 * @param value
	 */
	public void writeInt(int value) 
	{ 
		writeLong((long)value); 
	}
	
	
	/**
	 * Write a long
	 * @param value
	 */
	public void writeLong(long value)
	{
		try
		{
			gen.writeNumber(value);
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Write a float
	 * @param value
	 */
	public void writeFloat(float value)
	{
		try
		{
			gen.writeNumber(value);
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Write a double
	 * @param value
	 */
	public void writeDouble(double value)
	{
		try
		{
			gen.writeNumber(value);
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Start an object (must be matched with a cal lto closeObject())
	 */
	public void openObject()
	{
		try
		{
			gen.writeStartObject();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Close an object
	 */
	public void closeObject()
	{ 
		try
		{
			gen.writeEndObject();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Write a string in its complete object encoding (i.e. not as a JavaScript
	 * Primitive, but as a full object, with type_hint, etc.)
	 * 
	 * @param str
	 *            The string to write. Can be null, the empty string, or can
	 *            contain any character
	 */
	public void writeStringObject(String str)
	{
		try
		{
			gen.writeStartObject();
			
			writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
			gen.writeString(TypeName.TYPE_NAME_STRING.getSimpleName());
			
			if ( str != null && isBase64Required(str) )
			{
				writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE_BASE64);
				gen.writeString(this.base64EncodeString(str));
			}
			else
			{
				writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
				
				if ( str == null ) 
					gen.writeNull();
				else
					gen.writeString(str);
			}
			
			gen.writeEndObject();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Write an object.
	 * 
	 * Primitives (Strings, Integer, Double, etc.) will be written in complete
	 * object encoding (i.e. not as a JavaScript Primitive, but as a full
	 * object, with type_hint, etc.)
	 * 
	 * @param obj The object to write.  Can be null.
	 */
	public void writeObject(Object obj)
	{
		try
		{
			if ( obj == null )
			{
				writeNull();
				return;
			}
			
			if ( obj instanceof String )
			{
				String value = (String)obj;
				writeStringObject(value);
				return;
			}
			
			gen.writeStartObject();
			{
				if ( obj instanceof StandardWritable )
				{
					StandardWritable std = (StandardWritable)obj;
					
					TypeName type_name = std.getTypeName();
					
					if ( !ObjectParseTree.isTypeRegistered(type_name) )
					{
						System.err.println(String.format("WARNING! An object, %s, with type name %s is being written, but it is not registered with ObjectReader (meaning you will not be able to read/clone it)", 
								obj.getClass().getSimpleName(), 
								type_name.getSimpleName()));
						
						System.err.println("Register the class by calling ObjectReader.registerType");
					}
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(type_name.getSimpleName());
					
					std.write(new ObjectWriter(this));
				}
				
				else if ( obj instanceof Boolean )
				{
					Boolean value = (Boolean)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_BOOLEAN.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeBoolean(value.booleanValue());
				}
				
				else if ( obj instanceof Character )
				{
					Character value = (Character)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_CHAR.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeString(value.toString());
				}
				
				else if ( obj instanceof Byte )
				{
					Byte value = (Byte)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_BYTE.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeByte(value.byteValue());
				}
				
				else if ( obj instanceof Short )
				{
					Short value = (Short)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_SHORT.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeInt(value.shortValue());
				}
				
				else if ( obj instanceof Integer )
				{
					Integer value = (Integer)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_INT.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeInt(value.intValue());
				}
				
				else if ( obj instanceof Long )
				{
					Long value = (Long)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_LONG.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeLong(value.longValue());
				}
				
				else if ( obj instanceof Float )
				{
					Float value = (Float)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_FLOAT.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeFloat(value.floatValue());
				}
				
				else if ( obj instanceof Double )
				{
					Double value = (Double)obj;
					
					writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
					writeString(TypeName.TYPE_NAME_DOUBLE.getSimpleName());
					
					writeFieldName(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
					writeDouble(value.doubleValue());
				}
				
				else
				{
					throw new SerializeException(String.format("Attempt to serialize unknown type %s",obj.getClass()));
				}
				
				gen.writeEndObject();
			}
		}
		catch(SerializeException e )
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Begin an array (must be matched with a call to closeArray())
	 */
	public void openArray()
	{
		try
		{
			gen.writeStartArray();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Close a previously opened array
	 */
	public void closeArray() 
	{
		try
		{
			gen.writeEndArray();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Flush 
	 */
	public void flush()
	{
		try
		{
			gen.flush();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Close (prevents any further writing)
	 */
	public void close()
	{
		try
		{
			gen.close();
		}
		catch(Exception e)
		{
			throw new SerializeException("Low level write serialization error", e);
		}
	}
	
	/**
	 * Get the data format being written
	 * 
	 * @return The data format being written
	 */
	public Format getSimpleFormat() 
	{ 
		return format; 
	}
	
	/**
	 * Test to see if the format being written is a JSON format
	 * 
	 * @return True if the output is JSON (either pretty printed or regular),
	 *         false otherwise
	 */
	public boolean isJSON() {  return getSimpleFormat() == Format.JSON || getSimpleFormat() == Format.JSON_PRETTY_PRINT; }
	
	/**
	 * Test to see if the format being written is XML
	 * 
	 * @return True if the output is XML (either pretty printed or regular),
	 *         false otherwise
	 */
	public boolean isXML() { return getSimpleFormat() == Format.XML || getSimpleFormat() == Format.XML_PRETTY_PRINT; }
	
}
