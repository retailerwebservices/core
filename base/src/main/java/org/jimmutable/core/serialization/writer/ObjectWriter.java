package org.jimmutable.core.serialization.writer;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.utils.Validator;

import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * A writer used to serialize Objects (supports primitives and objects that
 * extend StandardObject) * Primavtes (String, null, Float, Integer, Double
 * etc.) have two forms:
 * 
 * complete object form
 * 
 * primitive form
 * 
 * For example, if I write field, my_field as a float, 0.2f, in primitive form
 * in JSON, the output is:
 * 
 * my_field : 0.2
 * 
 * In complete object form, again in JSON, the output is
 * 
 * my_field : { type_hint: "float", primative_value: 0.2f }
 * 
 * The complete object form is required for certain types of String (for
 * example, strings that contain ASCII control characters, which must be base64
 * encoded). Complete object form also enables, for example, a collection of
 * mixed primitives to be read using ReadAs.OBJECTS.
 * 
 * That being said, generally speaking, primitive form is preferred over
 * complete object form because of the ease of coding against it in JavaScript.
 * 
 * ObjectReader's various asXXX functions will auto-detect complete object vs
 * primitive form and return the right values for you all the time.
 * 
 * @author jim.kane
 *
 */
public class ObjectWriter 
{
	private LowLevelWriter writer;
	
	/**
	 * Construct an ObjectWriter
	 * 
	 * @param writer The low level writer to write to
	 * 
	 */
	protected ObjectWriter(LowLevelWriter writer)
	{
		this.writer = writer;
	}
	
	/**
	 * Write a null
	 * @param field_name The field name of the null
	 */
	public void writeNull(FieldName field_name)
	{
		if ( writer.isXML() ) return;  // in xml, a null is written by simply "not writing" the field...
		
		writer.writeFieldName(field_name);
		writer.writeNull();
	}
	
	/**
	 * Write a String.
	 * 
	 * This function will favor the primitive form (over complete object form)
	 * when possible
	 * 
	 * @param field_name
	 *            The field name of the String
	 * @param value
	 *            The value of the String
	 */
	public void writeString(FieldName field_name, String value)
	{
		if ( writer.isXML() && value == null ) return; // in xml, a null is written by simply "not writing" the field...
		
		writer.writeFieldName(field_name);
		
		if ( writer.isBase64Required(value) )
		{
			writer.writeStringObject(value);
		}
		else
		{
			writer.writeString(value);
		}
	}
	
	
	/**
	 * Convenience method for writing stringables. Equivalent to
	 * writeString(field_name, stringable.toString())
	 * 
	 * @param field_name
	 *            The field name of the stringable to be written
	 * @param stringable
	 *            The stringable to write.
	 */
	public void writeStringable(FieldName field_name, Stringable stringable)
	{
		Validator.notNull(field_name, stringable);
		writeString(field_name, stringable.toString());
	}
	
	/**
	 * Write a boolean in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeBoolean(FieldName field_name, boolean value) 
	{
		writer.writeFieldName(field_name);
		writer.writeBoolean(value);
	}
	
	/**
	 * Write a char in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeChar(FieldName field_name, char value) 
	{
		writer.writeFieldName(field_name);
		writer.writeChar(value);
	}
	
	/**
	 * Write a byte in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeByte(FieldName field_name, byte value) 
	{
		writer.writeFieldName(field_name);
		writer.writeByte(value);
	}
	
	/**
	 * Write a short in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeShort(FieldName field_name, short value) 
	{
		writer.writeFieldName(field_name);
		writer.writeShort(value);
	}
	
	/**
	 * Write a int in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeInt(FieldName field_name, int value) 
	{
		writer.writeFieldName(field_name);
		writer.writeInt(value);
	}
	
	/**
	 * Write a long in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeLong(FieldName field_name, long value) 
	{
		writer.writeFieldName(field_name);
		writer.writeLong(value);
	}
	
	/**
	 * Write a float in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeFloat(FieldName field_name, float value) 
	{
		writer.writeFieldName(field_name);
		writer.writeFloat(value);
	}
	
	/**
	 * Write a double in primitive form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeDouble(FieldName field_name, double value) 
	{
		writer.writeFieldName(field_name);
		writer.writeDouble(value);
	}
	
	/**
	 * Write an Object. Primitives (String, Double, Float, etc.) will be
	 * written in complete object form
	 * 
	 * @param field_name
	 * @param value
	 */
	public void writeObject(FieldName field_name, Object value)
	{
		if ( writer.isXML() && value == null ) return; // in xml, a null is written by simply "not writing" the field...
		
		writer.writeFieldName(field_name);
		writer.writeObject(value);
	}
	
