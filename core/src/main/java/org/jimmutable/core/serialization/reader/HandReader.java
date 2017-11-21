package org.jimmutable.core.serialization.reader;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.serialization.FieldName;


/**
 * TODO: Write a really good explanation of read semantics, including arrays, etc.
 * @author kanej
 *
 */
public class HandReader
{
	private ObjectParseTree tree;
	
	public HandReader(String json_or_xml_data)  throws SerializeException
	{
		tree = Parser.parse(json_or_xml_data);
	}
	
	private HandReader(ObjectParseTree tree)
	{
		this.tree = tree;
	}
	
	/**
	 * Read a child (specified by xpath). A read gets the object parse tree
	 * specified by xpath while simultaneously deleting it from the tree.
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return The object parse tree specified by xpath, default value if the tree
	 *         can not be found.
	 */
	private ObjectParseTree readChild(String xpath, ObjectParseTree default_value)
	{
		if ( xpath == null ) return default_value;
	
		try
		{
			String arr[] = xpath.split("/");
			
			ObjectParseTree cursor = tree;
			
			for ( int i = 0; i < arr.length-1; i++ )
			{
				cursor = cursor.findChild(new FieldName(arr[i]), null);
				if ( cursor == null ) return default_value;
			}
			
			return cursor.readChild(new FieldName(arr[arr.length-1]), default_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Create a new hand reader by reading the specified child
	 * 
	 * @param xpath
	 *            The path of the child to read
	 * @param default_value
	 *            The value to return in the event that the child can not be deleted
	 * @return The HandReader from the specified child, or default_value if the
	 *         child can not be read
	 */
	public HandReader read(String xpath, HandReader default_value)
	{ 
		if ( xpath == null ) return default_value;
		
		ObjectParseTree tmp = readChild(xpath,null);
		if ( tmp == null ) return default_value;
		
		return new HandReader(tmp);
	}
	
	/**
	 * Read the value of a given child (xpath) as a String (read deletes the child)
	 */
	public String readString(String xpath, String default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asString(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a boolean (read deletes the child)
	 */
	public Boolean readBoolean(String xpath, Boolean default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asBoolean(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a char (read deletes the child)
	 */
	public Character readCharacter(String xpath, Character default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asCharacter(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a byte (read deletes the child)
	 */
	public Byte readByte(String xpath, Byte default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asByte(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a short (read deletes the child)
	 */
	public Short readShort(String xpath, Short default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asShort(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a int (read deletes the child)
	 */
	public Integer readInt(String xpath, Integer default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asInteger(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a long (read deletes the child)
	 */
	public Long readLong(String xpath, Long default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asLong(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a float (read deletes the child)
	 */
	public Float readFloat(String xpath, Float default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asFloat(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a double (read deletes the child)
	 */
	public Double readDouble(String xpath, Double default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asDouble(default_value);
	}
	
	/**
	 * Read the value of a given child (xpath) as a object (read deletes the child)
	 */
	public Object readObject(String xpath, Object default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if ( child == null ) return default_value;
		return child.asObject(default_value);
	}
}
