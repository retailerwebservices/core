package org.jimmutable.core.serialization.reader;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.serialization.FieldName;

/**
 * Used for parsing JSON in our internal processing. This class expects valid
 * FieldName values for JSON names. This means that is a name/value pair is
 * {"myName" : "value"} an exception will be thrown since myName is not valid
 * FieldName (capital 'N') </br>
 * For usage examples see HandReaderTest
 * 
 * @see HandReaderTest
 * @author kanej, trevorbox
 *
 */
public class HandReader
{
	private ObjectParseTree tree;

	/**
	 * 
	 * Used for parsing JSON in our internal processing. This class expects valid
	 * FieldName values for JSON names. This means that if a name/value pair is
	 * {"myName" : "value"} an exception will be thrown since FieldName does not
	 * accept capital letters </br>
	 * For usage examples see HandReaderTest
	 * 
	 * @see HandReaderTest
	 * @param json_or_xml_data
	 *            The json or xml string
	 * @throws SerializeException
	 */
	public HandReader(String json_or_xml_data) throws SerializeException
	{
		tree = Parser.parse(json_or_xml_data);
	}

	/**
	 * 
	 * Used for parsing JSON in our internal processing. This class expects valid
	 * FieldName values for JSON names. This means that if a name/value pair is
	 * {"myName" : "value"} an exception will be thrown since FieldName does not
	 * accept capital letters </br>
	 *
	 * For usage examples see HandReaderTest
	 * 
	 * @see HandReaderTest
	 * @param tree
	 *            ObjectParseTree
	 */
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
		if (xpath == null)
			return default_value;

		try
		{
			String arr[] = xpath.split("/");

			ObjectParseTree cursor = tree;

			for (int i = 0; i < arr.length - 1; i++)
			{

				cursor = cursor.findChild(new FieldName(arr[i]), null);
				if (cursor == null)
					return default_value;
			}

			return cursor.readChild(new FieldName(arr[arr.length - 1]), default_value);
		} catch (Exception e)
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
		if (xpath == null)
			return default_value;

		ObjectParseTree tmp = readChild(xpath, null);
		if (tmp == null)
			return default_value;

		return new HandReader(tmp);
	}

	/**
	 * Read the value of a given child (xpath) as a String (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return String
	 */
	public String readString(String xpath, String default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asString(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a boolean (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Boolean
	 */
	public Boolean readBoolean(String xpath, Boolean default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asBoolean(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a char (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Character
	 */
	public Character readCharacter(String xpath, Character default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asCharacter(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a byte (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Byte
	 */
	public Byte readByte(String xpath, Byte default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asByte(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a short (read deletes the child)
	 * 
	 * * @param xpath The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Short
	 */
	public Short readShort(String xpath, Short default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asShort(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a int (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Integer
	 */
	public Integer readInt(String xpath, Integer default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asInteger(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a long (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Long
	 */
	public Long readLong(String xpath, Long default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asLong(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a float (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Float
	 */
	public Float readFloat(String xpath, Float default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asFloat(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a double (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Double
	 */
	public Double readDouble(String xpath, Double default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asDouble(default_value);
	}

	/**
	 * Read the value of a given child (xpath) as a StandardObject or primitive
	 * object (read deletes the child)
	 * 
	 * @param xpath
	 *            The location of the object parse tree to read
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return Object
	 */
	public Object readObject(String xpath, Object default_value)
	{
		ObjectParseTree child = readChild(xpath, null);
		if (child == null)
			return default_value;
		return child.asObject(default_value);
	}

	/**
	 * Interpret the current node as an Object (either StandardObject or a primitive
	 * object) Nothing is removed
	 * 
	 * @param default_value
	 *            The value to return if the tree can not be read
	 * @return The node as Object
	 */
	public Object asObject(Object default_value)
	{
		return tree.asObject(default_value);
	}

	/**
	 * Get the pretty print of what is in the ObjectParseTree
	 */
	public String toString()
	{
		return tree.toString();
	}
}