	/**
	 * Start an object.  Must lated be closed with closeObject()
	 * 
	 * @param field_name
	 * @param type_name
	 */
	public void openObject(FieldName field_name, TypeName type_name)
	{
		Validator.notNull(field_name, type_name);
		
		writer.writeFieldName(field_name);
		
		writer.openObject();
		writer.writeFieldName(FieldName.FIELD_NAME_TYPE_HINT);
		writer.writeString(type_name.getSimpleName());
	}
	
	/**
	 * Close an object
	 */
	public void closeObject()
	{
		writer.closeObject();
	}
	
	/**
	 * Write a collection
	 * 
	 * @param field_name
	 *            The field name of the Collection
	 * @param c
	 *            The collection to write (may not be null, can be empty)
	 * @param write_as
	 *            How do you want each element in the collection written
	 *            (String? Number? Boolean? Object?)
	 */
	public void writeCollection(FieldName field_name, Collection c, WriteAs write_as)
	{
		Validator.notNull(field_name, c, write_as);

		
		writer.writeFieldName(field_name);
		writer.openArray();
		
		for ( Object obj : c )
		{
			write_as.writeObject(this, FieldName.FIELD_ARRAY_ELEMENT, obj);
		}
		
		writer.closeArray();
	}
	
	/**
	 * Write a map
	 * 
	 * @param field_name
	 *            The field name of the Map
	 * @param m
	 *            The map to write (may not be null, can be empty)
	 * @param write_keys_as
	 *            How do you want to write the keys (Strings? Objects? etc.)
	 * @param write_values_as
	 *            How do you want to write the values (Strings? Objects? etc.)
	 */
	public void writeMap(FieldName field_name, Map m, WriteAs write_keys_as, WriteAs write_values_as)
	{
		Validator.notNull(field_name, m, write_keys_as, write_values_as);
		
		writeCollection(field_name, m.entrySet(), new WriteAs.MapWriteAs(write_keys_as, write_values_as));
	}
	
	/**
	 * Get the data format being written
	 * 
	 * @return The data format being written
	 */
	public Format getSimpleFormat() 
	{ 
		return writer.getSimpleFormat(); 
	}
	
	/**
	 * Test to see if the format being written is a JSON format
	 * 
	 * @return True if the output is JSON (either pretty printed or regular),
	 *         false otherwise
	 */
	public boolean isJSON() { return writer.isJSON(); }
	
	/**
	 * Test to see if the format being written is XML
	 * 
	 * @return True if the output is XML (either pretty printed or regular),
	 *         false otherwise
	 */
	public boolean isXML() { return writer.isXML(); }
	
	/**
	 * Serialize an object, return a String
	 * 
	 * @param format
	 *            The format to serialize in
	 * @param obj
	 *            The object to serialize (can be null)
	 * @return obj serialized in the specified format
	 */
	static public String serialize(Format format, Object obj)
	{
		try
		{
			if ( obj == null )
			{
				obj = NullPrimative.NULL_PRIMATIVE;
			}
			
			StringWriter writer = new StringWriter();
			LowLevelWriter low_level_writer = new LowLevelWriter(format,writer);
			
			if ( obj instanceof String )
			{
				low_level_writer.writeStringObject((String)obj);
			}
			else
			{
				low_level_writer.writeObject(obj);
			}
			
			low_level_writer.close();
			
			writer.close();
			
			return writer.toString();
		}
		catch(SerializeException e)
		{
			throw e;
		}
		catch(Exception e2)
		{
			throw new SerializeException("Error while writing object: "+e2.getMessage(), e2);
		}
	}
	
	/**
	 * Serialize an object, return a TokenBuffer
	 * 
	 * @param obj
	 *            The object to serialize (can be null)
	 *            
	 * @return obj serialized in the specified format
	 */
	static public TokenBuffer serializeToTokenBuffer(Object obj)
	{
		try
		{
			if ( obj == null )
			{
				obj = NullPrimative.NULL_PRIMATIVE;
			}
			
			TokenBuffer ret = new TokenBuffer(null,false);
			
			LowLevelWriter low_level_writer = new LowLevelWriter(ret);
			
			if ( obj instanceof String )
			{
				low_level_writer.writeStringObject((String)obj);
			}
			else
			{
				low_level_writer.writeObject(obj);
			}
			
			low_level_writer.close();
			
			return ret;
		}
		catch(SerializeException e)
		{
			throw e;
		}
		catch(Exception e2)
		{
			throw new SerializeException("Error while writing object: "+e2.getMessage(), e2);
		}
	}
}
